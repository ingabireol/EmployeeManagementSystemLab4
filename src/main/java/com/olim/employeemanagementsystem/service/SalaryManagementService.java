package com.olim.employeemanagementsystem.service;

import com.olim.employeemanagementsystem.model.Employee;

import java.util.List;
import java.util.Map;

public interface SalaryManagementService<T> {
    List<Employee<T>> giveSalaryRaiseByPerformanceRating(double performanceRating,double percentageRaise);
    List<Employee<T>> findTopHighestPaid();
    List<Employee<T>> findTopHighestPaid(int numberOfEmployees);
    double calculateAverageSalaryByDepartment(String department);
    Map<String, Double> calculateAverageSalaryPerDepartment();
}
