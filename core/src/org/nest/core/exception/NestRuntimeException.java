package org.nest.core.exception;

public class NestRuntimeException extends RuntimeException {

	/**
	 * @Fields serialVersionUID : Description
	 */

	private static final long serialVersionUID = 1L;

	public NestRuntimeException(Exception cause) {
		super(cause);
	}

	public NestRuntimeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NestRuntimeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NestRuntimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NestRuntimeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
