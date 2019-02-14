package com.taist.proxy;

import java.io.OutputStream;
import java.net.Socket;

import com.taist.helper.ProxyHelper;
import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public abstract class BaseSender implements Sender {
	protected Socket socket = null;
	private boolean running = true;
	protected ResponseBody default_resp = ResponseBody.RSP_503;
	
	public BaseSender(String host, int port) {
		for(int i=0;i<3;i++) {
			try {
				socket = new Socket(host, port);
				socket.setReceiveBufferSize(81920);
				socket.setSendBufferSize(81920);
				break;
			} catch (Exception e) {
				this.default_resp = ResponseBody.RSP_503;
				System.out.println("connect " + host + ":" + port + "failed. retry...");
			}
		}
	}
	
	@Override
	public Socket getSocket() {
		return socket;
	}
	
	@Override
	public void close() {
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void execute(RequestBody request) {
		if(socket == null) {
			return;
		}
		if(socket.isClosed()){
			System.out.println("socket is closed");
		}
		OutputStream output = null;
		try {
			output = socket.getOutputStream();
			output.write(request.getData());
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ResponseBody getDefaultResponse() {
		return default_resp;
	}

	protected byte[] readBytes() throws Exception {
		while(socket.getInputStream().available() == 0) {
			if(!running) {
				throw new InterruptedException();
			}
			ProxyHelper.sleep(50);
		}
		return ProxyHelper.read(socket);
	}

	@Override
	public ResponseBody receive() {
		try {
			byte[] data = readBytes();
			if(data != null && data.length != 0) {
				return new ResponseBody(data);
			}
		}
		catch (InterruptedException e) {
			ProxyHelper.safeClose(socket);
		}
		catch (Exception e) {
			ProxyHelper.safeClose(socket);
			e.printStackTrace();
		}
		return default_resp;
	}
}
