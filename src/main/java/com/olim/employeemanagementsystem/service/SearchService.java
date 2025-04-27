package com.olim.employeemanagementsystem.service;

import com.olim.employeemanagementsystem.exception.InvalidDepartmentException;
import com.olim.employeemanagementsystem.exception.InvalidSalaryException;
import com.olim.employeemanagementsystem.model.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface  SearchService<T> {
    List<Employee<T>> findByDepartment(String department) throws InvalidDepartmentException;
    List<Employee<T>> findByName(String name);
    List<Employee<T>> findByRating(double rating);
    List<Employee<T>> findBySalaryBetween(double min, double max) throws InvalidSalaryException;
    void displayAll();
    Map<String,List<Employee<T>>> groupByDepartment();
}
