package br.com.trevezani.tutorial.census.core.usecase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.trevezani.tutorial.census.core.Census;
import br.com.trevezani.tutorial.census.core.exeption.ValidationException;
import br.com.trevezani.tutorial.census.core.port.CensusDemographyRestService;
import br.com.trevezani.tutorial.census.core.port.CensusZipCodeRestService;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;

public class GetCensusInformationUseCaseImpl implements GetCensusInformationUseCase {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final CensusDemographyRestService censusDemographyRestService;
	private final CensusZipCodeRestService censusZipCodeRestService;
	
	public GetCensusInformationUseCaseImpl(final CensusDemographyRestService censusDemographyRestService, final CensusZipCodeRestService censusZipCodeRestService) {
		this.censusDemographyRestService = censusDemographyRestService;
		this.censusZipCodeRestService = censusZipCodeRestService;
	}
	
	@Override
	public Census execute(final String correlationId, final String zip) throws ValidationException, BusinessException, InternalErrorException {
		if (zip == null || zip.isBlank()) {
			throw new ValidationException("Zip value is invalid");
		}
		
		Map<String, String> exception = new HashMap<>();
		
		ZipCodeRest zipCode = null;
		
		try {
			zipCode = censusZipCodeRestService.call(correlationId, zip);
		} catch (BusinessException e) {
			exception.put("ZipCode", e.getMessage());
			
			log.error("[{}] Call ZipCode Error: {}", correlationId, e.getMessage());
		}
		
		DemographyRest demography = null;
		
		try {
			demography = censusDemographyRestService.call(correlationId, zip);
		} catch (BusinessException e) {
			exception.put("Demography", e.getMessage());

			log.error("[{}] Call Demography Error: {}", correlationId, e.getMessage());
		}
		
		if (!exception.isEmpty()) {
			final String message = exception.entrySet()
											.stream()
											.map(entry -> String.join(": ", entry.getKey(),entry.getValue()))
											.collect(Collectors.joining(", "));
			
			throw new BusinessException(message);
		}
		
		return merge.apply(zipCode, demography);
	}
	
	public BiFunction<ZipCodeRest, DemographyRest, Census> merge = (zipCode, demography) -> {
		Census census = new Census();
		census.setPrimaryCity(zipCode.getPrimaryCity());
		census.setState(zipCode.getState());
		census.setType(zipCode.getType());
		census.setAreaCodes(zipCode.getAreaCodes());
		census.setStateName(demography.getStateName());
		census.setPopulation(demography.getPopulation());
		census.setDensity(demography.getDensity());

		return census;
	};
}
