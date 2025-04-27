package com.olim.employeemanagementsystem.exception;

public class EmptyAttributeException extends Exception{
    public EmptyAttributeException() {
    }

    public EmptyAttributeException(String message) {
        super(message);
    }

    public EmptyAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
