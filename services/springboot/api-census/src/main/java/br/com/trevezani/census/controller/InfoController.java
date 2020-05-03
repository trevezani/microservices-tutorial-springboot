package br.com.trevezani.census.controller;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.census.service.InfoService;
import br.com.trevezani.commons.exception.BusinessException;
import br.com.trevezani.commons.exception.InternalErrorException;
import br.com.trevezani.commons.utils.ReturnMessage;

@RestController
public class InfoController {
	@Autowired
	private InfoService infoService;

	@Autowired
	private ReturnMessage returnMessage;

    @GetMapping(path = "/info/zip/{zip}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> info(@PathVariable String zip) {
		try {
			return ResponseEntity.ok().body(infoService.infoByZip(zip));
			
		} catch (ValidationException | BusinessException e) {
			return new ResponseEntity<>(returnMessage.getMessageJSON(e.getMessage()), HttpStatus.BAD_REQUEST);
			
		} catch (InternalErrorException e) {
			return new ResponseEntity<>(returnMessage.getMessageJSON(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (Exception e) {
			return new ResponseEntity<>(returnMessage.getMessageJSON(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}    	
    }
}
