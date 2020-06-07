package br.com.trevezani.tutorial.gateway.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.trevezani.tutorial.gateway.rest.CustomErrorResponse;

@RestController
@RequestMapping("/")
public class GatewayFallbackController {
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss.SSS");

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = "/fallback", produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomErrorResponse getFallback() {
		CustomErrorResponse error = new CustomErrorResponse();
		error.setTimestamp(LocalDateTime.now().format(formatter));
		error.setStatus(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
		error.setMessage("The current service is currently unavailable. You can try again later.");
		
		return error;
	}	
}
