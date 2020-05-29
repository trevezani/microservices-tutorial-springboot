package br.com.trevezani.tutorial.demography.core.usecase;

import java.sql.SQLException;

import br.com.trevezani.tutorial.demography.core.Demography;
import br.com.trevezani.tutorial.demography.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.demography.core.exception.ValidationException;

public interface GetDemographyInformationUseCase {

	public Demography execute(String zip) throws InformationNotExistException, ValidationException, SQLException;
	
}
