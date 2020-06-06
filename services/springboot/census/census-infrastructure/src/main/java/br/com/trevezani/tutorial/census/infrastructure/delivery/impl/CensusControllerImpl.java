package br.com.trevezani.tutorial.census.infrastructure.delivery.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import br.com.trevezani.tutorial.internal.log.LoggerUtils;

@RestController
@RequestMapping("/")
public class CensusControllerImpl implements CensusController {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final GetCensusInformationUseCase getCensusInformationUseCase;
	private final CensusRestConverter censusRestConverter;
	
	public CensusControllerImpl(final GetCensusInformationUseCase getCensusInformationUseCase, final CensusRestConverter censusRestConverter) {
		this.getCensusInformationUseCase = getCensusInformationUseCase;
		this.censusRestConverter = censusRestConverter;
	}	

	@Override
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = "/census/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CensusResponse<CensusRest> getCensusInformation(@PathVariable String zip) throws CensusException {
		try {
			return new CensusResponse<>(String.valueOf(HttpStatus.OK.value()), censusRestConverter.mapToRest(getCensusInformationUseCase.execute(MDC.get("correlationId"), zip)));

		} catch (ValidationException | BusinessException e) {
			log.info(String.format("Exception: %s [zip: %s]", e.getMessage(), zip));
			return new CensusResponse<>(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
	
		} catch (Exception e) {
			log.error(LoggerUtils.format(e.getMessage(), e));
			throw new CensusException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
}
