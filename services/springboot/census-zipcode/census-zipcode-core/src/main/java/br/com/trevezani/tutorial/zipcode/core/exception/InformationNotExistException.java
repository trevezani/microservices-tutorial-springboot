package br.com.trevezani.tutorial.zipcode.core.exception;

public class InformationNotExistException extends Exception {
	private static final long serialVersionUID = 20771739340925236L;
	
	public String getMessage() {
		return "Information not found";
	}
}

