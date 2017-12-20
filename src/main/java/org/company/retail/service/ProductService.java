package org.company.retail.service;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.Product;

public interface ProductService {
	
	/**
	 * Returns Product details for the id provided as argument
	 * @param id of the product that needs to be retrieved
	 * @return Product details
	 * @throws ResourceNotFoundException if product with id not found
	 * @throws Exception for all other errors
	 */
	public Product getProduct(long id) throws ResourceNotFoundException, Exception;

	/**
	 * Updates Product
	 * @param product has price details that need to be updated
	 * @return updated Product details
	 * @throws ResourceNotFoundException if product is not not found in data store
	 * @throws ProductValidationException if validation rules for Product fail
	 * @throws Exception for all other Exceptions
	 */
	public Product updateProduct(Product product)
			throws ResourceNotFoundException, ProductValidationException, Exception;

}
