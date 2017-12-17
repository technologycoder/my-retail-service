package org.company.retail.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

	@NotNull(message = "Product ID is missing")
	@JsonProperty("id")
	private Long id;

	@NotNull(message = "Product name is missing")
	@JsonProperty("name")
	private String name;

	@Valid
	@NotNull(message = "Price info is missing")
	@JsonProperty("current_price")
	private ProductPrice productPrice;

	public Product() {

	}

	public Product(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Product(Long id, String name, ProductPrice productPrice) {
		super();
		this.id = id;
		this.name = name;
		this.productPrice = productPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProductPrice getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(ProductPrice productPrice) {
		this.productPrice = productPrice;
	}

}
