package com.adrip.mayordomo.exceptions;

import java.io.IOException;
import java.io.Serial;

public class DatabaseNotAvaliableException extends IOException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DatabaseNotAvaliableException(String message) {
        super(message);
    }

}
