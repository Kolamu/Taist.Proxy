package com.taist.helper;

import java.io.Closeable;
import java.io.InputStream;
import java.net.Socket;

import com.taist.message.ResponseBody;

public class ProxyHelper {

	public static void safeClose(Closeable close) {
		if(close != null) {
			try {
				close.close();
			}
			catch(Throwable t) {
				
			}
		}
	}
	
	public static byte[] read(Socket socket) throws Exception {
		return read(socket.getInputStream());
	}
	
	public static byte[] read(InputStream input) throws Exception {
		byte[] data = new byte[0];
		byte[] buff = new byte[1024];
		int rc = 0;
		int available = input.available();
		while(available > 0) {
			rc = input.read(buff);
			if(rc < 0) {
				return ResponseBody.RSP_503.getData();
			}
			data = ByteHelper.concat(data, ByteHelper.subarray(buff, 0, rc));
			available = input.available();
		}
		return data;
	}
	
	public static void sleep(int millionSeconds) {
		try {
			Thread.sleep(millionSeconds);
		} catch (InterruptedException e) {
		}
	}
}
