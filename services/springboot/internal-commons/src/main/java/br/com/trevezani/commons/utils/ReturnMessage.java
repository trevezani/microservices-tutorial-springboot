package br.com.trevezani.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReturnMessage {
	@Autowired
	private CorrelationSession correlationSession;

	public Map<String, String> getMessageJSON(final String message) {
		Map<String, String> error = new HashMap<>();
		error.put("x-correlation-id", correlationSession.getCorrelationId());
		error.put("message", message);
		return error;
	}
}
