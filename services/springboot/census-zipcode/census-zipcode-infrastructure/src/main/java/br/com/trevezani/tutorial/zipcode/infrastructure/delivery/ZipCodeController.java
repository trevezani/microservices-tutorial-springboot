package br.com.trevezani.tutorial.zipcode.infrastructure.delivery;

import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;

public interface ZipCodeController {

	CensusResponse<ZipCodeRest> getZipInformation(String correlationId, String zip) throws CensusException;	
	
}
