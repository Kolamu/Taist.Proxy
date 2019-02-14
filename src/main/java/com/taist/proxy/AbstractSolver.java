package com.taist.proxy;

public abstract class AbstractSolver implements Solver {
	private Solver next = null;
	
	public Solver getNext() {
		return next;
	}
	
	public void setNext(Solver solver) {
		next = solver;
	}
}
