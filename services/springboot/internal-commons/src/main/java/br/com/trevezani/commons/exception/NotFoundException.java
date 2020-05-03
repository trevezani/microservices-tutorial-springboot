package br.com.trevezani.commons.exception;

public class NotFoundException extends Exception {
	private static final long serialVersionUID = 8555478589819117265L;

	public NotFoundException() {
		super();
	}
	
	public NotFoundException(String message) {
		super(message);
	}
}
