package org.company.retail.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.ErrorBody;
import org.company.retail.model.Product;
import org.company.retail.service.ProductService;
import org.company.retail.service.ProductValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Product products(@PathVariable final Long id) throws ResourceNotFoundException, Exception {

		return this.productService.getProduct(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public Product updateProductPrice(@RequestBody final Product product)
			throws ResourceNotFoundException, ProductValidationException, Exception {
		return this.productService.updateProduct(product);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorBody emrError(final ResourceNotFoundException e) {

		return new ErrorBody(e.getMessage());
	}

	@ExceptionHandler(ProductValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody emrError(final ProductValidationException e) {

		return new ErrorBody(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorBody error(final Exception e) {
		logger.error(ExceptionUtils.getFullStackTrace(e), e);
		return new ErrorBody(e.getMessage());
	}

}
