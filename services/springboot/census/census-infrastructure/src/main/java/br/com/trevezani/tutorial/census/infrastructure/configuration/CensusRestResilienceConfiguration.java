package br.com.trevezani.tutorial.census.infrastructure.configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCase;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCaseImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusDemographyRestResilienceServiceImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusZipCodeRestResilienceServiceImpl;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunicationConsul;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunicationGeneral;
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
@Profile({"resilience", "consul"})
public class CensusRestResilienceConfiguration {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
	
	@Value("${censusdemography.api.url:http://censusdemography:8080}")
	private String censusdemographyURL;

	@Value("${censuszipcode.api.url:http://censuszipcode:8080}")
	private String censuszipcodeURL;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@Autowired
	private RetryRegistry retryRegistry;
	
	@Autowired
	RestTemplate template;

	@LoadBalanced
	@Bean
	RestTemplate loadBalanced(RestTemplateBuilder builder) {
		return builder
                .setConnectTimeout(Duration.ofMillis(8000))
                .setReadTimeout(Duration.ofMillis(8000))
                .build();
	}
	
	@Bean
	public CircuitBreakerRegistry createCircuitBreakerRegistry() {
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
			    .failureRateThreshold(50)
			    .waitDurationInOpenState(Duration.ofMillis(8000))
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
		log.info("[NA] Profile ({})", activeProfile);
		log.info("[NA] Census-Demography ({})", censusdemographyURL);
		log.info("[NA] Census-ZipCode ({})", censuszipcodeURL);
		
		if (activeProfile.contains("consul")) {
			return new GetCensusInformationUseCaseImpl(
					new CensusDemographyRestResilienceServiceImpl(
							circuitBreakerRegistry, 
							retryRegistry, 
							new HTTPCommunicationConsul<DemographyRest>(template, DemographyRest.class), 
							censusdemographyURL), 
					new CensusZipCodeRestResilienceServiceImpl(
							circuitBreakerRegistry, 
							retryRegistry, 
							new HTTPCommunicationConsul<ZipCodeRest>(template, ZipCodeRest.class), 
							censuszipcodeURL));
		} else {
			return new GetCensusInformationUseCaseImpl(
					new CensusDemographyRestResilienceServiceImpl(
							circuitBreakerRegistry, 
							retryRegistry, 
							new HTTPCommunicationGeneral<DemographyRest>(DemographyRest.class), 
							censusdemographyURL), 
					new CensusZipCodeRestResilienceServiceImpl(
							circuitBreakerRegistry, 
							retryRegistry, 
							new HTTPCommunicationGeneral<ZipCodeRest>(ZipCodeRest.class), 
							censuszipcodeURL));
		}
	}
}
