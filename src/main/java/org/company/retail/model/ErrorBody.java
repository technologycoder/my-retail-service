package org.company.retail.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorBody {

	@JsonProperty("error")
	private String error;

	@JsonProperty("errors")
	private List<String> errors;

	public ErrorBody(final String error) {
		super();
		this.error = error;
	}

	public ErrorBody(final List<String> errors) {
		super();
		this.errors = errors;
	}

	public String getError() {
		return error;
	}

	public List<String> getErrors() {
		return errors;
	}

}
