package com.taist.ssl;

import com.taist.helper.ByteHelper;
import com.taist.ssl.fragment.*;

public class SSLPlainText {
	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}

	public byte getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(byte majorVersion) {
		this.majorVersion = majorVersion;
	}

	public byte getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(byte minorVersion) {
		this.minorVersion = minorVersion;
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

	private ContentType type;
	private byte majorVersion;
	private byte minorVersion;
	private int length;
	private Fragment fragment;
	
	public SSLPlainText(byte[] data) {
		type = getContentType(data[0]);
		majorVersion = data[1];
		minorVersion = data[2];
		length = ByteHelper.toInt(data, 3, 2);
		fragment = getFragment(ByteHelper.subarray(data, 5, length));
	}

	public byte[] getBytes(){
		byte[] bts = new byte[]{
				type.getCode(),
				majorVersion,
				minorVersion
		};
		bts = ByteHelper.concat(bts, ByteHelper.fromInt16(length));
		bts = ByteHelper.concat(bts, fragment.getBytes());
		return bts;
	}
	
	private Fragment getFragment(byte[] fragmentData) {
		switch (type) {
		case alert:
			return new AlertFragment(fragmentData);
		case handshake:
			return new HandshakeFragment(fragmentData);
		case change_cipher_spec:
			return new ChangeCipherSpecFragment(fragmentData);
		case application_data:
			return new ApplicationDataFragment(fragmentData);
		default:
			return new UnknownDataFragment(fragmentData);
		}
	}

	private ContentType getContentType(byte b) {
		ContentType[] types = ContentType.values();
		for(ContentType type : types) {
			if(type.getCode() == b) {
				return type;
			}
		}

		return ContentType.blank;
	}
}
