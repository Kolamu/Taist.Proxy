package com.taist.ssl;

import com.taist.helper.ByteHelper;

public class HandshakeFragment implements Fragment {
	public HandshakeType getType() {
		return type;
	}

	public void setType(HandshakeType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	private HandshakeType type;
	private int length;
	private Fragment fragment;
	
	public HandshakeFragment(byte[] data) {
		type = getType(data[0]);
		length = ByteHelper.toInt(data, 1, 3);
		fragment = getFragment(ByteHelper.subarray(data, 4, length));
	}
	
	@Override
	public byte[] getBytes() {
		return null;
	}
	
	private Fragment getFragment(byte[] fragmentData) {
		switch (type) {
		case certificate:
			System.out.println("<< certificate");
			System.out.println(ByteHelper.toString(fragmentData));
			return null;
		case certificate_verify:
			System.out.println(">> certificate_verify");
			return null;
		case client_hello:
			System.out.println(">> client_hello");
			return null;
		case certificate_request:
			System.out.println(">> certificate_request");
			return null;
		case client_key_exchange:
			System.out.println(">> client_key_exchange");
			return null;
		case hello_request:
			System.out.println(">> hello_request");
			return null;
		case server_hello:
			System.out.println("<< server_hello");
			return null;
		case finished:
			System.out.println("<< finished");
			return null;
		case server_hello_done:
			System.out.println("<< server_hello_done");
			return null;
		case server_key_exchange:
			System.out.println("<< server_key_exchange");
			return null;
		default:
			System.out.println("<< default");
			return null;
		}
	}
	
	private HandshakeType getType(int code) {
		HandshakeType[] types = HandshakeType.values();
		for(HandshakeType type : types) {
			if(type.getCode() == code) {
				return type;
			}
		}
		
		return HandshakeType.blank;
	}
}
