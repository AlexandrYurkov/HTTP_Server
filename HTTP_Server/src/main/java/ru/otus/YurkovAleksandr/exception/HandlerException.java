package ru.otus.YurkovAleksandr.exception;

public class HandlerException extends RuntimeException {

    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandlerException(Throwable cause) {
        super(cause);
    }
}
