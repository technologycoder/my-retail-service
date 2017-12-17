package org.company.retail.service;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.Product;

public interface ProductService {

	public Product getProduct(long id) throws ResourceNotFoundException, Exception;

	public Product updateProduct(Product product)
			throws ResourceNotFoundException, ProductValidationException, Exception;

}
