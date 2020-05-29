package br.com.trevezani.tutorial.demography.core.port;

import java.sql.SQLException;
import java.util.Optional;

import br.com.trevezani.tutorial.demography.core.Demography;

public interface DemographyRepositoryService {

	public Optional<Demography> findByZip(final String zip) throws SQLException;
	
}