package br.com.trevezani.tutorial.demography.infrastructure.delivery.converters;

import br.com.trevezani.tutorial.demography.core.Demography;
import br.com.trevezani.tutorial.internal.converter.RestConverter;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;

public class DemographyRestConverter implements RestConverter<DemographyRest, Demography> {

	@Override
	public Demography mapToEntity(final DemographyRest rest) {
		return new Demography(rest.getStateName(), rest.getPopulation(), rest.getDensity());
	}

	@Override
	public DemographyRest mapToRest(final Demography entity) {
		return new DemographyRest(entity.getStateName(), entity.getPopulation(), entity.getDensity());
	}
	
}
