package com.knoell.survey.exception;

/**
 * An error thrown by business logic of this application.
 * 
 * @author lam
 *
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = 3803637354773774875L;

	/**
	 * {@inheritDoc}
	 */
	public BusinessException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * {@inheritDoc}
	 */
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public BusinessException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public BusinessException(Throwable cause) {
		super(cause);
	}

}
