package br.com.trevezani.tutorial.zipcode.infrastructure.delivery.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.InfoRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;
import br.com.trevezani.tutorial.zipcode.core.usecase.GetInternalInformationUseCase;
import br.com.trevezani.tutorial.zipcode.infrastructure.delivery.InternalInformationController;
import br.com.trevezani.tutorial.zipcode.infrastructure.delivery.converters.InformationRestConverter;

@RestController
@RequestMapping("/")
public class InternalInformationControllerImpl implements InternalInformationController {
	private final GetInternalInformationUseCase getInternalInformationUseCase;
	private final InformationRestConverter informationRestConverter;
	
	public InternalInformationControllerImpl(final GetInternalInformationUseCase getInternalInformationUseCase, final InformationRestConverter informationRestConverter) {
		this.getInternalInformationUseCase = getInternalInformationUseCase;
		this.informationRestConverter = informationRestConverter;
	}	

	@Override
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public CensusResponse<InfoRest> getInternalInformation() throws CensusException {
		return new CensusResponse<>(String.valueOf(HttpStatus.OK.value()), informationRestConverter.mapToRest(getInternalInformationUseCase.execute()));
	}
}
