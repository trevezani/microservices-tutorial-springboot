package br.com.trevezani.tutorial.census.infrastructure.configuration;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.trevezani.tutorial.census.core.InternalBuildProperties;
import br.com.trevezani.tutorial.census.core.usecase.GetInternalInformationUseCaseImpl;
import br.com.trevezani.tutorial.census.infrastructure.delivery.converters.CensusRestConverter;
import br.com.trevezani.tutorial.census.infrastructure.delivery.converters.InformationRestConverter;
import br.com.trevezani.tutorial.internal.utils.CorrelationUUID;

@Configuration
public class CensusConfiguration {
	@Autowired
	private BuildProperties buildProperties;	
	
	@Bean
	public CorrelationUUID createCorrelationUUID() {
		return new CorrelationUUID();
	}
	
	@Bean
	public InformationRestConverter createInformationRestConverter() {
		return new InformationRestConverter();
	}
	
	@Bean
	public GetInternalInformationUseCaseImpl createGetInternalInformationUseCase() {
		return new GetInternalInformationUseCaseImpl(Optional.of(buildProperties).map(this::map).orElse(new InternalBuildProperties()));
	}

	@Bean
	public CensusRestConverter createCensusRestConverter() {
		return new CensusRestConverter();
	}
	
	private InternalBuildProperties map(BuildProperties buildProperties) {
		return new InternalBuildProperties(buildProperties.getName(), buildProperties.getVersion());
	}
}
