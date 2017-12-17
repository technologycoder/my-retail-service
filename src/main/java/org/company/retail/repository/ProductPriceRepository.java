package org.company.retail.repository;

import org.company.retail.model.ProductPrice;

public interface ProductPriceRepository {

	public ProductPrice getProductPrice(long id) throws Exception;

	public void updateProductPrice(ProductPrice productPrice, Long id) throws Exception;
}
