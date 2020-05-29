package br.com.trevezani.tutorial.demography.infrastructure.persistence.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import br.com.trevezani.tutorial.demography.core.Demography;
import br.com.trevezani.tutorial.demography.core.port.DemographyRepositoryService;

public class DemographyServiceImpl implements DemographyRepositoryService {
	private final Connection connection;
	
	public DemographyServiceImpl(final Connection connection) {
		this.connection = connection;
	}	
	
	@Override
	public Optional<Demography> findByZip(String zip) throws SQLException {
		Optional<Demography> demography = Optional.empty();
		
		String sql = "SELECT state_name, population, density FROM uscities WHERE zips like '%".concat(zip).concat("%'");
		
	    try (PreparedStatement stmt = connection.prepareStatement(sql);
	    	 ResultSet rs = stmt.executeQuery()) {
	    	
	        while (rs.next()) {
            	String state_name = rs.getString("state_name");
            	String population = rs.getString("population");
            	String density = rs.getString("density");
            	
            	demography = Optional.of(new Demography(state_name, population, density));
	        }
	        
	    }
		
		return demography;
	}
}
