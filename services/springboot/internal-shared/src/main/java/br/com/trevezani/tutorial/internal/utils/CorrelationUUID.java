package br.com.trevezani.tutorial.internal.utils;

import java.io.Serializable;
import java.util.UUID;

public class CorrelationUUID implements Serializable {
	private static final long serialVersionUID = -6208827028107358950L;
	
	public CorrelationUUID() {}

	public String getCorrelationId() {
		return getCorrelationId(null);
	}	
	
	public String getCorrelationId(final String correlationId) {
		if (correlationId == null || correlationId.equals("na")) {
			return UUID.randomUUID().toString();
		}
		
		return correlationId;
	}
}
