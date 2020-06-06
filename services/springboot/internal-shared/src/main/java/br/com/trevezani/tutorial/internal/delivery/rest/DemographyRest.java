package br.com.trevezani.tutorial.internal.delivery.rest;

import java.io.Serializable;

public class DemographyRest implements Serializable {
	private static final long serialVersionUID = -6153470786545851391L;

	private Boolean fallback = Boolean.FALSE;
	
	private String stateName;
	private String population;
	private String density;

	public DemographyRest() {

	}

	public DemographyRest(String stateName, String population, String density) {
		super();
		this.stateName = stateName;
		this.population = population;
		this.density = density;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public String getDensity() {
		return density;
	}

	public void setDensity(String density) {
		this.density = density;
	}

	public Boolean getFallback() {
		return fallback;
	}

	public void setFallback(Boolean fallback) {
		this.fallback = fallback;
	}

	@Override
	public String toString() {
		return "DemographyRest [fallback=" + fallback + ", stateName=" + stateName + ", population=" + population
				+ ", density=" + density + "]";
	}
}
