package br.com.trevezani.tutorial.zipcode.core.port;

import java.sql.SQLException;
import java.util.Optional;

import br.com.trevezani.tutorial.zipcode.core.ZipCode;

public interface ZipCodeRepositoryService {

	public Optional<ZipCode> findByZip(final String zip) throws SQLException;
	
}