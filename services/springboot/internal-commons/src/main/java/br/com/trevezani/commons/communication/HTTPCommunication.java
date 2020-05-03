package br.com.trevezani.commons.communication;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import br.com.trevezani.commons.exception.BusinessException;
import br.com.trevezani.commons.exception.InternalErrorException;
import br.com.trevezani.commons.exception.ServiceNotAvailableException;

@Service
public class HTTPCommunication {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	public Optional<Map<String, String>> callService(final String correlationId, final String service, final String url) throws Exception {
		log.info("[{}] Calling {}", correlationId, url);

		try {
			var responseType = new ParameterizedTypeReference<Map<String, String>>() {};

			var headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-correlation-id", correlationId);

			var entity = new HttpEntity<String>("parameters", headers);
			var result = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);

			if (result != null && result.getStatusCodeValue() == HttpStatus.OK.value()) {
				return Optional.ofNullable(result.getBody());
			}

		} catch (ServiceUnavailable e) {
			throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later.", service));

		} catch (ResourceAccessException e) {
			throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later.", service));

		} catch (HttpStatusCodeException e) {
			int statusCode = e.getStatusCode().value();

			ObjectReader reader = new ObjectMapper().readerFor(Map.class);

			Map<String, String> map = null;

			try {
				map = reader.readValue(e.getResponseBodyAsString());
			} catch (JsonProcessingException ex) {
				throw new InternalErrorException(ex.getMessage());
			}

			if (statusCode == HttpStatus.BAD_REQUEST.value()) {
				throw new BusinessException(map.get("message"));
			} else {
				throw new InternalErrorException(map.get("message"));
			}
		}

		return Optional.empty();
	}

}
