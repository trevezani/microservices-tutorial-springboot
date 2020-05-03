package br.com.trevezani.commons.utils;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CorrelationSession implements Serializable {
	private static final long serialVersionUID = -6208827028107358950L;

	private String correlationId;
	
	public CorrelationSession() {}

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
