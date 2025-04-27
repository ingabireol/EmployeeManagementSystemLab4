package com.olim.employeemanagementsystem.exception;

public class InvalidDepartmentException extends Exception{
    public InvalidDepartmentException(){
        super("Department provided not found");
    }

    public InvalidDepartmentException(String message) {
        super(message);
    }

    public InvalidDepartmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDepartmentException(Throwable cause) {
        super(cause);
    }
}
