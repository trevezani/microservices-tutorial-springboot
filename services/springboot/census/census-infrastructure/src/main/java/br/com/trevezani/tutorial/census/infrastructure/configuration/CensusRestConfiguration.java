package br.com.trevezani.tutorial.census.infrastructure.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCase;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCaseImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusDemographyRestServiceImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusZipCodeRestServiceImpl;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunication;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;

@Configuration
@Profile("default")
public class CensusRestConfiguration {
	@Value("${censusdemography.api.url:http://censusdemography:8080}")
	private String censusdemographyURL;

	@Value("${censuszipcode.api.url:http://censuszipcode:8080}")
	private String censuszipcodeURL;

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	@Primary
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(5000))
                .build();
	}	
	
	@Bean
	public GetCensusInformationUseCase createGetCensusInformationUseCase() {
		return new GetCensusInformationUseCaseImpl(
						new CensusDemographyRestServiceImpl(
								new HTTPCommunication<DemographyRest>(DemographyRest.class, restTemplate), 
								censusdemographyURL), 
						new CensusZipCodeRestServiceImpl(
								new HTTPCommunication<ZipCodeRest>(ZipCodeRest.class, restTemplate), 
								censuszipcodeURL));
	}
}
