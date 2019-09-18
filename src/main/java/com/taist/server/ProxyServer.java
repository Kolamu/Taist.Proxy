package com.taist.server;

import com.taist.message.MessageBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static com.taist.helper.ProxyHelper.safeClose;

public final class ProxyServer {
	protected int port;
	private ServerSelector selector = new ServerSelector();
	private ServerHandler handler = new ServerHandler();
	private boolean running = false;
	public ProxyServer() {
		this(8008);
	}
 	public ProxyServer(int port) {
		this.port = port;
	}
	
	public void start() {
 		if(running) {
 			return;
		}
		running = true;
		handler.start();
		selector.start();
	}
	
	public void close() {
		running = false;
		handler.close();
		selector.close();
	}
	
	private class ServerHandler extends Thread {
 		private ServerSocketChannel channel = null;
		public ServerHandler() {
			setDaemon(true);
		}

		@Override
		public void run() {
			init();
			while(running) {
				try {
					SocketChannel client = channel.accept();
					ProxySession session = new ProxySession(client);
					selector.register(client, session);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			MessageBus.shutdown();
		}

		private void init() {
			try {
				channel = ServerSocketChannel.open();
				channel.bind(new InetSocketAddress(port));
				channel.socket().setReuseAddress(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void close() {
			safeClose(channel);
		}
	}

	private class ServerSelector extends Thread {
		private Selector selector = null;
		public ServerSelector() {
			setDaemon(true);
		}

		@Override
		public void run() {
			init();
			while(running) {
				try {
					int channels = selector.select();
					if(channels == 0) continue;
					Set<SelectionKey> keys = selector.keys();
					Iterator<SelectionKey> keyIterator = keys.iterator();
					while(keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();
						ProxySession session = (ProxySession)key.attachment();
						if(key.isReadable()) {
							System.out.println("readable");
							session.read((SocketChannel) key.channel());
						}
						else if (key.isWritable()) {
							if(session.busy()) {
								continue;
							}
							session.write((SocketChannel) key.channel());
						}
						else {
							System.out.println("未知Selector类型");
						}
						keyIterator.remove();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void init() {
			try {
				selector = Selector.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void close() {
			selector.wakeup();
			safeClose(selector);
		}

		public void register(SocketChannel client, ProxySession session) {
			try {
				client.configureBlocking(false);
				client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
