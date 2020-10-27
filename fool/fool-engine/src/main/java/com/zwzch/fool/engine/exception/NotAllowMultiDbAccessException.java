package com.zwzch.fool.engine.exception;

public class NotAllowMultiDbAccessException extends Exception{

	public NotAllowMultiDbAccessException() {
		super();
	}
	
	public NotAllowMultiDbAccessException(String msg) {
		super(msg);
	}

}