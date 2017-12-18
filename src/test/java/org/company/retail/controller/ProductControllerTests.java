package org.company.retail.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.config.JacksonConfiguration;
import org.company.retail.model.Product;
import org.company.retail.model.ProductPrice;
import org.company.retail.service.ProductService;
import org.company.retail.service.ProductValidationException;
import org.company.retail.service.impl.ProductServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = { ProductController.class, ProductServiceImpl.class, JacksonConfiguration.class })
public class ProductControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void products() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		when(this.productService.getProduct(id)).thenReturn(product);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders.get("/products/" + id))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(id.intValue())))
				.andExpect(jsonPath("$.name", is(name)))
				.andExpect(jsonPath("$.current_price.currency_code", is(currency)))
				.andExpect(jsonPath("$.current_price.value", is(value)));

		verify(this.productService, times(1)).getProduct(id);
		verifyNoMoreInteractions(this.productService);
	}

	@Test
	public void products_throws_ResourceNotFoundException() throws Exception {

		String errorMessage = "Error happened";
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(errorMessage);

		// Arrange
		Long id = 123L;

		when(this.productService.getProduct(id)).thenThrow(resourceNotFoundException);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders.get("/products/" + id))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.error", is(errorMessage)));

		verify(this.productService, times(1)).getProduct(id);
		verifyNoMoreInteractions(this.productService);

	}

	@Test
	public void products_throws_Exception() throws Exception {

		String errorMessage = "Error happened";
		Exception exception = new Exception(errorMessage);

		// Arrange
		Long id = 123L;

		when(this.productService.getProduct(id)).thenThrow(exception);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders.get("/products/" + id))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.error", is(errorMessage)));

		verify(this.productService, times(1)).getProduct(id);
		verifyNoMoreInteractions(this.productService);

	}

	@Test
	public void updateProductPrice() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders	.put("/products/" + id)
												.contentType(MediaType.APPLICATION_JSON)
												.content(objectMapper.writeValueAsString(product)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk());

		verify(this.productService, times(1)).updateProduct(any(Product.class));
		verifyNoMoreInteractions(this.productService);

	}

	@Test
	public void updateProductPrice_throws_ResourceNotFound_Exception() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		String errorMessage = "Error happened";
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(errorMessage);

		when(this.productService.updateProduct(any(Product.class))).thenThrow(resourceNotFoundException);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders	.put("/products/" + id)
												.contentType(MediaType.APPLICATION_JSON)
												.content(objectMapper.writeValueAsString(product)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.error", is(errorMessage)));

		verify(this.productService, times(1)).updateProduct(any(Product.class));
		verifyNoMoreInteractions(this.productService);

	}

	@Test
	public void updateProductPrice_throws_ProductValidationException() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		String errorMessage = "Error happened";
		ProductValidationException productValidationException = new ProductValidationException(errorMessage);

		when(this.productService.updateProduct(any(Product.class))).thenThrow(productValidationException);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders	.put("/products/" + id)
												.contentType(MediaType.APPLICATION_JSON)
												.content(objectMapper.writeValueAsString(product)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.error", is(errorMessage)));

		verify(this.productService, times(1)).updateProduct(any(Product.class));
		verifyNoMoreInteractions(this.productService);

	}

	@Test
	public void updateProductPrice_throws_Exception() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		String errorMessage = "Error happened";
		Exception exception = new Exception(errorMessage);

		when(this.productService.updateProduct(any(Product.class))).thenThrow(exception);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders	.put("/products/" + id)
												.contentType(MediaType.APPLICATION_JSON)
												.content(objectMapper.writeValueAsString(product)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.error", is(errorMessage)));

		verify(this.productService, times(1)).updateProduct(any(Product.class));
		verifyNoMoreInteractions(this.productService);

	}

	@Test
	public void products_not_supported_POST() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		when(this.productService.getProduct(id)).thenReturn(product);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders	.post("/products/" + id)
												.contentType(MediaType.APPLICATION_JSON)
												.content(objectMapper.writeValueAsString(product)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isMethodNotAllowed());

		verify(this.productService, times(0)).getProduct(id);
		verifyNoMoreInteractions(this.productService);
	}

	@Test
	public void products_not_supported_DELETE() throws Exception {

		// Arrange
		Long id = 123L;
		String name = "test product";
		String currency = "USD";
		Double value = 23.34;
		Product product = new Product(id, name, new ProductPrice(currency, value));

		when(this.productService.getProduct(id)).thenReturn(product);

		// Act Assert
		this.mvc.perform(MockMvcRequestBuilders.delete("/products/" + id))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isMethodNotAllowed());

		verify(this.productService, times(0)).getProduct(id);
		verifyNoMoreInteractions(this.productService);
	}

}
