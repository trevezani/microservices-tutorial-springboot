package br.com.trevezani.zipcode.controller;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.commons.exception.NotFoundException;
import br.com.trevezani.commons.utils.ReturnMessage;
import br.com.trevezani.zipcode.service.ZipCodeService;

@RestController
public class ZipCodeController {
	@Autowired
	private ZipCodeService zipCodeService; 

	@Autowired
	private ReturnMessage returnMessage;

    @GetMapping(path = "/zipcode/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> zip(@PathVariable String zip) {
		try {
			return ResponseEntity.ok().body(zipCodeService.findByZip(zip));
			
		} catch (ValidationException | NotFoundException e) {
			return new ResponseEntity<>(returnMessage.getMessageJSON(e.getMessage()), HttpStatus.BAD_REQUEST);
			
		} catch (Exception e) {
			return new ResponseEntity<>(returnMessage.getMessageJSON(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
}
