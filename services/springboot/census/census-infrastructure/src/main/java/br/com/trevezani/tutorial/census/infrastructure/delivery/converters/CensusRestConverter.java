package br.com.trevezani.tutorial.census.infrastructure.delivery.converters;

import br.com.trevezani.tutorial.census.core.Census;
import br.com.trevezani.tutorial.internal.converter.RestConverter;
import br.com.trevezani.tutorial.internal.delivery.rest.CensusRest;

public class CensusRestConverter implements RestConverter<CensusRest, Census> {

	@Override
	public Census mapToEntity(final CensusRest rest) {
		return new Census(rest.getPrimaryCity(), rest.getType(), rest.getState(), rest.getStateName(), rest.getAreaCodes(), rest.getPopulation(), rest.getDensity());
	}

	@Override
	public CensusRest mapToRest(final Census entity) {
		return new CensusRest(entity.getPrimaryCity(), entity.getType(), entity.getState(), entity.getStateName(), entity.getAreaCodes(), entity.getPopulation(), entity.getDensity());
	}
	
}
