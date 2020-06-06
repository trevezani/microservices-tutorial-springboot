package br.com.trevezani.tutorial.zipcode.infrastructure.delivery.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;
import br.com.trevezani.tutorial.internal.log.LoggerUtils;
import br.com.trevezani.tutorial.zipcode.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.zipcode.core.exception.ValidationException;
import br.com.trevezani.tutorial.zipcode.core.usecase.GetZipInformationUseCase;
import br.com.trevezani.tutorial.zipcode.infrastructure.delivery.ZipCodeController;
import br.com.trevezani.tutorial.zipcode.infrastructure.delivery.converters.ZipCodeRestConverter;

@RestController
@RequestMapping("/")
public class ZipCodeControllerImpl implements ZipCodeController {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final GetZipInformationUseCase getZipInformationUseCase;
	private final ZipCodeRestConverter zipCodeRestConverter;

	public ZipCodeControllerImpl(final GetZipInformationUseCase getZipInformationUseCase, final ZipCodeRestConverter zipCodeRestConverter) {
		this.getZipInformationUseCase = getZipInformationUseCase;
		this.zipCodeRestConverter = zipCodeRestConverter;
	}	

	@Override
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = "/zipcode/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CensusResponse<ZipCodeRest> getZipInformation(@PathVariable String zip) throws CensusException {
		try {
			return new CensusResponse<>(String.valueOf(HttpStatus.OK.value()), zipCodeRestConverter.mapToRest(getZipInformationUseCase.execute(zip)));

		} catch (InformationNotExistException | ValidationException e) {
			log.info(String.format("Exception: %s [zip: %s]", e.getMessage(), zip));
			return new CensusResponse<>(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());

		} catch (Exception e) {
			log.error(LoggerUtils.format(e.getMessage(), e));
			throw new CensusException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
}
