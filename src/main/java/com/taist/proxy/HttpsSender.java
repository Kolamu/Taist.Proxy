package com.taist.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public class HttpsSender extends BaseSender {
	public HttpsSender(String host, int port) {
		super(host, port);
	}
	
	private ResponseBody sendWild(RequestBody request) {
		try {
			InputStream input = socket.getInputStream();
	        OutputStream output = socket.getOutputStream();
	
	        BufferedInputStream bis = new BufferedInputStream(input);
	        BufferedOutputStream bos = new BufferedOutputStream(output);
	
	        bos.write("Hello".getBytes());
	        bos.flush();
	
	        byte[] buffer = new byte[20];
	        int length = bis.read(buffer);
	        System.out.println(new String(buffer, 0, length));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void createSSLSocket (String host, int port) throws Exception {
		 SSLContext ctx = SSLContext.getInstance("SSL");

	     KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	     TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

	     KeyStore ks = KeyStore.getInstance("JKS");
	     KeyStore tks = KeyStore.getInstance("JKS");

	     ks.load(new FileInputStream("cert/kclient.ks"), "clientpass".toCharArray());
	     tks.load(new FileInputStream("cert/tclient.ks"), "clientpublicpass".toCharArray());

	     kmf.init(ks, "clientpass".toCharArray());
	     tmf.init(tks);

	     ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
	     this.socket = (SSLSocket) ctx.getSocketFactory().createSocket(host, port);
	}

	@Override
	public void execute(RequestBody request) {
	    if(socket == null) {
	    	return;
	    }
	    
		OutputStream output = null;
		try {
			output = socket.getOutputStream();
			output.write(request.getData());
			output.flush();
		} catch (Exception e) {
		}
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public ResponseBody getDefaultResponse() {
		return default_resp;
	}
}
