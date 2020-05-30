package br.com.trevezani.tutorial.demography.infrastructure.delivery.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.tutorial.demography.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.demography.core.exception.ValidationException;
import br.com.trevezani.tutorial.demography.core.usecase.GetDemographyInformationUseCase;
import br.com.trevezani.tutorial.demography.infrastructure.delivery.DemographyController;
import br.com.trevezani.tutorial.demography.infrastructure.delivery.converters.DemographyRestConverter;
import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;
import br.com.trevezani.tutorial.internal.utils.CorrelationUUID;

@RestController
@RequestMapping("/")
public class DemographyControllerImpl implements DemographyController {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final GetDemographyInformationUseCase getDemographyInformationUseCase;
	private final DemographyRestConverter demographyRestConverter;
	
	private final CorrelationUUID correlationUUID;

	public DemographyControllerImpl(final GetDemographyInformationUseCase getDemographyInformationUseCase, final DemographyRestConverter demographyRestConverter, final CorrelationUUID correlationUUID) {
		this.getDemographyInformationUseCase = getDemographyInformationUseCase;
		this.demographyRestConverter = demographyRestConverter;
		this.correlationUUID = correlationUUID;
	}	

	@Override
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = "/demography/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CensusResponse<DemographyRest> getDemographyInformation(@RequestHeader(name = "x-correlation-id", required = false, defaultValue = "na") String correlationId, @PathVariable String zip) throws CensusException {
		try {
			return new CensusResponse<>(String.valueOf(HttpStatus.OK.value()), demographyRestConverter.mapToRest(getDemographyInformationUseCase.execute(zip)));

		} catch (InformationNotExistException | ValidationException e) {
			log.error("[{}] {}", correlationUUID.getCorrelationId(correlationId), e.toString());
			return new CensusResponse<>(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());

		} catch (Exception e) {
			log.error("[{}] {}", correlationUUID.getCorrelationId(correlationId), e.toString());
			throw new CensusException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
}
