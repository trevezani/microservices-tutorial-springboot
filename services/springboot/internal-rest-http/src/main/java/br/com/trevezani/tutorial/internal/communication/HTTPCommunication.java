package br.com.trevezani.tutorial.internal.communication;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
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

public class HTTPCommunication<T> {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final Class<T> clazz;
	private final RestTemplate restTemplate;

	private final Gson gson;
	
	public HTTPCommunication(final Class<T> clazz, final RestTemplate restTemplate) {
		this.clazz = clazz;
		this.restTemplate = restTemplate;
		
		this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
			@Override
			public LocalDateTime deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
			}
		}).create();
	}
	
	public Optional<T> callGetService(final String correlationId, final String service, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		log.info("[{}] Calling {}", correlationId, url);

		try {
			var headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-correlation-id", correlationId);

			var entity = new HttpEntity<String>("parameters", headers);
			var result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

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

		} catch (ServiceUnavailable e) {
			throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later.", service));

		} catch (ResourceAccessException e) {
			throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later.", service));

		} catch (HttpStatusCodeException e) {
			int statusCode = e.getStatusCode().value();

			log.info("[{}] Error ({}) {}", correlationId, statusCode, e.getMessage());

			if (e.getResponseBodyAsString().isBlank()) {
				throw new InternalErrorException(e.getMessage());
			} else {
				throw new InternalErrorException(e.getResponseBodyAsString());
			}
		}

		return Optional.empty();
	}

}
