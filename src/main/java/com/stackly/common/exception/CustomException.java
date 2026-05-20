package com.stackly.common.exception;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {
	
	public CustomException(String message) {
		super(message);
	}

}
