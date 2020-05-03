package br.com.trevezani.cityinformation.service;

import java.sql.SQLException;
import java.util.Map;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trevezani.cityinformation.dao.CityInformationDao;
import br.com.trevezani.commons.exception.BusinessException;
import br.com.trevezani.commons.exception.NotFoundException;

@Service
public class CityInformationService {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CityInformationDao cityInformationDao;

	public Map<String, String>  findByZip(String zip) throws ValidationException, BusinessException, NotFoundException {
		if (zip == null || zip.isBlank()) {
			throw new ValidationException("Zip code is invalid");
		}
		
		try {
			Map<String, String> returned = cityInformationDao.findByZip(zip);
			
			if (returned.isEmpty()) {
				throw new NotFoundException("Information not found");
			}
			
			return returned;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			
			throw new BusinessException(e.getMessage());
		}
	}

}
