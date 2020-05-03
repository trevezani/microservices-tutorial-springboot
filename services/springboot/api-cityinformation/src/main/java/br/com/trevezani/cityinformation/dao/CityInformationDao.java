package br.com.trevezani.cityinformation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class CityInformationDao {
	@Autowired
	@Qualifier("connection")
	private Connection conn;
	
	public Map<String, String> findByZip(final String zip) throws SQLException {
		Map<String, String> returned = new HashMap<>();
		
		String sql = "SELECT state_name, population, density FROM uscities WHERE zips like '%".concat(zip).concat("%'");
		
	    try (PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
            	String state_name = rs.getString("state_name");
            	String population = rs.getString("population");
            	String density = rs.getString("density");
            	
            	returned.put("state_name", state_name);
            	returned.put("population", population);
            	returned.put("density", density);
	        }
	    }
		
		return returned;
	}
}
