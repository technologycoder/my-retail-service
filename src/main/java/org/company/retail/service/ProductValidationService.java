package org.company.retail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.company.retail.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductValidationService {

	@Autowired
	private Validator validator;

	public List<String> validate(final Product product) {

		List<String> messages = new ArrayList<>();

		Set<ConstraintViolation<Product>> violations = this.validator.validate(product);

		for (ConstraintViolation<Product> violation : violations) {

			messages.add(violation.getMessage());

		}

		return messages;

	}

}
