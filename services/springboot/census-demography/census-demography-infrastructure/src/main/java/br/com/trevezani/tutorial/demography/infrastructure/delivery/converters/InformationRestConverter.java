package br.com.trevezani.tutorial.demography.infrastructure.delivery.converters;

import br.com.trevezani.tutorial.demography.core.Info;
import br.com.trevezani.tutorial.internal.converter.RestConverter;
import br.com.trevezani.tutorial.internal.delivery.rest.InfoRest;

public class InformationRestConverter implements RestConverter<InfoRest, Info> {

	@Override
	public Info mapToEntity(final InfoRest rest) {
		return new Info(rest.getArtifact(), rest.getVersion());
	}

	@Override
	public InfoRest mapToRest(final Info entity) {
		return new InfoRest(entity.getArtifact(), entity.getVersion());
	}
	
}
