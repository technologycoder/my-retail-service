package org.company.retail.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {

	private Log logger = LogFactory.getLog(getClass());

	@Autowired
	private RestTemplate restTemplate;

	public <T, U> U resourceCRUD(final String url, final String resource, final HttpMethod httpMethod,
			final Class<U> responseClazz, final T requestBody) throws RestException, ResourceNotFoundException {

		try {

			HttpEntity<?> httpEntity = HttpEntity.EMPTY;
			if (requestBody != null) {
				httpEntity = new HttpEntity<>(requestBody);
			}

			ResponseEntity<U> response = this.restTemplate.exchange(url + resource, httpMethod, httpEntity,
					responseClazz);

			return response.getBody();

		} catch (HttpClientErrorException e) {

			String errorBody = e.getResponseBodyAsString();

			String message = String.format("Error performing [%s] for [%s], HTTP Status code: [%s], error: [%s]",
					httpMethod.toString(), resource, e.getStatusCode().toString(), errorBody);

			this.logger.error(message, e);

			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				throw new ResourceNotFoundException(message, e);
			} else {
				throw new RestException(message, e);
			}
		} catch (Exception e) {

			String message = String.format("Error performing [%s] for [%s]", httpMethod.toString(), resource);
			this.logger.error(message, e);
			throw new RestException(message, e);
		}

	}

}
