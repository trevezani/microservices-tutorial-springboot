package br.com.trevezani.cityinformation.controller;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.cityinformation.service.CityInformationService;
import br.com.trevezani.commons.exception.NotFoundException;
import br.com.trevezani.commons.response.Message;

@RestController
public class CityInformationController {
	@Autowired
	private CityInformationService cityInformationService; 
	
    @GetMapping(path = "/cityinformation/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> zip(@PathVariable String zip) {
		try {
			return ResponseEntity.ok().body(cityInformationService.findByZip(zip));
			
		} catch (ValidationException e) {
			return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
			
		} catch (NotFoundException e) {
			return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
			
		} catch (Exception e) {
			return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
}
