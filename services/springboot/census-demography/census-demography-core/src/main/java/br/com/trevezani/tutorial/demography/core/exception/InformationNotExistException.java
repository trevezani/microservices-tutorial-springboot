package br.com.trevezani.tutorial.demography.core.exception;

public class InformationNotExistException extends Exception {
	private static final long serialVersionUID = -5968344108427963578L;

	public String getMessage() {
		return "Information not found";
	}
}

