package br.com.trevezani.tutorial.zipcode.core.usecase;

import java.sql.SQLException;
import java.util.Optional;

import br.com.trevezani.tutorial.zipcode.core.ZipCode;
import br.com.trevezani.tutorial.zipcode.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.zipcode.core.exception.ValidationException;
import br.com.trevezani.tutorial.zipcode.core.port.ZipCodeRepositoryService;

public class GetZipInformationUseCaseImpl implements GetZipInformationUseCase {
	private final ZipCodeRepositoryService zipCodeRepositoryService;
	
	public GetZipInformationUseCaseImpl(final ZipCodeRepositoryService zipCodeRepositoryService) {
		this.zipCodeRepositoryService = zipCodeRepositoryService;
	}	
	
	@Override
	public ZipCode execute(final String zip) throws InformationNotExistException, ValidationException, SQLException {
		if (zip == null || zip.isBlank()) {
			throw new ValidationException("Zip value is invalid");
		}
		
		final Optional<ZipCode> zipcode = zipCodeRepositoryService.findByZip(zip);
		
		return zipcode.map(this::mapZipCode).orElseThrow(() -> new InformationNotExistException());
	}

	public ZipCode mapZipCode(ZipCode value) {
		return value;
	}
}
