package com.zwzch.fool.engine.exception;

public class NotSupportSQLException extends RuntimeException{

	public NotSupportSQLException() {
		super();

	}

	public NotSupportSQLException(String message, Throwable cause) {
		super(message, cause);

	}

	public NotSupportSQLException(String message) {
		super(message);

	}

	public NotSupportSQLException(Throwable cause) {
		super(cause);
	}
	
}