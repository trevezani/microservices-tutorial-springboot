package br.com.trevezani.tutorial.zipcode.infrastructure.configuration;

import java.sql.Connection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.trevezani.tutorial.internal.utils.CorrelationUUID;
import br.com.trevezani.tutorial.zipcode.core.InternalBuildProperties;
import br.com.trevezani.tutorial.zipcode.core.usecase.GetInternalInformationUseCaseImpl;
import br.com.trevezani.tutorial.zipcode.core.usecase.GetZipInformationUseCaseImpl;
import br.com.trevezani.tutorial.zipcode.infrastructure.delivery.converters.InformationRestConverter;
import br.com.trevezani.tutorial.zipcode.infrastructure.delivery.converters.ZipCodeRestConverter;
import br.com.trevezani.tutorial.zipcode.infrastructure.persistence.impl.ZipCodeServiceImpl;

@Configuration
public class ZipCodeConfiguration {
	@Autowired
	@Qualifier("connection")
	private Connection connection;
	
	@Autowired
	private BuildProperties buildProperties;	
	
	@Bean
	public CorrelationUUID createCorrelationUUID() {
		return new CorrelationUUID();
	}

	@Bean
	public ZipCodeRestConverter createZipCodeRestConverter() {
		return new ZipCodeRestConverter();
	}

	@Bean
	public InformationRestConverter createInformationRestConverter() {
		return new InformationRestConverter();
	}
	
	@Bean
	public GetZipInformationUseCaseImpl createGetZipInformationUseCase() {
		return new GetZipInformationUseCaseImpl(new ZipCodeServiceImpl(connection));
	}
	
	@Bean
	public GetInternalInformationUseCaseImpl createGetInternalInformationUseCase() {
		return new GetInternalInformationUseCaseImpl(Optional.of(buildProperties).map(this::map).orElse(new InternalBuildProperties()));
	}
	
	private InternalBuildProperties map(BuildProperties buildProperties) {
		return new InternalBuildProperties(buildProperties.getName(), buildProperties.getVersion());
	}
}
