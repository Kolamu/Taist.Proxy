package com.taist.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;

import com.taist.factory.CertFactory;
import com.taist.factory.SenderFactory;
import com.taist.helper.ByteHelper;
import com.taist.proxy.HttpReceiver;
import com.taist.proxy.HttpsReceiver;
import com.taist.proxy.Receiver;
import com.taist.proxy.Sender;
import com.taist.ssl.SSLPlainText;

public class Channel implements Runnable {
	private Receiver receiver = null;
	private ChannelConfig configuration = null;
	public Channel(Receiver receiver) {
		this.receiver = receiver;
	}
	
	public void run() {
		RequestBody request = receiver.receive();
		if(request == null) return;
		if(!ChannelFilter.filter(request.getHost())) {
			receiver.execute(ResponseBody.RSP_503);
			return;
		}
		
		InetSocketAddress clientAddress = (InetSocketAddress)receiver.getSocket().getRemoteSocketAddress();
		configuration = new ChannelConfig()
				.setClientHost(clientAddress.getAddress().getHostAddress())
				.setClientPort(clientAddress.getPort())
				.setServerHost(request.getHost())
				.setServerPort(request.getPort());

		if(request.getMethod().equals("CONNECT")) {
			receiver.execute(ResponseBody.RSP_CONNECTED);
			configuration.setHttps(true)
			.setReciever(new HttpsReceiver(receiver.getSocket()));
			try {
				handShake();
				//doHttpsShakeHands();
			} catch (Exception e) {
				e.printStackTrace();
				receiver.execute(ResponseBody.RSP_503);
				return;
			}
			return;
		}
		else {
			configuration.setHttps(false)
			.setReciever(new HttpReceiver(receiver.getSocket()));
			configuration.getSolver().solveRequest(request);
			configuration.getSender().execute(request);
			ResponseBody response = configuration.getSolver()
					.solveResponse(configuration.getSender().receive());
			receiver.execute(response);
		}
		MessageBus.registerConnection(new ServerConnection(configuration));
		MessageBus.registerConnection(new ClientConnection(configuration));
	}
	
	private void handShake() {
		Receiver receiver = configuration.getReciever();
		Sender sender = configuration.getSender();
		
		RequestBody req = receiver.receive();
		SSLPlainText clientHello = new SSLPlainText(req.getData());
		sender.execute(req);
		ResponseBody res = sender.receive();
		SSLPlainText serverHello = new SSLPlainText(res.getData());
		System.out.println(serverHello.getLength());
		System.out.println(res.getData().length);
		System.out.println(ByteHelper.toString(res.getData()));
		receiver.execute(res);
		req = receiver.receive();
		SSLPlainText client = new SSLPlainText(req.getData());
		sender.execute(req);
		res = sender.receive();
		SSLPlainText certificate = new SSLPlainText(res.getData());
		receiver.execute(res);
		res = sender.receive();
		SSLPlainText server_key_exchange = new SSLPlainText(res.getData());
		receiver.execute(res);
	}

	public ChannelConfig getConfiguration() {
		return configuration;
	}

