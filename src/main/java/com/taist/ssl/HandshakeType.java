package com.taist.ssl;

public enum HandshakeType {
	hello_request(0),
	client_hello(1),
	server_hello(2),
    certificate(11),
    server_key_exchange(12),
    certificate_request(13),
    server_hello_done(14),
    certificate_verify(15),
    client_key_exchange(16),
    finished(20),
    unknown(255);
	
	private byte code = -1;
	HandshakeType(int code){
		this.code = (byte)code;
	}
	
	public byte getCode() {
		return code;
	}
}
