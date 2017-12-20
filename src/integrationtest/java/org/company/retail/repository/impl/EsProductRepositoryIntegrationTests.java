package org.company.retail.repository.impl;

import org.company.retail.client.ResourceNotFoundException;
import org.company.retail.client.RestClient;
import org.company.retail.config.RestTemplateConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EsProductRepository.class })
@ContextConfiguration(classes = { RestClient.class, EsProductRepository.class, RestTemplateConfiguration.class    })
public class EsProductRepositoryIntegrationTests {
	
	@Autowired
	EsProductRepository esProductRepository;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void getProduct_Elasticsearch_notAvailable() throws ResourceNotFoundException, Exception {
		
		thrown.expect(Exception.class);
	    thrown.expectMessage("Error while retrieving product with id [123] from Elasticsearch");
	    
		this.esProductRepository.getProduct(123L);
	}

}
