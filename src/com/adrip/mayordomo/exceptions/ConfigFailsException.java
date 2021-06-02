package com.adrip.mayordomo.exceptions;

import java.io.IOException;

public class ConfigFailsException extends IOException {

	private static final long serialVersionUID = 1L;

	public ConfigFailsException(String message) {
		super(message);
	}
}
