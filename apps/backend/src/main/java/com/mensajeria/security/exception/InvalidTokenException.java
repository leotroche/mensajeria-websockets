package com.mensajeria.security.exception;

import java.io.Serial;

public class InvalidTokenException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String mensaje) {

        super(mensaje);
    }
}
