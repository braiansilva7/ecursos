package com.ecursos.myapp.service.errors;

public class MilitarServiceException extends RuntimeException {
    public MilitarServiceException(String message) {
        super(message);
    }

    public MilitarServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}