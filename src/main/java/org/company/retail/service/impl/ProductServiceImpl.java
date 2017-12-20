package org.company.retail.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.model.Product;
import org.company.retail.model.ProductPrice;
import org.company.retail.repository.ProductPriceRepository;
import org.company.retail.repository.ProductRepository;
import org.company.retail.service.ProductService;
import org.company.retail.service.ProductValidationException;
import org.company.retail.service.ProductValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductServiceImpl implements ProductService {

	private Log logger = LogFactory.getLog(getClass());

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductPriceRepository productPriceRepository;

	@Autowired
	private ProductValidationService productValidationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Product getProduct(long id) throws ResourceNotFoundException, Exception {

		Product product = this.getProductName(id);

		ProductPrice productPrice = this.getProductPrice(id);
		product.setProductPrice(productPrice);

		return product;

	}

	private Product getProductName(long id) throws ResourceNotFoundException, Exception {

		try {
			Product product = this.productRepository.getProduct(id);
			return product;
		} catch (ResourceNotFoundException e) {
			logger.error(e);
			throw e;

		} catch (Exception e) {
			logger.error(e);
			throw e;

		}

	}

	private ProductPrice getProductPrice(long id) throws ResourceNotFoundException, Exception {

		try {
			ProductPrice productPrice = this.productPriceRepository.getProductPrice(id);
			return productPrice;
		} catch (ResourceNotFoundException e) {
			logger.error(e);
			throw e;

		} catch (Exception e) {
			logger.error(e);
			throw e;

		}

	}

	@Override
	public Product updateProduct(Product product)
			throws ResourceNotFoundException, ProductValidationException, Exception {
		
		//perform validation
		List<String> messages = this.productValidationService.validate(product);
		if (messages != null && !messages.isEmpty()) {
			throw new ProductValidationException(this.objectMapper.writeValueAsString(messages));
		}		

		//update product prices
		this.productPriceRepository.updateProductPrice(product.getProductPrice(), product.getId());

		return this.getProduct(product.getId());

	}

}
