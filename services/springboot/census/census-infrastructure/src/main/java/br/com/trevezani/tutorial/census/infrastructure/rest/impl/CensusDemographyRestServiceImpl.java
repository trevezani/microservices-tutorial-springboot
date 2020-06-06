package br.com.trevezani.tutorial.census.infrastructure.rest.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.trevezani.tutorial.census.core.port.CensusDemographyRestService;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunicationGeneral;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;

public class CensusDemographyRestServiceImpl implements CensusDemographyRestService {
	Logger log = LoggerFactory.getLogger(this.getClass());	
	
	private static final String SERVICE = "census-demography";

	private final HTTPCommunicationGeneral<DemographyRest> httpCommunication;
	
	private final String remoteURL;
	
	public CensusDemographyRestServiceImpl(final HTTPCommunicationGeneral<DemographyRest> httpCommunication, final String remoteURL) {
		this.httpCommunication = httpCommunication;
		this.remoteURL = remoteURL;
	}

	@Override
	public DemographyRest call(final String correlationId, final String zip) throws InternalErrorException, BusinessException {
		final String url = remoteURL.concat("/demography/").concat(zip);
		
		try {
			return this.internal(correlationId, url);
		} catch (ServiceNotAvailableException e) {
			return fallback(correlationId, e.getMessage());
		}
	}

	private DemographyRest internal(final String correlationId, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		DemographyRest rest = new DemographyRest();
		
		httpCommunication.callGetService(correlationId, SERVICE, url)
				.ifPresent(m -> {
					rest.setStateName(m.getStateName());
					rest.setPopulation(m.getPopulation());
					rest.setDensity(m.getDensity());
				});

		return rest;
	}
	
	private DemographyRest fallback(final String correlationId, String message) {
		log.error("Fallback :: {}", message);

		DemographyRest rest = new DemographyRest();
		rest.setFallback(Boolean.TRUE);
		rest.setStateName("NA");
		rest.setPopulation("NA");
		rest.setDensity("NA");

		return rest;
	}
}
