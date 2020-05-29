package br.com.trevezani.tutorial.internal.communication.exception;

public class ServiceNotAvailableException extends Exception {
	private static final long serialVersionUID = -3854204432975699150L;
	
	public ServiceNotAvailableException() {
		super();
	}
	
	public ServiceNotAvailableException(String message) {
		super(message);
	}
}
