package org.company.retail.client;

public class ResourceNotFoundException extends Exception {

	public ResourceNotFoundException(final String message) {
		super(message);
	}

	public ResourceNotFoundException(final String message, final Exception e) {
		super(message, e);
	}

}
