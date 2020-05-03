package br.com.trevezani.zipcode.service;

import java.sql.SQLException;
import java.util.Map;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trevezani.commons.exception.BusinessException;
import br.com.trevezani.commons.exception.NotFoundException;
import br.com.trevezani.zipcode.dao.ZipCodeDao;

@Service
public class ZipCodeService {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ZipCodeDao zipCodeDao;
		
	public Map<String, String> findByZip(final String zip) throws ValidationException, BusinessException, NotFoundException {
		if (zip == null || zip.isBlank()) {
			throw new ValidationException("Zip code is invalid");
		}
		
		try {
			Map<String, String> returned = zipCodeDao.findByZip(zip);
			
			if (returned.isEmpty()) {
				throw new NotFoundException("Zip not found");
			}
			
			return returned;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			
			throw new BusinessException(e.getMessage());
		}
	}
}
