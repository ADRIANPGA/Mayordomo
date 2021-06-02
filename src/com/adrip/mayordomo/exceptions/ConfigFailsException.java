package com.adrip.mayordomo.exceptions;

import java.io.IOException;
import java.io.Serial;

public class ConfigFailsException extends IOException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ConfigFailsException(String message) {
        super(message);
    }
}
