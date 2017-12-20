package org.company.retail.model;

import org.company.retail.TestUtils;
import org.company.retail.config.JacksonConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { JacksonConfiguration.class })
public class ProductTest {

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void jsonToProductConversion() throws Exception {

		String json = TestUtils.getResourceAsString("product.json");

		Product product = objectMapper.readValue(json, Product.class);

		String actualJson = objectMapper.writeValueAsString(product);

		JSONAssert.assertEquals(json, actualJson, false);

	}

}
