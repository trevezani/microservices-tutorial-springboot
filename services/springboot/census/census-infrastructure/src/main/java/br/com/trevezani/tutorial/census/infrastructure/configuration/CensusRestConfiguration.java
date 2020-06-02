package br.com.trevezani.tutorial.census.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCase;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCaseImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusDemographyRestServiceImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusZipCodeRestServiceImpl;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunicationGeneral;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;

@Configuration
@Profile({"default"})
public class CensusRestConfiguration {
	@Value("${censusdemography.api.url:http://censusdemography:8080}")
	private String censusdemographyURL;

	@Value("${censuszipcode.api.url:http://censuszipcode:8080}")
	private String censuszipcodeURL;
	
	@Bean
	public GetCensusInformationUseCase createGetCensusInformationUseCase() {
		return new GetCensusInformationUseCaseImpl(
						new CensusDemographyRestServiceImpl(
								new HTTPCommunicationGeneral<DemographyRest>(DemographyRest.class), 
								censusdemographyURL), 
						new CensusZipCodeRestServiceImpl(
								new HTTPCommunicationGeneral<ZipCodeRest>(ZipCodeRest.class), 
								censuszipcodeURL));
	}
}
