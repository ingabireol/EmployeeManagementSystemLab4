package com.olim.employeemanagementsystem.service;

import com.olim.employeemanagementsystem.model.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface  SearchService<T> {
    List<Employee<T>> findByDepartment(String department);
    List<Employee<T>> findByName(String name);
    List<Employee<T>> findByRating(double rating);
    List<Employee<T>> findBySalaryBetween(double min, double max);
    void displayAll();
    Map<String,List<Employee<T>>> groupByDepartment();
}
