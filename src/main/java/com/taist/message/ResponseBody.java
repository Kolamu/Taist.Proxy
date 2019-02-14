package com.taist.message;

public class ResponseBody {
	private byte[] data = new byte[0];
	private String stringValue = null;
	public ResponseBody(byte[] data) {
		this.data = data;
		this.stringValue = new String(data, 0, data.length);
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	@Override
	public String toString() {
		return stringValue;
	}
	

	public static final ResponseBody RSP_503 = new ResponseBody("HTTP/1.0 503 Error\r\nContent-Length: 0\r\n\r\n".getBytes());
	public static final ResponseBody RSP_CONNECTED = new ResponseBody("HTTP/1.1 200 Connection Established\r\nProxy-agent: http-serv\r\n\r\n".getBytes());
}
