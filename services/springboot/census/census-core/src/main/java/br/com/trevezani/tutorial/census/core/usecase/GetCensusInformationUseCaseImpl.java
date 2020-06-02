package br.com.trevezani.tutorial.census.core.usecase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

		ExecutorService executorService = Executors.newCachedThreadPool();
		
		CompletableFuture<ZipCodeRest> callZipCodeRest = CompletableFuture.supplyAsync(() -> {
			try {
				return censusZipCodeRestService.call(correlationId, zip);
			} catch (BusinessException | InternalErrorException e) {
				log.error("[{}] Call ZipCode Exception: {}", correlationId, e.getMessage());
				throw new CompletionException(e);
			}
		}, executorService);

		CompletableFuture<DemographyRest> callDemographyRest = CompletableFuture.supplyAsync(() -> {
			try {
				return censusDemographyRestService.call(correlationId, zip);
			} catch (BusinessException | InternalErrorException e) {
				log.error("[{}] Call Demography Exception: {}", correlationId, e.getMessage());
				throw new CompletionException(e);
			}
		}, executorService);

		try {
			return processCalls(callZipCodeRest, callDemographyRest);		
		} finally {
			executorService.shutdown();
		}
	}
	
	private Census processCalls(CompletableFuture<ZipCodeRest> callZipCodeRest, CompletableFuture<DemographyRest> callDemographyRest) throws BusinessException, InternalErrorException {
		Map<String, String> exceptionMessage = new HashMap<>();
		
		ZipCodeRest zipCode = null;
		DemographyRest demography = null;

		try {
			zipCode = callZipCodeRest.get();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() != null) {
				if (e.getCause() instanceof BusinessException) {
					exceptionMessage.put("ZipCode", e.getCause().getMessage());
				} else {
					throw new InternalErrorException(e.getCause().getMessage());
				}
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
		
		try {
			demography = callDemographyRest.get();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() != null) {
				if (e.getCause() instanceof BusinessException) {
					exceptionMessage.put("Demography", e.getCause().getMessage());
				} else {
					throw new InternalErrorException(e.getCause().getMessage());
				}
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}		
		
		if (!exceptionMessage.isEmpty()) {
			final String message = exceptionMessage.entrySet()
											.stream()
											.map(entry -> String.join(": ", entry.getKey(),entry.getValue()))
											.collect(Collectors.joining(", "));
			
			throw new BusinessException(message);
		}
		
		return merge.apply(zipCode, demography);
	}

	private BiFunction<ZipCodeRest, DemographyRest, Census> merge = (zipCode, demography) -> {
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
