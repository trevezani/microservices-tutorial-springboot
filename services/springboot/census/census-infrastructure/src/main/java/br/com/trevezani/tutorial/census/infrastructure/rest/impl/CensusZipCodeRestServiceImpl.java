package br.com.trevezani.tutorial.census.infrastructure.rest.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.trevezani.tutorial.census.core.port.CensusZipCodeRestService;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunication;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;

public class CensusZipCodeRestServiceImpl implements CensusZipCodeRestService {
	Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String SERVICE = "census-zipcode";
	
	private final HTTPCommunication<ZipCodeRest> httpCommunication;
	
	private final String remoteURL;

	public CensusZipCodeRestServiceImpl(final HTTPCommunication<ZipCodeRest> httpCommunication, final String remoteURL) {
		this.httpCommunication = httpCommunication;
		this.remoteURL = remoteURL;
	}
	
	@Override
	public ZipCodeRest call(final String correlationId, final String zip) throws InternalErrorException, BusinessException {
		final String url = remoteURL.concat("/zipcode/").concat(zip);

		try {
			return this.internal(correlationId, url);
		} catch (ServiceNotAvailableException e) {
			return fallback(correlationId, e.getMessage());
		}
	}
	
	private ZipCodeRest internal(final String correlationId, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException {
		ZipCodeRest rest = new ZipCodeRest();
		
		httpCommunication.callGetService(correlationId, SERVICE, url)
				.ifPresent(m -> {
					rest.setType(m.getType());
					rest.setPrimaryCity(m.getPrimaryCity());
					rest.setAreaCodes(m.getAreaCodes());
					rest.setState(m.getState());
				});
		
		return rest;
	}

	private ZipCodeRest fallback(final String correlationId, String message) {
		log.error("[{}] Fallback :: {}", correlationId, message);

		ZipCodeRest rest = new ZipCodeRest();
		rest.setType("NA");
		rest.setPrimaryCity("NA");
		rest.setAreaCodes("NA");
		rest.setState("NA");

		return rest;
	}
}
