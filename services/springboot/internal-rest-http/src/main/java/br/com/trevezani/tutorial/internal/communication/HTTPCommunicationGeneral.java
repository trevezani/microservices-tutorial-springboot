package br.com.trevezani.tutorial.internal.communication;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

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

public class HTTPCommunicationGeneral<T> implements HTTPCommunication<T> {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final Class<T> clazz;

	private final Gson gson;
	
	public HTTPCommunicationGeneral(final Class<T> clazz) {
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
	}
	
	@Override
	public Optional<T> callGetService(final String correlationId, final String service, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		log.info("[{}] Calling {}", correlationId, url);

		try {
		    var request = HttpRequest.newBuilder(URI.create(url))
		    		.GET()
		            .header("Accept", "application/json")
		            .header("x-correlation-id", correlationId)
		            .build();
			
		    CompletableFuture<CensusResponse<T>> rest = HttpClient.newBuilder()
										    					  .connectTimeout(Duration.ofSeconds(5))
										    					  .build()
										    					  .sendAsync(request, BodyHandlers.ofString())
										    					  .thenApply(HttpResponse::body)
									 				              .thenApply(response -> {
									 				            	  if (response == null) {
									 				            		  return null;
									 				            	  }
									 				            	  return gson.fromJson(response, TypeToken.getParameterized(CensusResponse.class, clazz).getType());				
													              });
		    
		    CensusResponse<T> object = rest.get();

		    if (object == null) {
		    	throw new InternalErrorException("Object not retrieved");
		    }
		    
			if (object.getStatus().equals(String.valueOf(HttpStatus.OK.value()))) {
				return Optional.ofNullable(object.getData());
			} else if (object.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST.value()))) {
				throw new BusinessException(object.getMessage());
			} else {
				throw new InternalErrorException(object.getMessage());
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new ServiceNotAvailableException(String.format("The '%s' service is not available at the moment you can try again later. (Exception: %s)", service, e.getMessage()));
		}
	}
}
