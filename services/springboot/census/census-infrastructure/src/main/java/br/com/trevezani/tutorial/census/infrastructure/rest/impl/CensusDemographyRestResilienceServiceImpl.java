package br.com.trevezani.tutorial.census.infrastructure.rest.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.trevezani.tutorial.census.core.port.CensusDemographyRestService;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunication;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.CheckedFunction0;

public class CensusDemographyRestResilienceServiceImpl implements CensusDemographyRestService {
	Logger log = LoggerFactory.getLogger(this.getClass());	
	
	private static final String SERVICE = "census-demography";

	private final CircuitBreakerRegistry circuitBreakerRegistry;
	private final RetryRegistry retryRegistry;
	
	private final HTTPCommunication<DemographyRest> httpCommunication;
	
	private final String remoteURL;
	
	public CensusDemographyRestResilienceServiceImpl(final CircuitBreakerRegistry circuitBreakerRegistry, final RetryRegistry retryRegistry, 
			final HTTPCommunication<DemographyRest> httpCommunication, final String remoteURL) {
		this.circuitBreakerRegistry = circuitBreakerRegistry;
		this.retryRegistry = retryRegistry;
		this.httpCommunication = httpCommunication;
		this.remoteURL = remoteURL;
	}

	@Override
	public DemographyRest call(final String correlationId, final String zip) throws InternalErrorException, BusinessException {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(SERVICE);
		Retry retry = retryRegistry.retry(SERVICE);		
		
		final String url = remoteURL.concat("/demography/").concat(zip);

		CheckedFunction0<DemographyRest> supplier = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> this.internal(correlationId, url));
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

	private DemographyRest internal(final String correlationId, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		DemographyRest rest = new DemographyRest();
		
		httpCommunication.callGetService(correlationId, SERVICE, url)
				.ifPresent(m -> {
					rest.setStateName(m.getStateName());
					rest.setPopulation(m.getPopulation());
					rest.setDensity(m.getDensity());
				});

		return rest;
	}
	
	private DemographyRest fallback(final String correlationId, String message) {
		log.error("Fallback :: {}", message);

		DemographyRest rest = new DemographyRest();
		rest.setFallback(Boolean.TRUE);
		rest.setStateName("NA");
		rest.setPopulation("NA");
		rest.setDensity("NA");

		return rest;
	}
}
