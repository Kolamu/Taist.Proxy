package com.taist.server;

import com.bj58.qa.agent.CertUtil;
import com.taist.message.Channel;
import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;
import com.taist.proxy.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

import org.bouncycastle.asn1.ocsp.ResponderID;

public class ProxySession {
	private Channel channel = null;
    private boolean busy = false;
    //private Receiver receiver = null;
    //private Sender sender = null;
    private ByteBuffer fromClient = null;
    private ByteBuffer fromServer = null;
    private List<Byte> clientData = new ArrayList<Byte>();
    private static final int BUF_SIZE = 4096;
    private boolean isHttps = false;

    public ProxySession(SocketChannel channel) {
    	this.channel = new Channel(channel);
        fromClient = ByteBuffer.allocate(BUF_SIZE);
        fromServer = ByteBuffer.allocate(BUF_SIZE);
        init(channel);
        if(isHttps) {
            
        }
    }

    public void read(SocketChannel channel) {
        busy = true;
        fromClient.clear();
        try {
            int n = channel.read(fromClient);
            while(n > 0) {
               if(n < fromClient.capacity()) {
                   break;
               }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        busy = false;
    }

    public boolean busy() {
        return busy;
    }

    public void write(SocketChannel channel) {
        //sender.execute(channel);
    }

    private void init(SocketChannel channel) {
        try {
            int n = channel.read(fromClient);
            String content = new String(fromClient.array(), 0, n);
            System.out.println(content);
            isHttps = content.startsWith("CONNECT");
        } catch (IOException e) {
            e.printStackTrace();
            isHttps = false;
        }
        
        if(isHttps) {
        	ByteBuffer buf = ByteBuffer.allocate(1024);
        	buf.put(ResponseBody.RSP_CONNECTED.getData());
    		buf.flip();
        	try {
        		while(buf.hasRemaining()) {
        			channel.write(buf);
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	try {
				handShake(channel);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    

	private void handShake(SocketChannel channel) throws Exception {
		SSLEngine sslEngine = getEngine();
		sslEngine.setUseClientMode(false);
		SSLSession session = sslEngine.getSession();
		ByteBuffer peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
		ByteBuffer peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
		
		ByteBuffer myAppData = ByteBuffer.wrap("Hello, I'm server.".getBytes());
		ByteBuffer myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
		
		sslEngine.beginHandshake();
		SSLEngineResult result;
		HandshakeStatus hsStatus = sslEngine.getHandshakeStatus();
		Status status;
        while(hsStatus != HandshakeStatus.FINISHED){
            
            System.out.println("handshake status: " + hsStatus);
            switch (hsStatus) {
            case NEED_TASK:
                Runnable runnable;
                while((runnable=sslEngine.getDelegatedTask()) != null){
                	System.out.println("run task");
                    runnable.run();
                }
                hsStatus = sslEngine.getHandshakeStatus();
                break;
            case NEED_UNWRAP:
            	int len = channel.read(peerNetData);
                peerNetData.flip();
                //peerAppData.flip();
                do {
                	
                    result = sslEngine.unwrap(peerNetData, peerAppData);
                    System.out.println("Unwrapping:\n" + result);
                    if(peerAppData.limit() > 0) {
                    	System.out.println("app data : \n" + new String(peerAppData.array()));
                    }
                    // During an handshake renegotiation we might need to perform
                    // several unwraps to consume the handshake data.
                } while (result.getStatus() == Status.OK &&
                        result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP &&
                        result.bytesProduced() == 0);
                
                if (peerAppData.position() == 0 &&
                    result.getStatus() == Status.OK &&
                    peerNetData.hasRemaining())
                {
                        
                    result = sslEngine.unwrap(peerNetData, peerAppData);
                    System.out.println("Unwrapping:\n" + result);
                    
                }
                
                hsStatus = result.getHandshakeStatus();
                status = result.getStatus();
                
                assert status != status.BUFFER_OVERFLOW : "buffer not overflow." + status.toString();
                
                // Prepare the buffer to be written again.
                peerNetData.compact();
                // And the app buffer to be read.
                peerAppData.flip();
                
                break;
                
            case NEED_WRAP:
                
                myNetData.clear();
                result = sslEngine.wrap(myAppData, myNetData);

                hsStatus = result.getHandshakeStatus();
                status = result.getStatus();
                
                while (status != Status.OK) {
                    System.out.println("wrap status: " + status);
                    switch (status) {
                    
                    case BUFFER_OVERFLOW:
                        break;
                    case BUFFER_UNDERFLOW:
                        break;
                    }
                }
                myNetData.flip();
                while(myNetData.hasRemaining()) {
                	channel.write(myNetData);
                }
                
                break;
                
            }
            
            if (myAppData.limit() == peerAppData.position()) {

            	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                break;
            }
        }
	}
	
	private SSLEngine getEngine() throws Exception {
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(
				getKeyManagerFactory().getKeyManagers(),
				getTrustManagerFactory().getTrustManagers(),
				null);
		return context.createSSLEngine();
	}
	
	private KeyManagerFactory getKeyManagerFactory() throws Exception {
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		String password = "taist.proxy";
		kmf.init(getKeyStore("*.baidu.com", password), password.toCharArray());
		 
		return kmf;
	}
	
	private TrustManagerFactory getTrustManagerFactory() throws Exception {
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		String password = "taist.proxy";
		tmf.init(getKeyStore("*.baidu.com", password));
		return tmf;
	}
	private KeyPair serverKey = null;
	private KeyStore getKeyStore(String host, String password) throws Exception {
		KeyStore ks = CertUtil.loadKeyStore("c:\\taist.proxy.pfx", password);
		KeyStore rs = KeyStore.getInstance(KeyStore.getDefaultType());
		rs.load(null, null);
		X509Certificate root = (X509Certificate) ks.getCertificate("Taist Proxy Root CA");
		String issuer = CertUtil.getSubject(root);
		PrivateKey privateKey = (PrivateKey) ks.getKey("Taist Proxy Root CA", password.toCharArray());
		serverKey = CertUtil.genKeyPair();
		X509Certificate cert = CertUtil.genCert(
				issuer,
				privateKey,
				root.getNotBefore(),
				root.getNotAfter(),
				serverKey.getPublic(),
				host);
		rs.setKeyEntry(
				"entry",
				serverKey.getPrivate(),
				password.toCharArray(),
				new Certificate[] { cert });
		return rs;
	}
	
}
