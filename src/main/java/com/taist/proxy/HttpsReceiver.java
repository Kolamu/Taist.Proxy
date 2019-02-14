package com.taist.proxy;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.taist.helper.ByteHelper;
import com.taist.helper.ProxyHelper;
import com.taist.message.RequestBody;

public class HttpsReceiver extends BaseReceiver {
	private ConcurrentLinkedQueue<Byte> buffer = new ConcurrentLinkedQueue<>();
	private boolean closed = false;
	public HttpsReceiver(Socket socket) {
		super(socket);
	}
	
	/**
	 * TLS 数据包格式如下：
	 * uint8 type
	 * uint8 majorVersion
	 * uint8 minorVersion
	 * uint16 length
	 * 
	 * 因此获取第5 6字节作为长度获取整个包的内容后再传输
	 */
	//@Override1
	public RequestBody receive1() {
		System.out.println("Https receive...");
		if(closed) {
			return null;
		}
		
		byte[] data = new byte[5];
		int index = 0;
		RequestBody request = null;
		while(!closed) {
			if(buffer.isEmpty()) {
				ProxyHelper.sleep(50);
				continue;
			}
			while(!buffer.isEmpty()) {
				data[index++] = buffer.poll();
				if(index == 5) {
					int len = ByteHelper.toInt(data, 3, 2);
					System.out.println(len);
					byte[] tmp = data;
					data = new byte[5 + len];
					System.arraycopy(tmp, 0, data, 0, 5);
				}

				if(index == data.length) {
					request = new RequestBody(data);
					break;
				}
			}
			if(index == data.length) {
				break;
			}
		}
		
		System.out.println(request);
		return request;
	}
}
