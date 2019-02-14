package com.taist.proxy;

public final class DefaultProxy {
	private final static int DEF_PORT = 808;
	private ReceiverManager receiverManager = null;
	
	public DefaultProxy() {
		this(DEF_PORT);
	}
	
	public DefaultProxy(int port) {
		this(new ReceiverManager(DEF_PORT));
	}
	
	public DefaultProxy(ReceiverManager receiverManager) {
		this.receiverManager = receiverManager;
	}
	
	public void start() {
		receiverManager.receive();
	}
	
	public void stop() {
		receiverManager.close();
	}
}
