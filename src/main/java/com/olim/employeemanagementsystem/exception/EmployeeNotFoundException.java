package com.olim.employeemanagementsystem.exception;

public class EmployeeNotFoundException extends Exception{
    public EmployeeNotFoundException() {
        super("Employee searched not found");
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
