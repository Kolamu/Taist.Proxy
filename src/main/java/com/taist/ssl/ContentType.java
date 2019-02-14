package com.taist.ssl;

public enum ContentType {
	change_cipher_spec(20),
	alert(21),
	handshake(22),
	application_data(23),
	blank(255);
	
	private byte code = 0;
	private ContentType(int code) {
		this.code = (byte)code;
	}
	
	public byte getCode() {
		return code;
	}
}
