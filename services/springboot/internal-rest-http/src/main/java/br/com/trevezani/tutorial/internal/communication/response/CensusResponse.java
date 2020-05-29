package br.com.trevezani.tutorial.internal.communication.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CensusResponse<T> implements Serializable {
	private static final long serialVersionUID = 3138294888207959555L;

	private LocalDateTime timestamp;
	private String status;
	private String message;
	private T data;

	public CensusResponse() {
		super();
	}

	public CensusResponse(String status, T data) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.data = data;
	}

	public CensusResponse(String status, String message) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.message = message;
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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "CensusResponse [timestamp=" + timestamp + ", status=" + status + ", message=" + message + ", data="
				+ data + "]";
	}
}
