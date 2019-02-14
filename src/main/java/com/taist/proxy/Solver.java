package com.taist.proxy;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public interface Solver {
	RequestBody solveRequest(RequestBody request);
	ResponseBody solveResponse(ResponseBody response);
	Solver getNext();
	void setNext(Solver solver);
}
