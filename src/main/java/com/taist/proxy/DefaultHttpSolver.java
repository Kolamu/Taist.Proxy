package com.taist.proxy;

import java.util.HashMap;
import java.util.Map;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public class DefaultHttpSolver extends AbstractSolver {
	public RequestBody solveRequest(RequestBody message) {
		Map<String, String> headers = new HashMap<String, String>(message.getHeaders());
		String connection = headers.get("Proxy-Connection");
		headers.put("Connection", connection == null ? "Keep-Alive" : connection);
		headers.put("Host", message.getHost() + ":" + message.getPort());
		StringBuffer buf = new StringBuffer();
		buf.append(message.getMethod());
		buf.append(" ");
		buf.append(message.getUrl());
		buf.append(" ");
		buf.append(message.getProtocal());
		buf.append("\r\n");
		for(String key : headers.keySet()) {
			buf.append(key);
			buf.append(":");
			buf.append(headers.get(key));
			buf.append("\r\n");
		}
		buf.append("\r\n");
		byte[] hBytes = buf.toString().getBytes();
		byte[] body = message.getRequestBody();
		byte[] data = new byte[hBytes.length+body.length];
		System.arraycopy(hBytes, 0, data, 0, hBytes.length);
		System.arraycopy(body, 0, data, hBytes.length, body.length);
		return new RequestBody(data);
	}

	@Override
	public ResponseBody solveResponse(ResponseBody response) {
		return response;
	}
}
