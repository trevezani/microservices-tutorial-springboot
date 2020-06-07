package br.com.trevezani.tutorial.gateway.rest;

public class CustomErrorResponse {
	private String timestamp;
	private String status;
	private String message;

	public CustomErrorResponse() {
		
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
