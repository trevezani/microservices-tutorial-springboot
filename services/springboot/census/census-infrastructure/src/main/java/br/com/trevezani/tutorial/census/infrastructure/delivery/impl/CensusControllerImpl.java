package br.com.trevezani.tutorial.census.infrastructure.delivery.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.tutorial.census.core.exeption.ValidationException;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCase;
import br.com.trevezani.tutorial.census.infrastructure.delivery.CensusController;
import br.com.trevezani.tutorial.census.infrastructure.delivery.converters.CensusRestConverter;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.CensusRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;
import br.com.trevezani.tutorial.internal.utils.CorrelationUUID;

@RestController
@RequestMapping("/")
public class CensusControllerImpl implements CensusController {
	private final GetCensusInformationUseCase getCensusInformationUseCase;
	private final CensusRestConverter censusRestConverter;
	
	private final CorrelationUUID correlationUUID;
	
	public CensusControllerImpl(final GetCensusInformationUseCase getCensusInformationUseCase, final CensusRestConverter censusRestConverter, final CorrelationUUID correlationUUID) {
		this.getCensusInformationUseCase = getCensusInformationUseCase;
		this.censusRestConverter = censusRestConverter;
		this.correlationUUID = correlationUUID;
	}	

	@Override
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = "/census/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CensusResponse<CensusRest> getCensusInformation(@RequestHeader(name = "x-correlation-id", required = false, defaultValue = "na") String correlationId, @PathVariable String zip) throws CensusException {
		try {
			return new CensusResponse<>(String.valueOf(HttpStatus.OK.value()), censusRestConverter.mapToRest(getCensusInformationUseCase.execute(correlationUUID.getCorrelationId(correlationId), zip)));
		} catch (ValidationException | BusinessException e) {
			return new CensusResponse<>(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
		} catch (Exception e) {
			throw new CensusException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
}
