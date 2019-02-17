package com.taist.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.taist.helper.ProxyHelper;
import com.taist.message.Channel;
import com.taist.message.MessageBus;

public final class ReceiverManager {
	protected int port;
	private ServerSocket socket = null;
	private ReceiverHandler handler;
	private boolean running = false;
	private ArrayList<Channel> channellist = new ArrayList<Channel>();
 	public ReceiverManager(int port) {
		this.port = port;
		try {
			this.socket = new ServerSocket(port);
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
		ProxyHelper.safeClose(socket);

	}
	
	private class ReceiverHandler extends Thread {
		public ReceiverHandler() {
			
			super(new Runnable() {
				public void run() {
					while(running) {
						try {
							Socket clientSocket = socket.accept();
							Channel channel = new Channel(new BaseReceiver(clientSocket));
							channellist.add(channel);
							MessageBus.registerChannel(channel);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					MessageBus.shutdown();
				}
			});
			setDaemon(true);
		}
	}
}
