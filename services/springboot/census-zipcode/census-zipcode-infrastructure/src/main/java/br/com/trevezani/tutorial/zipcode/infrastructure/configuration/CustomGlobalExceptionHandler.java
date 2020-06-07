package br.com.trevezani.tutorial.zipcode.infrastructure.configuration;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody Map<String, Object> handle(HttpServletRequest request, Exception ex) {
    	Map<String, Object> error = new LinkedHashMap<>();
    	error.put("timestamp", LocalDateTime.now());

		if (ex instanceof NullPointerException) {
	    	error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
		} else {
	    	error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
		}

		error.put("message", ex.getMessage());
    	error.put("correlationId", MDC.get("correlationId"));
		
		return error;
    }
    
}