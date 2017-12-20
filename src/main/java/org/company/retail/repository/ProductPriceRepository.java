package org.company.retail.repository;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.ProductPrice;

public interface ProductPriceRepository {

	/**
	 * Retrieved Product price info
	 * @param id of the product whose price info is requested
	 * @return Price info for the Product
	 * @throws ResourceNotFoundException when price info for the product is not found in the store
	 * @throws Exception for all other errors
	 */
	public ProductPrice getProductPrice(long id) throws ResourceNotFoundException, Exception;

	
	/**
	 * Updates product price info
	 * @param productPrice that needs to be updated
	 * @param id of the product whose price needs to be updated
	 * @throws ResourceNotFoundException when product price is not found
	 * @throws Exception for all other errors
	 */
	public void updateProductPrice(ProductPrice productPrice, Long id) throws ResourceNotFoundException, Exception;
}
