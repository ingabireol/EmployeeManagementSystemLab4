package com.olim.employeemanagementsystem.service;

import com.olim.employeemanagementsystem.model.Employee;

import java.util.List;

public interface SortService<T> {
    List<Employee<T>> findSortedBySalary();
    List<Employee<T>> findSortedByPerformanceRating();
    List<Employee<T>> findSortedByExperience();
}
