package br.com.trevezani.tutorial.census.core.usecase;

import br.com.trevezani.tutorial.census.core.Census;
import br.com.trevezani.tutorial.census.core.exeption.ValidationException;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;

public interface GetCensusInformationUseCase {

	public Census execute(final String correlationId, final String zip) throws ValidationException, BusinessException, InternalErrorException;
	
}
