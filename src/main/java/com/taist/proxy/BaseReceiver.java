package com.taist.proxy;

import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.taist.helper.ProxyHelper;
import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public class BaseReceiver implements Receiver {
	private Socket socket = null;
	private boolean running = true;
	
	public BaseReceiver(Socket socket) {
		this.socket = socket;
		try {
			socket.setSoTimeout(0);
			socket.setKeepAlive(true);
			socket.setReceiveBufferSize(81920);
			socket.setSendBufferSize(81920);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void execute(ResponseBody response) {
		if(socket == null) return;
		OutputStream output = null;
		try {
			output = socket.getOutputStream();
			output.write(response.getData());
			output.flush();
		} catch (Exception e) {
//			socket = null;
//			ProxyHelper.safeClose(socket);
		}
	}

	@Override
	public Socket getSocket() {
		return socket;
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
	public RequestBody receive() {
		try {
			byte[] data = readBytes();
			if(data != null && data.length != 0) {
				return new RequestBody(data);
			}
		}
		catch (InterruptedException e) {
			ProxyHelper.safeClose(socket);
		}
		catch (Exception e) {
			ProxyHelper.safeClose(socket);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		running = false;
	}
}
