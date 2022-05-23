package com.knoell.survey.exception;

import com.knoell.survey.api.ISurveyApi;

/**
 * Used for {@link ISurveyApi} responses.
 * 
 * @author lam
 *
 */
public class ResponseException extends Exception {
	private static final long serialVersionUID = 5361162219161833645L;

	private final int responseCode;

	public ResponseException(int response) {
		super();
		this.responseCode = response;
	}

	public ResponseException(int response, String message, Throwable cause) {
		super(message, cause);
		this.responseCode = response;
	}

	public ResponseException(int response, String message) {
		super(message);
		this.responseCode = response;
	}

	public ResponseException(int response, Throwable cause) {
		super(cause);
		this.responseCode = response;
	}

	public int getResponseCode() {
		return responseCode;
	}

}
