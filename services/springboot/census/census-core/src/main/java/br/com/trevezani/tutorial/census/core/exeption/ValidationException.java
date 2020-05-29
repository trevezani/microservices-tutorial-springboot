package br.com.trevezani.tutorial.census.core.exeption;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 155617173894109074L;

	public ValidationException(final String message) {
		super(message);
	}
}

