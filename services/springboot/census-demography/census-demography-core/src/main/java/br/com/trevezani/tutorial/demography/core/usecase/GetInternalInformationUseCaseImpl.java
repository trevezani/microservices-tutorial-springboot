package br.com.trevezani.tutorial.demography.core.usecase;

import br.com.trevezani.tutorial.demography.core.Info;
import br.com.trevezani.tutorial.demography.core.InternalBuildProperties;

public class GetInternalInformationUseCaseImpl implements GetInternalInformationUseCase {
	private final InternalBuildProperties buildProperties;
	
	public GetInternalInformationUseCaseImpl(final InternalBuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}	

	@Override
	public Info execute() {
		return new Info(buildProperties.getName(), buildProperties.getVersion());
	}

}
