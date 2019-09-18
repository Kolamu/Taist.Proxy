package com.taist.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import com.taist.helper.ProxyHelper;
import com.taist.message.Channel;
import com.taist.message.MessageBus;

public final class ReceiverManager {
	protected int port;
	private ServerSocketChannel channel = null;
	private ReceiverHandler handler;
	private boolean running = false;
	private ArrayList<Channel> channellist = new ArrayList<Channel>();
 	public ReceiverManager(int port) {
		this.port = port;
		try {
			this.channel = ServerSocketChannel.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler = new ReceiverHandler();
	}
	
	public void receive() {
		running = true;
		handler.start();
	}
	
	public void close() {
		running = false;
		ProxyHelper.safeClose(channel);
	}
	
	private class ReceiverHandler extends Thread {
		public ReceiverHandler() {
			setDaemon(true);
		}

		@Override
		public void run() {
			while(running) {
				try {
					SocketChannel client = channel.accept();
					/*Channel channel = new Channel(new BaseReceiver(client));
					channellist.add(channel);
					MessageBus.registerChannel(channel);*/
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			MessageBus.shutdown();
		}
	}
}
