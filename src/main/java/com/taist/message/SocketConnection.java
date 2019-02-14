package com.taist.message;

public interface SocketConnection extends Runnable {
	boolean isClosed();
	void close();
}
