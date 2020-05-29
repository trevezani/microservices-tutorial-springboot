package br.com.trevezani.tutorial.internal.utils;

import java.io.Serializable;
import java.util.UUID;

public class CorrelationUUID implements Serializable {
	private static final long serialVersionUID = -6208827028107358950L;

	private String correlationId;
	
	public CorrelationUUID() {}

	public String getCorrelationId() {
		if (correlationId == null) {
			correlationId = UUID.randomUUID().toString();
		}
		
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
}
