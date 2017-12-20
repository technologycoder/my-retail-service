package org.company.retail;

import static org.junit.Assert.assertEquals;

import org.company.retail.model.Product;
import org.company.retail.model.ProductPrice;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:integration-test.properties")
public class MyRetailServiceIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static EmbeddedElasticsearchServer embeddedElasticsearchServer;

	@Value("${es.url}")
	private String repositoryUrl;

	@Value("${product.price.repository.es.resource}")
	private String priceResource;

	@Value("${product.price.repository.es.index}")
	private String priceIndex;

	@Value("${product.repository.es.resource}")
	private String productResource;

	@Value("${product.repository.es.index}")
	private String productIndex;

	@BeforeClass
	public static void setup() throws Exception {

		embeddedElasticsearchServer = new EmbeddedElasticsearchServer();

	}

	@Before
	public void setupBeforeTest() throws Exception {

		// cleanup
		this.deleteProductIndex();
		this.deleteProductPriceIndex();

	}

	@AfterClass
	public static void cleanup() throws Exception {

		embeddedElasticsearchServer.shutdown();

	}

	@Test
	public void getProduct() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// act
		ResponseEntity<String> response = this.restTemplate.getForEntity("/products/" + product	.getId()
																								.intValue(),
				String.class);

		// assert
		product.setProductPrice(productPrice);
		assertEquals(200, response.getStatusCodeValue());
		JSONAssert.assertEquals(this.objectMapper.writeValueAsString(product), response.getBody(), false);

	}

	@Test
	public void getProduct_resourceNotFoundException_product_missing() throws Exception {

		/// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// act
		ResponseEntity<String> response = this.restTemplate.getForEntity("/products/9999", String.class);

		// assert
		assertEquals(404, response.getStatusCodeValue());
		assertEquals("Product with id [9999] not found.", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void getProduct_resourceNotFoundException_price_missing() throws Exception {

		/// arrange
		Product product = this.getSampleProduct();
		this.indexProductDetails(product);

		// act
		ResponseEntity<String> response = this.restTemplate.getForEntity("/products/" + product	.getId()
																								.intValue(),
				String.class);

		// assert
		assertEquals(404, response.getStatusCodeValue());
		assertEquals("Price info for product with id [123] not found.", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("EUR", 1000.0);
		Product updatedProduct = new Product(product.getId(), product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		restTemplate.put("/products/" + updatedProduct	.getId()
														.intValue(),
				entity);

		// assert
		Product actualUpdatedProduct = this.getproduct(updatedProduct.getId());
		JSONAssert.assertEquals(this.objectMapper.writeValueAsString(updatedProduct),
				this.objectMapper.writeValueAsString(actualUpdatedProduct), false);

	}

	@Test
	public void updateProductPrice_validationError_id_missing() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("EUR", 1000.0);
		Product updatedProduct = new Product(null, product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("[\"Product ID is missing\"]", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_validationError_name_missing() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("EUR", 1000.0);
		Product updatedProduct = new Product(product.getId(), null, updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("[\"Product name is missing\"]", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_validationError_price_missing() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("EUR", 1000.0);
		Product updatedProduct = new Product(product.getId(), product.getName(), null);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("[\"Price info is missing\"]", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_validationError_currencyCode_missing() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice(null, 1000.0);
		Product updatedProduct = new Product(product.getId(), product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("[\"Currency code is missing\"]", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_validationError_priceValue_missing() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("USD", null);
		Product updatedProduct = new Product(product.getId(), product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("[\"Price value is missing\"]", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_validationError_priceValue_lessThanZero() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);
		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("USD", -100.0);
		Product updatedProduct = new Product(product.getId(), product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("[\"Price should not be less than 0\"]", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_productDoesNotExists() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductPriceDetails(product.getId(), productPrice);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("USD", 1000.0);
		Product updatedProduct = new Product(product.getId(), product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Product with id [123] not found.", JsonPath.read(response.getBody(), "$.error"));

	}

	@Test
	public void updateProductPrice_productPriceDoesNotExists() throws Exception {

		// arrange
		Product product = this.getSampleProduct();
		ProductPrice productPrice = this.getSampleProductPrice();

		this.indexProductDetails(product);

		// new product price
		ProductPrice updatedProductPrice = new ProductPrice("USD", 1000.0);
		Product updatedProduct = new Product(product.getId(), product.getName(), updatedProductPrice);

		// act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(this.objectMapper.writeValueAsString(updatedProduct),
				headers);
		ResponseEntity<String> response = restTemplate.exchange("/products/" + product	.getId()
																						.intValue(),
				HttpMethod.PUT, entity, String.class);

		// assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Price info for product with id [123] not found.", JsonPath.read(response.getBody(), "$.error"));

	}
	
	
	

	private void indexProductDetails(Product product) throws Exception {

		ResponseEntity<String> response = this.restTemplate.postForEntity(
				String.format("%s/%s/%d", repositoryUrl, productResource, product	.getId()
																					.intValue()),
				this.objectMapper.writeValueAsString(product), String.class);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}

	private void indexProductPriceDetails(Long id, ProductPrice productPrice) throws Exception {

		ResponseEntity<String> response = this.restTemplate.postForEntity(
				String.format("%s/%s/%d", repositoryUrl, priceResource, id.intValue()),
				this.objectMapper.writeValueAsString(productPrice), String.class);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}

	private void deleteProductIndex() {

		this.restTemplate.delete(String.format("%s/%s", repositoryUrl, productIndex));

	}

	private void deleteProductPriceIndex() {

		this.restTemplate.delete(String.format("%s/%s", repositoryUrl, priceIndex));

	}

	private Product getproduct(Long id) {
		return this.restTemplate.getForObject("/products/" + id.intValue(), Product.class);
	}

	private Product getSampleProduct() {

		Long id = 123L;
		String name = "test product";

		Product product = new Product(id, name);

		return product;

	}

	private ProductPrice getSampleProductPrice() {

		String currency = "USD";
		Double value = 23.34;
		return new ProductPrice(currency, value);

	}

}
