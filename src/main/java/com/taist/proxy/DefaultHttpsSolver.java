package com.taist.proxy;

import com.taist.message.RequestBody;
import com.taist.message.ResponseBody;

public class DefaultHttpsSolver extends AbstractSolver {
	
	@Override
	public RequestBody solveRequest(RequestBody request) {
		return request;
	}

	@Override
	public ResponseBody solveResponse(ResponseBody response) {
		return response;
	}
}
