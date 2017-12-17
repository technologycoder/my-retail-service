package org.company.retail.repository;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.Product;

public interface ProductRepository {

	public Product getProduct(long id) throws ResourceNotFoundException, Exception;

}
