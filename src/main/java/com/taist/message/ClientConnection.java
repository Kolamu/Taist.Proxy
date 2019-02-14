package com.taist.message;

import com.taist.proxy.Receiver;
import com.taist.proxy.Sender;
import com.taist.proxy.Solver;

public class ClientConnection implements SocketConnection {
	private Receiver receiver = null;
	private Sender sender = null;
	private Solver solver = null;
	private ChannelConfig config = null;
	public ClientConnection(ChannelConfig config) {
		this.config = config;
		this.receiver = config.getReciever();
		this.sender = config.getSender();
		this.solver = config.getSolver();
	}
	@Override
	public void run() {
		config.setRunning(true);
		while(config.isRunning()) {
			RequestBody request = receiver.receive();
			if(request == null) break;
			request = solver.solveRequest(request);
			sender.execute(request);
		}
	}
	
	@Override
	public boolean isClosed() {
		return !config.isRunning();
	}
	
	@Override
	public void close() {
		config.setRunning(false);
		receiver.close();
	}
}
