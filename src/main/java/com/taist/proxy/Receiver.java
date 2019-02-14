package com.taist.proxy;

import java.net.Socket;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public interface Receiver {
	void execute(ResponseBody response);
	Socket getSocket();
	RequestBody receive();
	void close();
}
