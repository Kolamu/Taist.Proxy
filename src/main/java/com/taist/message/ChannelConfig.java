package com.taist.message;

import com.taist.factory.SenderFactory;
import com.taist.factory.SolverFactory;
import com.taist.proxy.Receiver;
import com.taist.proxy.Sender;
import com.taist.proxy.Solver;

public final class ChannelConfig {
	private Receiver reciever = null;
	private boolean https = false;
	private boolean running = false;
	private String serverHost = null;
	private int serverPort = 0;
	private String clientHost = null;
	private int clientPort = 0;
	
	public Receiver getReciever() {
		return reciever;
	}
	public ChannelConfig setReciever(Receiver reciever) {
		this.reciever = reciever;
		return this;
	}
	public Solver getSolver() {
		return SolverFactory.getSolver(this);
	}
	public Sender getSender() {
		return SenderFactory.getSender(this);
	}
	public boolean isHttps() {
		return https;
	}
	public ChannelConfig setHttps(boolean https) {
		this.https = https;
		return this;
	}
	public String getServerHost() {
		return serverHost;
	}
	public ChannelConfig setServerHost(String serverHost) {
		this.serverHost = serverHost;
		return this;
	}
	public int getServerPort() {
		return serverPort;
	}
	public ChannelConfig setServerPort(int serverPort) {
		this.serverPort = serverPort;
		return this;
	}
	public String getClientHost() {
		return clientHost;
	}
	public ChannelConfig setClientHost(String clientHost) {
		this.clientHost = clientHost;
		return this;
	}
	public int getClientPort() {
		return clientPort;
	}
	public ChannelConfig setClientPort(int clientPort) {
		this.clientPort = clientPort;
		return this;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	@Override
	public boolean equals(Object obj) {
		ChannelConfig conf = null;
		if(obj instanceof ChannelConfig) {
			conf = (ChannelConfig)obj;
		}
		if(conf == null) {
			return false;
		}
		
		if(conf.getClientHost() != getClientHost()
				&&
		  !conf.getClientHost().equals(getClientHost())) return false;
		if(conf.getClientPort() != getClientPort()) return false;
		if(conf.getServerHost() != getServerHost()
				&&
		  !conf.getServerHost().equals(getServerHost())) return false;
		if(conf.getServerPort() != getServerPort()) return false;
		
		return true;
	}
}
