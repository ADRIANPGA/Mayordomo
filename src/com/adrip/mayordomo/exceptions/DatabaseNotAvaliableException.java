package com.adrip.mayordomo.exceptions;

import java.io.IOException;

public class DatabaseNotAvaliableException extends IOException {

	private static final long serialVersionUID = 1L;

	public DatabaseNotAvaliableException(String message) {
		super(message);
	}

}
