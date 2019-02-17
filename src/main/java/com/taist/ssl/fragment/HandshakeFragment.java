package com.taist.ssl.fragment;

import com.taist.helper.ByteHelper;
import com.taist.ssl.Fragment;
import com.taist.ssl.HandshakeType;

public class HandshakeFragment extends AbstractFragment {
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
		super(data);
		type = getType(data[0]);
		if(type == HandshakeType.unknown){
			System.out.println("Unknow handshake type " + data[0]);
			return;
		}
		length = ByteHelper.toInt(data, 1, 3);
		fragment = getFragment(ByteHelper.subarray(data, 4, length));
	}
	
	@Override
	public byte[] getBytes() {
		if(type == HandshakeType.unknown){
			return super.getBytes();
		}
		else{
			data = new byte[]{
					type.getCode()
			};
			data = ByteHelper.concat(data, ByteHelper.fromInt24(length));
			data = ByteHelper.concat(data, fragment.getBytes());
			return data;
		}
	}
	
	private Fragment getFragment(byte[] fragmentData) {
		switch (type) {
		case certificate:
			System.out.println("<< certificate");
			return new CertificateDataFragment(fragmentData);
		case certificate_verify:
			System.out.println(">> certificate_verify");
			return new UnknownDataFragment(fragmentData);
		case client_hello:
			System.out.println(">> client_hello");
            return new UnknownDataFragment(fragmentData);
		case certificate_request:
			System.out.println(">> certificate_request");
            return new UnknownDataFragment(fragmentData);
		case client_key_exchange:
			System.out.println(">> client_key_exchange");
            return new UnknownDataFragment(fragmentData);
		case hello_request:
			System.out.println(">> hello_request");
            return new UnknownDataFragment(fragmentData);
		case server_hello:
			System.out.println("<< server_hello");
            return new UnknownDataFragment(fragmentData);
		case finished:
			System.out.println("<< finished");
            return new UnknownDataFragment(fragmentData);
		case server_hello_done:
			System.out.println("<< server_hello_done");
            return new UnknownDataFragment(fragmentData);
		case server_key_exchange:
			System.out.println("<< server_key_exchange");
            return new UnknownDataFragment(fragmentData);
		default:
			System.out.println("<< default");
			return new UnknownDataFragment(fragmentData);
		}
	}
	
	private HandshakeType getType(int code) {
		HandshakeType[] types = HandshakeType.values();
		for(HandshakeType type : types) {
			if(type.getCode() == code) {
				return type;
			}
		}
		
		return HandshakeType.unknown;
	}
}
