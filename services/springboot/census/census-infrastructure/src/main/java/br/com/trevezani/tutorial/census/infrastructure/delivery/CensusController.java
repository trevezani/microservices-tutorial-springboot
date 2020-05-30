package br.com.trevezani.tutorial.census.infrastructure.delivery;

import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.CensusRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;

public interface CensusController {

	CensusResponse<CensusRest> getCensusInformation(String correlationId, String zip) throws CensusException;	
	
}
