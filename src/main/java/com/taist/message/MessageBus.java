package com.taist.message;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageBus {
	private static ArrayList<SocketConnection> connectionPool = new ArrayList<SocketConnection>();
	private final static ThreadPoolExecutor channelExecutor = new ThreadPoolExecutor(
			40,
			50,
			600,
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());
	
	private final static ThreadPoolExecutor contentExecutor = new ThreadPoolExecutor(
			200,
			300,
			600,
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());
	
	public static void registerChannel(Channel channel) {
		channelExecutor.execute(channel);
	}
	
	public static void registerConnection(SocketConnection connection) {
		connectionPool.add(connection);
		contentExecutor.execute(connection);
	}
	
	public static void shutdown() {
		channelExecutor.shutdown();
		contentExecutor.shutdown();
		for(SocketConnection conn : connectionPool) {
			conn.close();
		}
	}
}
