package com.olim.employeemanagementsystem.comparator;

import com.olim.employeemanagementsystem.model.Employee;

import java.util.Comparator;

public class EmployeePerformanceComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        return Double.compare(e2.getSalary(),e1.getSalary());
    }
}
