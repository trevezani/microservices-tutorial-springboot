package br.com.trevezani.tutorial.census.core;

import java.io.Serializable;

public class Census implements Serializable {
	private static final long serialVersionUID = 5996992143109751239L;

	private String primaryCity;
	private String type;
	private String state;
	private String stateName;
	private String areaCodes;
	private String population;
	private String density;
	
	public Census() {
		
	}

	public Census(String primaryCity, String type, String state, String stateName, String areaCodes, String population,
			String density) {
		super();
		this.primaryCity = primaryCity;
		this.type = type;
		this.state = state;
		this.stateName = stateName;
		this.areaCodes = areaCodes;
		this.population = population;
		this.density = density;
	}

	public String getPrimaryCity() {
		return primaryCity;
	}

	public void setPrimaryCity(String primaryCity) {
		this.primaryCity = primaryCity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getAreaCodes() {
		return areaCodes;
	}

	public void setAreaCodes(String areaCodes) {
		this.areaCodes = areaCodes;
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
		return "Census [primaryCity=" + primaryCity + ", type=" + type + ", state=" + state + ", stateName=" + stateName
				+ ", areaCodes=" + areaCodes + ", population=" + population + ", density=" + density + "]";
	}
}
