package br.com.trevezani.census.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trevezani.census.communication.CityInformationService;
import br.com.trevezani.census.communication.ZipCodeService;
import br.com.trevezani.commons.utils.CorrelationSession;

@Service
public class InfoService {
	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ZipCodeService zipCodeService;

	@Autowired
	private CityInformationService cityInformationService;

	@Autowired
	private CorrelationSession correlationSession;

	public Map<String, String> infoByZip(final String zip) throws Exception {
		Map<String, String> info = new HashMap<>();

		info.put("x-correlation-id", correlationSession.getCorrelationId());
		
		info.putAll(zipCodeService.call(zip));
		info.putAll(cityInformationService.call(zip));

		return info;
	}
}
