package br.com.trevezani.tutorial.zipcode.infrastructure.delivery.converters;

import br.com.trevezani.tutorial.internal.converter.RestConverter;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;
import br.com.trevezani.tutorial.zipcode.core.ZipCode;

public class ZipCodeRestConverter implements RestConverter<ZipCodeRest, ZipCode> {

	@Override
	public ZipCode mapToEntity(final ZipCodeRest rest) {
		return new ZipCode(rest.getPrimaryCity(), rest.getState(), rest.getType(), rest.getDecommissioned(), rest.getAreaCodes());
	}

	@Override
	public ZipCodeRest mapToRest(final ZipCode entity) {
		return new ZipCodeRest(entity.getPrimaryCity(), entity.getState(), entity.getType(), entity.getDecommissioned(), entity.getAreaCodes());
	}
	
}
