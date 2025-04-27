package com.olim.employeemanagementsystem.exception;

public class EmployeeExistsException extends Exception{
    public EmployeeExistsException() {
        super("Employee already exists");
    }

    public EmployeeExistsException(String message) {
        super(message);
    }

    public EmployeeExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
