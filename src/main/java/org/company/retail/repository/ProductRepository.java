package org.company.retail.repository;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.Product;

public interface ProductRepository {

	/**
	 * Retrieves Product details for the id provided as argument
	 * @param id of the product that is returned
	 * @return Product
	 * @throws ResourceNotFoundException when the product for the id is not found
	 * @throws Exception for all other errors.
	 */
	public Product getProduct(long id) throws ResourceNotFoundException, Exception;

}
