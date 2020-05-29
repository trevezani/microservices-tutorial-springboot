package br.com.trevezani.tutorial.demography.infrastructure.delivery;

import br.com.trevezani.tutorial.internal.delivery.responses.CensusResponse;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.exceptions.CensusException;

public interface DemographyController {

	CensusResponse<DemographyRest> getDemographyInformation(String zip) throws CensusException;	

}
