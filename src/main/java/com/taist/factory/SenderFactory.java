package com.taist.factory;

import java.util.concurrent.ConcurrentHashMap;

import com.taist.message.ChannelConfig;
import com.taist.proxy.HttpSender;
import com.taist.proxy.HttpsSender;
import com.taist.proxy.Sender;

public final class SenderFactory {
	private static ConcurrentHashMap<String, Sender> senderMap = new ConcurrentHashMap<String, Sender>();
	private SenderFactory() { }
	public static Sender getSender(String key) {
		return senderMap.get(key);
	}
	
	public static Sender getSender(ChannelConfig config) {
		String key = config.getServerHost()
				+ ":" + config.getServerPort()
				+ "_" + config.getClientHost()
				+ ":" + config.getClientPort();
		if(senderMap.containsKey(key)) {
			return senderMap.get(key);
		}
		Sender sender = null;
		if(config.isHttps()) {
			sender = new HttpsSender(config.getServerHost(), config.getServerPort());
		}
		else {
			sender = new HttpSender(config.getServerHost(), config.getServerPort());
		}
		senderMap.put(key, sender);
		return sender;
	}
	
	public static void register(String key, Sender sender) {
		senderMap.put(key, sender);
	}
}
