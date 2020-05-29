package br.com.trevezani.tutorial.census.infrastructure.configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCase;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCaseImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusDemographyRestResilienceServiceImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusZipCodeRestResilienceServiceImpl;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunication;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@Configuration
@Profile("resilience")
public class CensusRestResilienceConfiguration {
	@Value("${censusdemography.api.url:http://censusdemography:8080}")
	private String censusdemographyURL;

	@Value("${censuszipcode.api.url:http://censuszipcode:8080}")
	private String censuszipcodeURL;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@Autowired
	private RetryRegistry retryRegistry;

	@Bean
	public CircuitBreakerRegistry createCircuitBreakerRegistry() {
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
			    .failureRateThreshold(50)
			    .waitDurationInOpenState(Duration.ofMillis(2000))
			    .permittedNumberOfCallsInHalfOpenState(2)
			    .slidingWindowSize(10)
			    .minimumNumberOfCalls(5)
			    .recordExceptions(IOException.class, TimeoutException.class, ServiceNotAvailableException.class)
			    .ignoreExceptions(BusinessException.class, InternalErrorException.class)
			    .build();

		return CircuitBreakerRegistry.of(circuitBreakerConfig);
	}
	
	@Bean
	public RetryRegistry createRetryRegistry() {
		RetryConfig config = RetryConfig.custom()
				  .maxAttempts(3)
				  .retryExceptions(IOException.class, TimeoutException.class, ServiceNotAvailableException.class)
				  .ignoreExceptions(BusinessException.class, InternalErrorException.class)
				  .build();

		return RetryRegistry.of(config);
	}
	
	@Bean
	public GetCensusInformationUseCase createGetCensusInformationUseCase() {
		return new GetCensusInformationUseCaseImpl(
						new CensusDemographyRestResilienceServiceImpl(
								circuitBreakerRegistry, 
								retryRegistry, 
								new HTTPCommunication<DemographyRest>(DemographyRest.class), 
								censusdemographyURL), 
						new CensusZipCodeRestResilienceServiceImpl(
								circuitBreakerRegistry, 
								retryRegistry, 
								new HTTPCommunication<ZipCodeRest>(ZipCodeRest.class), 
								censuszipcodeURL));
	}
}
