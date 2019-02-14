package com.taist.proxy;

import java.net.Socket;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public interface Sender {
	void execute(RequestBody message);
	Socket getSocket();
	ResponseBody getDefaultResponse();
	ResponseBody receive();
	void close();
}
