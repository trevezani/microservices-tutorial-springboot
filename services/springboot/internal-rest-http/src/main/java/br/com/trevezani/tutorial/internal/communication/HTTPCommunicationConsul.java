package br.com.trevezani.tutorial.internal.communication;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;
import br.com.trevezani.tutorial.internal.communication.response.CensusResponse;

public class HTTPCommunicationConsul<T> implements HTTPCommunication<T> {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final RestTemplate template;
	private final Class<T> clazz;

	private final Gson gson;
	private ObjectReader readerMap;
	
	public HTTPCommunicationConsul(final RestTemplate template, final Class<T> clazz) {
		this.template = template;
		this.clazz = clazz;
		
		this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
			@Override
			public LocalDateTime deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				if (json == null || json.getAsJsonPrimitive() == null) {
					return null;
				}
				
				return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
			}
		}).create();
		
		this.readerMap = new ObjectMapper().readerFor(Map.class);
	}
	
	@Override
	public Optional<T> callGetService(final String correlationId, final String service, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		try {
			var headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-correlation-id", correlationId);

			var entity = new HttpEntity<String>("parameters", headers);
			var result = template.exchange(url, HttpMethod.GET, entity, String.class);

			if (result != null && result.getStatusCodeValue() == HttpStatus.OK.value()) {
				Type collectionType = TypeToken.getParameterized(CensusResponse.class, clazz).getType();
				CensusResponse<T> rest = gson.fromJson(result.getBody(), collectionType);				
				
				if (rest.getStatus().equals(String.valueOf(HttpStatus.OK.value()))) {
					return Optional.ofNullable(rest.getData());
				} else if (rest.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST.value()))) {
					throw new BusinessException(rest.getMessage());
				} else {
					throw new InternalErrorException(rest.getMessage());
				}
			}

		} catch (ServiceUnavailable | ResourceAccessException e) {
			throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later. (Exception: %s)", service, e.toString()));

		} catch (IllegalStateException | IllegalArgumentException e) {
			if (e.getMessage().startsWith("No servers available for service") ||
					e.getMessage().startsWith("Service Instance cannot be null")) {
				throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later. (Exception: %s)", service, e.toString()));
			} else {
				throw new InternalErrorException(e.getMessage());
			}
			
		} catch (HttpStatusCodeException e) {
			int statusCode = e.getStatusCode().value();

			log.info("Error ({}) {}", statusCode, e.getMessage());

			if (e.getResponseBodyAsString().isBlank()) {
				throw new InternalErrorException(e.getMessage());
			} else {
				Map<String, String> map = null;

				try {
					map = readerMap.readValue(e.getResponseBodyAsString());
				} catch (JsonProcessingException ex) {
					throw new InternalErrorException(e.getResponseBodyAsString());
				}

				if (statusCode == HttpStatus.BAD_REQUEST.value()) {
					throw new BusinessException(map.get("message"));
				} else {
					throw new InternalErrorException(map.get("message"));
				}
			}
		}
		
		return Optional.empty();
	}
}
