package com.nbk.test.exception;

public class CalError extends Exception {
    public CalError() {
    }

    public CalError(String message) {
        super(message);
    }

    public CalError(String message, Throwable cause) {
        super(message, cause);
    }

    public CalError(Throwable cause) {
        super(cause);
    }

    public CalError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
