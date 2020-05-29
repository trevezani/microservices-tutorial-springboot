package br.com.trevezani.tutorial.demography.infrastructure.delivery;

import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.InfoRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;

public interface InternalInformationController {

	CensusResponse<InfoRest> getInternalInformation() throws CensusException;	

}
