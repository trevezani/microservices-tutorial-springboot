package br.com.trevezani.zipcode.dao;

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
public class ZipCodeDao {
	@Autowired
	@Qualifier("connection")
	private Connection connection;
	
	public Map<String, String> findByZip(final String zip) throws SQLException {
		Map<String, String> returned = new HashMap<>();
		
		String sql = String.format("SELECT primary_city, state, type, decommissioned, area_codes FROM zip_code_database WHERE zip like '%s'", zip);
		
	    try (PreparedStatement stmt = connection.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
            	String primary_city = rs.getString("primary_city");
            	String state = rs.getString("state");
            	String type = rs.getString("type");
            	String decommissioned = rs.getString("decommissioned");
            	String area_codes = rs.getString("area_codes");
            	
            	returned.put("primary_city", primary_city);
            	returned.put("state", state);
            	returned.put("type", type);
            	
            	if (decommissioned != null) {
            		returned.put("decommissioned", decommissioned.toString());
            	}
            	
            	if (area_codes != null) {
            		returned.put("area_codes", area_codes.toString());
            	}
	        }
	    }
		
		return returned;
	}
}
