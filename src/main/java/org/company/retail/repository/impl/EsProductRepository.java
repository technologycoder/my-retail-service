package org.company.retail.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.client.RestClient;
import org.company.retail.client.RestException;
import org.company.retail.model.Product;
import org.company.retail.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

@Repository
public class EsProductRepository implements ProductRepository {

	private Log logger = LogFactory.getLog(getClass());

	@Autowired
	private RestClient restClient;

	@Value("${es.url}")
	private String repositoryUrl;

	@Value("${product.repository.es.resource}")
	private String repositoryResource;

	@Override
	public Product getProduct(long id) throws ResourceNotFoundException, Exception {

		try {

			String resource = String.format("%s/%d/_source", repositoryResource, id);

			Product product = this.restClient.resourceCRUD(repositoryUrl, resource, HttpMethod.GET, Product.class,
					null);

			return product;

		} catch (ResourceNotFoundException e) {
			String message = String.format("Product with id [%d] not found.", id);
			this.logger.error(message);
			throw new ResourceNotFoundException(message, e);

		} catch (RestException e) {
			String message = String.format("Error while retrieving product with id [%d] from Elasticsearch", id);
			this.logger.error(message, e);
			throw new Exception(message, e);
		}

	}

}
