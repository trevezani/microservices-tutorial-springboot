package br.com.trevezani.commons.exception;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 7469584577884662142L;

	public ValidationException() {
		super();
	}
	
	public ValidationException(String message) {
		super(message);
	}
}
