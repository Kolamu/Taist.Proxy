package com.taist.message;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {
	private String method = null;
	private String url = null;
	private String protocal = null;
	private String host = null;
	private boolean https = false;
	private int port = 80;
	private String realUrl = null;
	
	private byte[] data = new byte[0];
	private byte[] requestBody = new byte[0];
	private String stringValue = null;
	private Map<String, String> headers = new HashMap<String, String>();
	
	public RequestBody(byte[] data) {
		if(data == null || data.length == 0) {
			return;
		}
		try {
			parse(data);
		}
		catch(Exception e) {
			
		}
	}
	
	private void parse(byte[] data) {
		try {
			this.stringValue = new String(data);
			this.data = data;
			String[] array = stringValue.split("\r\n");
			if(array.length == 0) {
				return;
			}
			if(!parseProtocal(array[0])) {
				return;
			}
			parseHeader(array);
			parseData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseData(byte[] data) {
		if(!stringValue.contains("\r\n")) {
			return;
		}
		String temp = stringValue.substring(0, stringValue.lastIndexOf("\r\n\r\n") + 4);
		try {
			int startIndex = temp.getBytes().length;
			int size = data.length - startIndex;
			this.requestBody = new byte[size];
			System.arraycopy(data, startIndex, this.data, 0, size);
		} catch (Exception e) {
			e.printStackTrace();
			this.requestBody = data;
		}
	}

	private void parseHeader(String[] array) {
		for(int i=1;i<array.length;i++) {
			if(array[i].isEmpty()) {
				break;
			}
			
			addToHeader(array[i]);
		}
	}

	private void addToHeader(String header) {
		int index = header.indexOf(':');
		headers.put(header.substring(0, index), header.substring(index+1));
	}

	private boolean parseProtocal(String content) {
		if(!content.toLowerCase().contains("http/")) {
			return false;
		}
		String[] cArray = content.split(" ");
		if(cArray.length == 3) {
			method = cArray[0];
			url = cArray[1];
			protocal = cArray[2];
			parseUrl();
			return true;
		}
		else {
			//System.out.println(stringValue);
			return false;
		}
	}

	private void parseUrl() {
		String temp = null;
		
		if(method.equals("CONNECT")) {
			https = true;
			temp = url;
		}
		else {
			https = false;
			temp = url.substring(url.indexOf("//") + 2);
		}
		
		String hostAndPort = temp;
		realUrl = "";
		if(temp.contains("/")) {
			hostAndPort = temp.substring(0, temp.indexOf("/"));
			realUrl = temp.substring(temp.indexOf("/"));
		}
		parseHostAndPort(hostAndPort);
	}

	private void parseHostAndPort(String hostAndPort) {
		if(hostAndPort.contains(":")) {
			String[] array = hostAndPort.split(":");
			if(array.length < 2) {
				return;
			}
			host = array[0];
			try {
				port = Integer.valueOf(array[1]);
			}
			catch(Exception e) {
				host = null;
				port = 80;
			}
		}
		else {
			host = hostAndPort;
			port = 80;
		}
	}

	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}

	public byte[] getData() {
		return data;
	}
	
	public byte[] getRequestBody() {
		return requestBody;
	}
	
	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocal() {
		return protocal;
	}

	public boolean isHttps() {
		return https;
	}
	
	public boolean isAlive() {
		if(isHttps()) {
			return true;
		}
		
		String connection = null;
		if(headers.containsKey("Connection")) {
			connection = headers.get("Connection");
		}
		
		if(headers.containsKey("Proxy-Connection")) {
			connection = headers.get("Proxy-Connection");
		}
		
		if(connection == null) {
			return false;
		}
		
		return connection.equals("Keep-Alive");
	}

	public String getRealUrl() {
		return realUrl;
	}

	public String getStringValue() {
		return stringValue;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		return stringValue;
	}
}
