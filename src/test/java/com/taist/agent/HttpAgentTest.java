package com.taist.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import com.taist.message.ChannelFilter;
import com.taist.proxy.DefaultProxy;
import com.taist.server.ProxyServer;

public class HttpAgentTest {
	public static void main(String[] args) {
		//save();
		System.setProperty("javax.net.debug", "all");
		testStartProxy();
		//testPattern();
		//testBaidu();
		//testSocket();
		//System.out.println(ByteHelper.toInt(new byte[] {0x04, (byte) 0xed}));
		//System.out.println((null + "").length());
	}
	
	static void testStartProxy() {
		//ChannelFilter.add("google");
		//ChannelFilter.add("youtube");
		//ChannelFilter.add("baidu");
		ProxyServer agent = new ProxyServer();
		agent.start();
		ProxyTester tester = new ProxyTester();
		tester.run();
		agent.close();
	}
	
	static void testPattern() {
		String s = "CONNECT clients1.google.com:443 HTTP/1.1\r\n" + 
				"Host: clients1.google.com:443\r\n" + 
				"Proxy-Connection: keep-alive\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36\r\n" + 
				"";
		System.out.println(Pattern.compile("CONNECT").matcher(s).matches());
	}
	
	static void sslEngin() {
		try {
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
		    
		    SSLEngine engine = ctx.createSSLEngine();
		    engine.setUseClientMode(false);
		    engine.beginHandshake();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SSLEngine engin = null;
	}
	
	static void createCert() {
		
	}
	
	static void save() {
		String s = "30 82 04 69\r\n" + 
				"30 82 03 51 A0 03 02 01 02 02 0B 04 00 00 00 00\r\n" + 
				"01 44 4E F0 42 47 30 0D 06 09 2A 86 48 86 F7 0D\r\n" + 
				"01 01 0B 05 00 30 57 31 0B 30 09 06 03 55 04 06\r\n" + 
				"13 02 42 45 31 19 30 17 06 03 55 04 0A 13 10 47\r\n" + 
				"6C 6F 62 61 6C 53 69 67 6E 20 6E 76 2D 73 61 31\r\n" + 
				"10 30 0E 06 03 55 04 0B 13 07 52 6F 6F 74 20 43\r\n" + 
				"41 31 1B 30 19 06 03 55 04 03 13 12 47 6C 6F 62\r\n" + 
				"61 6C 53 69 67 6E 20 52 6F 6F 74 20 43 41 30 1E\r\n" + 
				"17 0D 31 34 30 32 32 30 31 30 30 30 30 30 5A 17\r\n" + 
				"0D 32 34 30 32 32 30 31 30 30 30 30 30 5A 30 66\r\n" + 
				"31 0B 30 09 06 03 55 04 06 13 02 42 45 31 19 30\r\n" + 
				"17 06 03 55 04 0A 13 10 47 6C 6F 62 61 6C 53 69\r\n" + 
				"67 6E 20 6E 76 2D 73 61 31 3C 30 3A 06 03 55 04\r\n" + 
				"03 13 33 47 6C 6F 62 61 6C 53 69 67 6E 20 4F 72\r\n" + 
				"67 61 6E 69 7A 61 74 69 6F 6E 20 56 61 6C 69 64\r\n" + 
				"61 74 69 6F 6E 20 43 41 20 2D 20 53 48 41 32 35\r\n" + 
				"36 20 2D 20 47 32 30 82 01 22 30 0D 06 09 2A 86\r\n" + 
				"48 86 F7 0D 01 01 01 05 00 03 82 01 0F 00 30 82\r\n" + 
				"01 0A 02 82 01 01 00 C7 0E 6C 3F 23 93 7F CC 70\r\n" + 
				"A5 9D 20 C3 0E 53 3F 7E C0 4E C2 98 49 CA 47 D5\r\n" + 
				"23 EF 03 34 85 74 C8 A3 02 2E 46 5C 0B 7D C9 88\r\n" + 
				"9D 4F 8B F0 F8 9C 6C 8C 55 35 DB BF F2 B3 EA FB\r\n" + 
				"E3 56 E7 4A 46 D9 13 22 CA 36 D5 9B C1 A8 E3 96\r\n" + 
				"43 93 F2 0C BC E6 F9 E6 E8 99 C8 63 48 78 7F 57\r\n" + 
				"36 69 1A 19 1D 5A D1 D4 7D C2 9C D4 7F E1 80 12\r\n" + 
				"AE 7A EA 88 EA 57 D8 CA 0A 0A 3A 12 49 A2 62 19\r\n" + 
				"7A 0D 24 F7 37 EB B4 73 92 7B 05 23 9B 12 B5 CE\r\n" + 
				"EB 29 DF A4 14 02 B9 01 A5 D4 A6 9C 43 64 88 DE\r\n" + 
				"F8 7E FE E3 F5 1E E5 FE DC A3 A8 E4 66 31 D9 4C\r\n" + 
				"25 E9 18 B9 89 59 09 AE E9 9D 1C 6D 37 0F 4A 1E\r\n" + 
				"35 20 28 E2 AF D4 21 8B 01 C4 45 AD 6E 2B 63 AB\r\n" + 
				"92 6B 61 0A 4D 20 ED 73 BA 7C CE FE 16 B5 DB 9F\r\n" + 
				"80 F0 D6 8B 6C D9 08 79 4A 4F 78 65 DA 92 BC BE\r\n" + 
				"35 F9 B3 C4 F9 27 80 4E FF 96 52 E6 02 20 E1 07\r\n" + 
				"73 E9 5D 2B BD B2 F1 02 03 01 00 01 A3 82 01 25\r\n" + 
				"30 82 01 21 30 0E 06 03 55 1D 0F 01 01 FF 04 04\r\n" + 
				"03 02 01 06 30 12 06 03 55 1D 13 01 01 FF 04 08\r\n" + 
				"30 06 01 01 FF 02 01 00 30 1D 06 03 55 1D 0E 04\r\n" + 
				"16 04 14 96 DE 61 F1 BD 1C 16 29 53 1C C0 CC 7D\r\n" + 
				"3B 83 00 40 E6 1A 7C 30 47 06 03 55 1D 20 04 40\r\n" + 
				"30 3E 30 3C 06 04 55 1D 20 00 30 34 30 32 06 08\r\n" + 
				"2B 06 01 05 05 07 02 01 16 26 68 74 74 70 73 3A\r\n" + 
				"2F 2F 77 77 77 2E 67 6C 6F 62 61 6C 73 69 67 6E\r\n" + 
				"2E 63 6F 6D 2F 72 65 70 6F 73 69 74 6F 72 79 2F\r\n" + 
				"30 33 06 03 55 1D 1F 04 2C 30 2A 30 28 A0 26 A0\r\n" + 
				"24 86 22 68 74 74 70 3A 2F 2F 63 72 6C 2E 67 6C\r\n" + 
				"6F 62 61 6C 73 69 67 6E 2E 6E 65 74 2F 72 6F 6F\r\n" + 
				"74 2E 63 72 6C 30 3D 06 08 2B 06 01 05 05 07 01\r\n" + 
				"01 04 31 30 2F 30 2D 06 08 2B 06 01 05 05 07 30\r\n" + 
				"01 86 21 68 74 74 70 3A 2F 2F 6F 63 73 70 2E 67\r\n" + 
				"6C 6F 62 61 6C 73 69 67 6E 2E 63 6F 6D 2F 72 6F\r\n" + 
				"6F 74 72 31 30 1F 06 03 55 1D 23 04 18 30 16 80\r\n" + 
				"14 60 7B 66 1A 45 0D 97 CA 89 50 2F 7D 04 CD 34\r\n" + 
				"A8 FF FC FD 4B 30 0D 06 09 2A 86 48 86 F7 0D 01\r\n" + 
				"01 0B 05 00 03 82 01 01 00 46 2A EE 5E BD AE 01\r\n" + 
				"60 37 31 11 86 71 74 B6 46 49 C8 10 16 FE 2F 62\r\n" + 
				"23 17 AB 1F 87 F8 82 ED CA DF 0E 2C DF 64 75 8E\r\n" + 
				"E5 18 72 A7 8C 3A 8B C9 AC A5 77 50 F7 EF 9E A4\r\n" + 
				"E0 A0 8F 14 57 A3 2A 5F EC 7E 6D 10 E6 BA 8D B0\r\n" + 
				"08 87 76 0E 4C B2 D9 51 BB 11 02 F2 5C DD 1C BD\r\n" + 
				"F3 55 96 0F D4 06 C0 FC E2 23 8A 24 70 D3 BB F0\r\n" + 
				"79 1A A7 61 70 83 8A AF 06 C5 20 D8 A1 63 D0 6C\r\n" + 
				"AE 4F 32 D7 AE 7C 18 45 75 05 29 77 DF 42 40 64\r\n" + 
				"64 86 BE 2A 76 09 31 6F 1D 24 F4 99 D0 85 FE F2\r\n" + 
				"21 08 F9 C6 F6 F1 D0 59 ED D6 56 3C 08 28 03 67\r\n" + 
				"BA F0 F9 F1 90 16 47 AE 67 E6 BC 80 48 E9 42 76\r\n" + 
				"34 97 55 69 24 0E 83 D6 A0 2D B4 F5 F3 79 8A 49\r\n" + 
				"28 74 1A 41 A1 C2 D3 24 88 35 30 60 94 17 B4 E1\r\n" + 
				"04 22 31 3D 3B 2F 17 06 B2 B8 9D 86 2B 5A 69 EF\r\n" + 
				"83 F5 4B C4 AA B4 2A F8 7C A1 B1 85 94 8C F4 0C\r\n" + 
				"87 0C F4 AC 40 F8 59 49 98";
		
		StringTokenizer tocken = new StringTokenizer(s, " \r\n", false);
		byte[] b = new byte[tocken.countTokens()];
		int index = 0;
		while(tocken.hasMoreTokens()) {
			b[index++] = (byte)Integer.parseInt(tocken.nextToken(), 16);
		}
		File f = new File("c:\\cert.crt");
		try {
			f.createNewFile();

			FileOutputStream output = new FileOutputStream(f);
			output.write(b);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
