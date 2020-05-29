package br.com.trevezani.tutorial.census.core.port;

import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;

public interface CensusDemographyRestService {

	public DemographyRest call(final String correlationId, final String zip) throws InternalErrorException, BusinessException;
	
}
