package br.com.trevezani.tutorial.internal.delivery.rest;

import java.io.Serializable;

public class DemographyRest implements Serializable {
	private static final long serialVersionUID = -6153470786545851391L;

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

	@Override
	public String toString() {
		return "Demography [stateName=" + stateName + ", population=" + population + ", density=" + density + "]";
	}
}
