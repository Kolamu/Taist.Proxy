package com.taist.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.taist.message.ChannelConfig;
import com.taist.message.RequestBody;
import com.taist.proxy.DefaultHttpSolver;
import com.taist.proxy.DefaultHttpsSolver;
import com.taist.proxy.Solver;

public final class SolverFactory {
	private SolverFactory() { }
	private static final Solver defaultHttpSolver = new DefaultHttpSolver();
	private static final Solver defaultHttpsSolver = new DefaultHttpsSolver();
	private static Map<String, Solver> solverMap = new ConcurrentHashMap<String, Solver>();
	
	public static Solver getSolver(ChannelConfig config) {
		String key = config.getServerHost() + ":" + config.getServerPort();
		Solver solver = config.isHttps() ? defaultHttpsSolver : defaultHttpSolver;
		solver.setNext(solverMap.get(key));
		return solver;
	}
	
	public static void register(String host, int port, Solver solver) {
		if(solver == null) {
			return;
		}
		String key = host + ":" + port;
		Solver header = solverMap.get(key);
		Solver next = header.getNext();
		header.setNext(solver);
		solver.setNext(next);
	}
	
	public static void register(RequestBody message, Solver solver) {
		register(message.getHost(),  message.getPort(), solver);
	}
}
