package org.company.retail.client;

public class RestException extends Exception {

	public RestException(final String message) {
		super(message);
	}

	public RestException(final String message, final Exception e) {
		super(message, e);
	}

}
