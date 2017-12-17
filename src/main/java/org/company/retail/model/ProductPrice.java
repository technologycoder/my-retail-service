package org.company.retail.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPrice {

	@NotBlank(message = "Currency code is missing")
	@JsonProperty("currency_code")
	private String currenyCode;

	@NotNull(message = "Price value is missing")
	@Min(value = 0, message = "Price should not be less than 0")
	@JsonProperty("value")
	private Double value;

	public ProductPrice() {

	}

	public ProductPrice(String currenyCode, Double value) {
		super();
		this.currenyCode = currenyCode;
		this.value = value;
	}

	public String getCurrenyCode() {
		return currenyCode;
	}

	public void setCurrenyCode(String currenyCode) {
		this.currenyCode = currenyCode;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

}
