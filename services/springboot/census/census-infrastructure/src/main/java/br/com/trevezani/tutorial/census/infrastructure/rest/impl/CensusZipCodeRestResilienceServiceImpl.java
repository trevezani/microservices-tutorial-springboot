package br.com.trevezani.tutorial.census.infrastructure.rest.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.trevezani.tutorial.census.core.port.CensusZipCodeRestService;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunication;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.CheckedFunction0;

public class CensusZipCodeRestResilienceServiceImpl implements CensusZipCodeRestService {
	Logger log = LoggerFactory.getLogger(this.getClass());	
	
	private static final String SERVICE = "census-zipcode";
	
	private final CircuitBreakerRegistry circuitBreakerRegistry;
	private final RetryRegistry retryRegistry;
	
	private final HTTPCommunication<ZipCodeRest> httpCommunication;
	
	private final String remoteURL;

	public CensusZipCodeRestResilienceServiceImpl(final CircuitBreakerRegistry circuitBreakerRegistry, final RetryRegistry retryRegistry, 
			final HTTPCommunication<ZipCodeRest> httpCommunication, final String remoteURL) {
		this.circuitBreakerRegistry = circuitBreakerRegistry;
		this.retryRegistry = retryRegistry;		
		this.httpCommunication = httpCommunication;
		this.remoteURL = remoteURL;
	}
	
	@Override
	public ZipCodeRest call(final String correlationId, final String zip) throws InternalErrorException, BusinessException {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(SERVICE);
		Retry retry = retryRegistry.retry(SERVICE);		
		
		final String url = remoteURL.concat("/zipcode/").concat(zip);

		CheckedFunction0<ZipCodeRest> supplier = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> this.internal(correlationId, url));
		supplier = Retry.decorateCheckedSupplier(retry, supplier);
		
		try {
			return supplier.apply();
			
		} catch (ServiceNotAvailableException | CallNotPermittedException e) {
			return fallback(correlationId, e.getMessage());
		} catch (BusinessException e) {
			throw new BusinessException(e.getMessage());
		} catch (Throwable e) {
			throw new InternalErrorException(e.getMessage());
		}
	}

	private ZipCodeRest internal(final String correlationId, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		ZipCodeRest rest = new ZipCodeRest();
		
		httpCommunication.callGetService(correlationId, SERVICE, url)
				.ifPresent(m -> {
					rest.setType(m.getType());
					rest.setPrimaryCity(m.getPrimaryCity());
					rest.setAreaCodes(m.getAreaCodes());
					rest.setState(m.getState());
				});
		
		return rest;
	}

	private ZipCodeRest fallback(final String correlationId, String message) {
		log.error("Fallback :: {}", message);

		ZipCodeRest rest = new ZipCodeRest();
		rest.setFallback(Boolean.TRUE);
		rest.setType("NA");
		rest.setPrimaryCity("NA");
		rest.setAreaCodes("NA");
		rest.setState("NA");

		return rest;
	}
}
