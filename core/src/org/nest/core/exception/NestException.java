package org.nest.core.exception;

public class NestException extends Exception {

	private static final long serialVersionUID = -2122941609576536542L;

	public NestException() {

	}

	public NestException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public NestException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NestException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NestException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
