package com.taist.message;

import com.taist.proxy.Receiver;
import com.taist.proxy.Sender;
import com.taist.proxy.Solver;

public class ServerConnection implements SocketConnection {
	private Receiver receiver = null;
	private Sender sender = null;
	private Solver solver = null;
	private ChannelConfig config = null;
	public ServerConnection(ChannelConfig config) {
		this.config = config;
		this.receiver = config.getReciever();
		this.sender = config.getSender();
		this.solver = config.getSolver();
	}
	@Override
	public void run() {
		config.setRunning(true);
		while(config.isRunning()) {
			ResponseBody response = null;
			response = sender.receive();
			
			response = solver.solveResponse(response);

			receiver.execute(response);
			if(response == ResponseBody.RSP_503) {
				break;
			}
		}
	}
	@Override
	public boolean isClosed() {
		return !config.isRunning();
	}
	@Override
	public void close() {
		config.setRunning(false);
		sender.close();
	}
}
