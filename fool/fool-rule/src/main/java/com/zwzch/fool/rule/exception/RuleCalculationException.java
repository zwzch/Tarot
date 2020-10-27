package com.zwzch.fool.rule.exception;

public class RuleCalculationException extends RuntimeException{

	public RuleCalculationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RuleCalculationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		//TODO DEL
		super(message, cause);
//		super(message, cause, enableSuppression, writableStackTrace);

		// TODO Auto-generated constructor stub
	}

	public RuleCalculationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RuleCalculationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RuleCalculationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
