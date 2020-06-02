package br.com.trevezani.tutorial.internal.communication;

import java.util.Optional;

import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.communication.exception.ServiceNotAvailableException;

public interface HTTPCommunication<T> {

	public Optional<T> callGetService(final String correlationId, final String service, final String url) throws ServiceNotAvailableException, InternalErrorException, BusinessException;
	
}