    private void doHttpsShakeHands() throws Exception {
    	
    	System.out.println("doHttpsShakeHands");
    	RequestBody request = receiver.receive();
    	System.out.println(byte2hex(request.getData()));
//    	Socket s = receiver.getSocket();
//        DataInputStream in = new DataInputStream(s.getInputStream());  
//        DataOutputStream out = new DataOutputStream(s.getOutputStream());  
  
        int length=byte2Int(request.getData(), 3, 3);
        System.out.println(request.getData().length);
        System.out.println(length);
        byte[] clientSupportHash=new byte[length];
        System.arraycopy(request.getData(), 6, clientSupportHash, 0, length);
        String clientHash=new String(clientSupportHash);
        //hash=clientHash;  
        System.out.println("客户端发送了hash算法为:"+clientHash);
  
        //第二步，发送服务器证书到客户端  
        byte[] certificateBytes=CertFactory.readCertifacates();  
        PrivateKey privateKey=CertFactory.readPrivateKeys();
        System.out.println("发送证书给客户端,字节长度为:"+certificateBytes.length);
        System.out.println("证书内容为:" + byte2hex(certificateBytes));
        receiver.execute(new ResponseBody(certificateBytes));
  
        System.out.println("获取客户端通过公钥加密后的随机数");
        request = receiver.receive();
        int secureByteLength=byte2Int(request.getData(), 3, 3);
        System.out.println(request.getData().length);
        System.out.println(secureByteLength);
        byte[] secureBytes=new byte[secureByteLength];
        System.arraycopy(request.getData(), 4, secureBytes, 0, secureByteLength);
  
        System.out.println("读取到的客户端的随机数为:"+byte2hex(secureBytes));
        byte secureSeed[]=decrypt(secureBytes, privateKey);
        System.out.println("解密后的随机数密码为:" +byte2hex(secureSeed));
  
//        //第三步 获取客户端加密字符串  
//        int skip=in.readInt();
//        System.out.println("第三步 获取客户端加密消息,消息长度为 ：" +skip);
//        byte[] data=readBytes(in,skip);  
//  
//        System.out.println("客户端发送的加密消息为 : " +byte2hex(data));  
//        System.out.println("用私钥对消息解密，并计算SHA1的hash值");
//        byte message[] =decrypt(data,new SecureRandom(secureBytes), privateKey);  
//        byte serverHash[]=cactHash(message);
//  
//  
//        System.out.println("获取客户端计算的SHA1摘要");  
//        int hashSkip=in.readInt();  
//        byte[] clientHashBytes=readBytes(in,hashSkip);
//        System.out.println("客户端SHA1摘要为 : " + byte2hex(clientHashBytes));
//  
//        System.out.println("开始比较客户端hash和服务器端从消息中计算的hash值是否一致");
//        boolean isHashEquals=byteEquals(serverHash,clientHashBytes);  
//        System.out.println("是否一致结果为 ： " + isHashEquals);  
//  
//  
//  
//        System.out.println("第一次校验客户端发送过来的消息和摘译一致，服务器开始向客户端发送消息和摘要");  
//        System.out.println("生成密码用于加密服务器端消息,secureRandom : "+byte2hex(secureSeed));  
//        SecureRandom secureRandom=new SecureRandom(secureSeed);  
//  
//        String randomMessage=random();
//        System.out.println("服务器端生成的随机消息为 : "+randomMessage);
//        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
  
//        System.out.println("用DES算法并使用客户端生成的随机密码对消息加密");
//        byte[] desKey=DesCoder.initSecretKey(secureRandom);
//        key=DesCoder.toKey(desKey);
//  
//        byte serverMessage[]=DesCoder.encrypt(randomMessage.getBytes(), key);  
//        SocketUtils.writeBytes(out,serverMessage,serverMessage.length);  
//        System.out.println("服务器端发送的机密后的消息为:"+byte2hex(serverMessage)+",加密密码为:"+byte2hex(secureSeed));  
//  
//        System.out.println("服务器端开始计算hash摘要值");  
//        byte serverMessageHash[]=cactHash(randomMessage.getBytes());  
//        System.out.println("服务器端计算的hash摘要值为 :" +byte2hex(serverMessageHash));  
//        SocketUtils.writeBytes(out,serverMessageHash,serverMessageHash.length);  
//  
        System.out.println("握手成功，之后所有通信都将使用DES加密算法进行加密");  
    }
    
    private int byte2Int(byte[] b, int offset, int length) {
    	int va = 0;
    	for(int i=offset;i<length + offset;i++) {
    		va = va | ((b[i] << 8 * (i - offset)) & 0xff);
    	}
    	return va;
    }
    
    public static byte[] cactHash(byte[] bytes) {  
        byte[] _bytes = null;  
        try {  
            MessageDigest md = MessageDigest.getInstance("SHA1");  
            md.update(bytes);  
            _bytes = md.digest();  
        } catch (NoSuchAlgorithmException ex) {  
            ex.printStackTrace();  
        }  
        return _bytes;  
    }  
  
  
  
    static String random(){  
        StringBuilder builder=new StringBuilder();  
        Random random=new Random();  
        int seedLength=10;  
        for(int i=0;i<seedLength;i++){  
            builder.append(digits[random.nextInt(seedLength)]);
        }  
  
        return builder.toString();  
    }  
  
    static char[] digits={  
            '0','1','2','3','4',  
            '5','6','7','8','9',  
            'a','b','c','d','e',  
            'f','g','h','i','j'  
    };
    
    public static boolean byteEquals(byte a[],byte[] b){  
        boolean equals=true;  
        if(a==null || b==null){  
            equals=false;  
        }  
  
        if(a!=null && b!=null){  
            if(a.length!=b.length){  
                equals=false;
            }else{  
                for(int i=0;i<a.length;i++){  
                    if(a[i]!=b[i]){  
                        equals=false;  
                        break;  
                    }  
                }  
            }  
  
        }  
        return equals;  
    }  
  
    public static byte[] decrypt(byte data[], PrivateKey privateKey) throws Exception{  
        // 对数据解密  
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        return cipher.doFinal(data);  
    }  
  
    public static byte[] decrypt(byte data[], SecureRandom seed, PrivateKey privateKey) throws Exception{  
        // 对数据解密  
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateKey,seed);  
        return cipher.doFinal(data);  
    }
    
    private final String hexCode = "0123456789ABCDEF";
    public String byte2hex(byte[] b) {  
        StringBuffer buf = new StringBuffer();
        for (int n = 0; n < b.length; n++) {  
            int v = b[n] & 0XFF;
            buf.append(hexCode.charAt((v>>4) & 0xF));
            buf.append(hexCode.charAt(v & 0xF));
            buf.append(",");
        }  
        return buf.toString();
    }

    private byte[] readBytes(DataInputStream in,int length) throws IOException {  
        int r=0;  
        byte[] data=new byte[length];  
        while(r<length && in.available() > 0){  
            r+=in.read(data,r,length-r);  
            System.out.println(r);
        }  
        
        return data;  
    }  
  
    private void writeBytes(DataOutputStream out,byte[] bytes,int length) throws IOException{  
        out.writeInt(length);  
        out.write(bytes,0,length);  
        out.flush();  
    }
}
