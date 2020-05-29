package br.com.trevezani.tutorial.demography.infrastructure.configuration;

import java.sql.Connection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.trevezani.tutorial.demography.core.InternalBuildProperties;
import br.com.trevezani.tutorial.demography.core.usecase.GetDemographyInformationUseCaseImpl;
import br.com.trevezani.tutorial.demography.core.usecase.GetInternalInformationUseCaseImpl;
import br.com.trevezani.tutorial.demography.infrastructure.delivery.converters.DemographyRestConverter;
import br.com.trevezani.tutorial.demography.infrastructure.delivery.converters.InformationRestConverter;
import br.com.trevezani.tutorial.demography.infrastructure.persistence.impl.DemographyServiceImpl;

@Configuration
public class DemographyConfiguration {
	@Autowired
	@Qualifier("connection")
	private Connection connection;
	
	@Autowired
	private BuildProperties buildProperties;	

	@Bean
	public DemographyRestConverter createDemographyRestConverter() {
		return new DemographyRestConverter();
	}

	@Bean
	public InformationRestConverter createInformationRestConverter() {
		return new InformationRestConverter();
	}
	
	@Bean
	public GetDemographyInformationUseCaseImpl createGetDemographyInformationUseCase() {
		return new GetDemographyInformationUseCaseImpl(new DemographyServiceImpl(connection));
	}
	
	@Bean
	public GetInternalInformationUseCaseImpl createGetInternalInformationUseCase() {
		return new GetInternalInformationUseCaseImpl(Optional.of(buildProperties).map(this::map).orElse(new InternalBuildProperties()));
	}
	
	private InternalBuildProperties map(BuildProperties buildProperties) {
		return new InternalBuildProperties(buildProperties.getName(), buildProperties.getVersion());
	}
}
