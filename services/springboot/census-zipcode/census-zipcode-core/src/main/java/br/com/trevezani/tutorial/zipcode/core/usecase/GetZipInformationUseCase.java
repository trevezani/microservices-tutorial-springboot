package br.com.trevezani.tutorial.zipcode.core.usecase;

import java.sql.SQLException;

import br.com.trevezani.tutorial.zipcode.core.ZipCode;
import br.com.trevezani.tutorial.zipcode.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.zipcode.core.exception.ValidationException;

public interface GetZipInformationUseCase {

	public ZipCode execute(String zip) throws InformationNotExistException, ValidationException, SQLException;
	
}
