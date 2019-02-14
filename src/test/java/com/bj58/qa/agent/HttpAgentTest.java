package com.bj58.qa.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

import com.taist.message.ChannelFilter;
import com.taist.proxy.DefaultProxy;

public class HttpAgentTest {
	public static void main(String[] args) {
		testStartProxy();
		//testPattern();
		//testBaidu();
		//testSocket();
		//System.out.println(ByteHelper.toInt(new byte[] {0x04, (byte) 0xed}));
	}
	
	static void testStartProxy() {
		//ChannelFilter.add("google");
		//ChannelFilter.add("youtube");
		ChannelFilter.add("baidu");
		DefaultProxy agent = new DefaultProxy();
		agent.start();
		try {
			Thread.sleep(610000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		agent.stop();
	}
	
	static void testPattern() {
		String s = "CONNECT clients1.google.com:443 HTTP/1.1\r\n" + 
				"Host: clients1.google.com:443\r\n" + 
				"Proxy-Connection: keep-alive\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36\r\n" + 
				"";
		System.out.println(Pattern.compile("").matcher(s).matches());
	}
	
	static void testBaidu() {
		try {
			Socket socket = new Socket("www.baidu.com", 443);
			socket.getOutputStream().write(new byte[] {1,2});
			socket.getOutputStream().flush();
			InputStream is = socket.getInputStream();
			sleep(2000);
			System.out.println(is.available());
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void testSocket() {
		boolean run = false;
		new Thread(new Runnable() {
			
			public void run() {
				try {
					ServerSocket server = new ServerSocket(8899);
					Socket socket = server.accept();
					for(int i=0;i< 10;i++) {
						int a = socket.getInputStream().available();
						if(a > 0) {
							socket.getInputStream().read();
						}
						System.out.println(a);
						sleep(1000);
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try {
					Socket socket = new Socket("127.0.0.1", 8899);
					sleep(2000);
					socket.getOutputStream().write("a".getBytes());
					sleep(1000);
					socket.getOutputStream().flush();
					sleep(1000);
					socket.close();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		sleep(10000);
	}
	private static void sleep(int m) {
		try {
			Thread.sleep(m);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
