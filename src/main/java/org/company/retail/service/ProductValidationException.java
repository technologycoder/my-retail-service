package org.company.retail.service;

public class ProductValidationException extends Exception {

	public ProductValidationException(final String message) {
		super(message);
	}

	public ProductValidationException(final String message, final Exception e) {
		super(message, e);
	}

}
