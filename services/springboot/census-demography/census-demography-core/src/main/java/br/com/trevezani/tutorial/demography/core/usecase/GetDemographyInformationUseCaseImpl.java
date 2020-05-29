package br.com.trevezani.tutorial.demography.core.usecase;

import java.sql.SQLException;
import java.util.Optional;

import br.com.trevezani.tutorial.demography.core.Demography;
import br.com.trevezani.tutorial.demography.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.demography.core.exception.ValidationException;
import br.com.trevezani.tutorial.demography.core.port.DemographyRepositoryService;

public class GetDemographyInformationUseCaseImpl implements GetDemographyInformationUseCase {
	private final DemographyRepositoryService demographyRepositoryService;
	
	public GetDemographyInformationUseCaseImpl(final DemographyRepositoryService demographyRepositoryService) {
		this.demographyRepositoryService = demographyRepositoryService;
	}	
	
	@Override
	public Demography execute(final String zip) throws InformationNotExistException, ValidationException, SQLException {
		if (zip == null || zip.isBlank()) {
			throw new ValidationException("Zip value is invalid");
		}
		
		final Optional<Demography> demography = demographyRepositoryService.findByZip(zip);
		
		return demography.map(this::mapDemography).orElseThrow(() -> new InformationNotExistException());
	}

	public Demography mapDemography(Demography value) {
		return value;
	}
}
