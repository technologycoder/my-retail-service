package org.company.retail.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.client.RestClient;
import org.company.retail.client.RestException;
import org.company.retail.model.ProductPrice;
import org.company.retail.repository.ProductPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class EsProductPriceRepository implements ProductPriceRepository {

	private Log logger = LogFactory.getLog(getClass());

	@Autowired
	private RestClient restClient;

	@Value("${es.url}")
	private String repositoryUrl;

	@Value("${product.price.repository.es.resource}")
	private String repositoryResource;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public ProductPrice getProductPrice(long id) throws Exception {
		try {

			String resource = String.format("%s/%d/_source", repositoryResource, id);

			ProductPrice productPrice = this.restClient.resourceCRUD(repositoryUrl, resource, HttpMethod.GET,
					ProductPrice.class, null);

			return productPrice;

		} catch (ResourceNotFoundException e) {
			String message = String.format("Price info for product with id [%d] not found.", id);
			this.logger.error(message);
			throw new ResourceNotFoundException(message, e);

		} catch (RestException e) {
			String message = String.format("Error while retrieving price for product with id [%d] from Elasticsearch",
					id);
			this.logger.error(message, e);
			throw new Exception(message, e);
		}
	}

	public void updateProductPrice(ProductPrice productPrice, Long id) throws Exception {

		try {

			String resource = String.format("%s/%d", repositoryResource, id);

			this.restClient.resourceCRUD(repositoryUrl, resource, HttpMethod.PUT, String.class, productPrice);

			this.restClient.resourceCRUD(repositoryUrl, "product-price/_flush", HttpMethod.POST, String.class, null);

		} catch (RestException e) {
			String message = String.format("Error while updating product pricing info [%s]",
					this.objectMapper.writeValueAsString(productPrice));
			this.logger.error(message, e);
			throw new Exception(message, e);
		}

	}

}
