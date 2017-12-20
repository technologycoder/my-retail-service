package org.company.retail.repository.impl;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.client.RestClient;
import org.company.retail.config.RestTemplateConfiguration;
import org.company.retail.model.Product;
import org.company.retail.model.ProductPrice;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EsProductPriceRepository.class })
@ContextConfiguration(classes = { RestClient.class, EsProductPriceRepository.class, RestTemplateConfiguration.class,
		ObjectMapper.class })
public class EsProductPriceRepositoryIntegrationTests {

	@Autowired
	EsProductPriceRepository esProductPriceRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void getProductPrice_Elasticsearch_notAvailable() throws ResourceNotFoundException, Exception {

		thrown.expect(Exception.class);
		thrown.expectMessage("Error while retrieving price for product with id [123] from Elasticsearch");

		esProductPriceRepository.getProductPrice(123L);

	}

	@Test
	public void updateProductPrice_Elasticsearch_notAvailable() throws ResourceNotFoundException, Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		thrown.expect(Exception.class);
		thrown.expectMessage("Error while retrieving price for product with id [123] from Elasticsearch");

		esProductPriceRepository.updateProductPrice(product.getProductPrice(), id);
	}

}
