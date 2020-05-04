package br.com.trevezani.census.communication;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.trevezani.commons.communication.HTTPCommunication;
import br.com.trevezani.commons.utils.CorrelationSession;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class ZipCodeService {
	Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String BACKEND = "backendA";
	private static final String SERVICE = "api-zipcode";

	@Autowired
	private CorrelationSession correlationSession;

	@Autowired
	private HTTPCommunication httpCommunication;

	@Value("${zipcode.api.url:http://zipcode:8080}")
	private String remoteURL;

	@CircuitBreaker(name = BACKEND, fallbackMethod = "fallbackCB")
	@Retry(name = BACKEND, fallbackMethod = "fallbackRT")
	public Map<String, String> call(String zip) throws Exception {
		Map<String, String> info = new HashMap<>();

		final String url = remoteURL.concat("/zipcode/").concat(zip);

		httpCommunication.callService(correlationSession.getCorrelationId(), SERVICE, url).ifPresent(m -> {
			info.put("type", m.get("type"));
			info.put("primary_city", m.get("primary_city"));
			info.put("area_codes", m.get("area_codes"));
			info.put("state", m.get("state"));
		});

		return info;
	}

	@SuppressWarnings("unused")
	private Map<String, String> fallbackCB(String zip, Exception ex) {
		log.error("[{}] Fallback Circuit Breaker ({}) :: {}", correlationSession.getCorrelationId(), SERVICE, ex.getMessage());

		return fallback();
	}

	@SuppressWarnings("unused")
	private Map<String, String> fallbackRT(String zip, Exception ex) {
		log.error("[{}] Fallback Retry ({}) :: {}", correlationSession.getCorrelationId(), SERVICE, ex.getMessage());

		return fallback();
	}

	private Map<String, String> fallback() {
		Map<String, String> info = new HashMap<>();
		info.put("type", "NA");
		info.put("primary_city", "NA");
		info.put("area_codes", "NA");
		info.put("state", "NA");

		return info;
	}
}
