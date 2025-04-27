package com.olim.employeemanagementsystem.exception;

public class InvalidSalaryException extends Exception{
    public InvalidSalaryException(){
        super("Salary provided is invalid");
    }
    public InvalidSalaryException(String message){
        super(message);
    }
    public InvalidSalaryException(String message, Throwable cause){
        super(message,cause);
    }
}
