package com.olim.employeemanagementsystem.display;

import com.olim.employeemanagementsystem.db.EmployeeDatabase;
import com.olim.employeemanagementsystem.model.Employee;

import java.util.HashMap;

public class ReportsDemo {
    public static void main(String[] args) {
        // Create an employee database
        EmployeeDatabase<Integer> database = new EmployeeDatabase<>(new HashMap<>());
        
        database.addEmployee(new Employee<>(1001, "John Smith", "IT", 78500.0, 4.2, 5, true));
        database.addEmployee(new Employee<>(1002, "Sarah Johnson", "HR", 65000.0, 4.5, 3, true));
        database.addEmployee(new Employee<>(1003, "Michael Chen", "Finance", 85000.0, 3.8, 7, true));
        database.addEmployee(new Employee<>(1004, "Emily Davis", "IT", 92000.0, 4.8, 6, true));
        database.addEmployee(new Employee<>(1005, "Robert Wilson", "Marketing", 72000.0, 3.5, 4, true));
        database.addEmployee(new Employee<>(1006, "Jessica Brown", "HR", 67500.0, 4.0, 2, true));
        database.addEmployee(new Employee<>(1007, "David Lee", "IT", 115000.0, 4.7, 9, true));
        database.addEmployee(new Employee<>(1008, "Amanda Miller", "Finance", 79000.0, 3.9, 5, true));
        database.addEmployee(new Employee<>(1009, "Thomas Garcia", "Marketing", 68000.0, 2.8, 3, false));
        database.addEmployee(new Employee<>(1010, "Jennifer Taylor", "Sales", 108000.0, 4.6, 8, true));
        
        System.out.println("==========================================================");
        System.out.println("                  EMPLOYEE REPORTS DEMO                   ");
        System.out.println("==========================================================");
        
        // Department summary report
        EmployeeDisplay.generateDepartmentSummaryReport(database.getAllEmployees());
        
        // Salary distribution report
        EmployeeDisplay.generateSalaryDistributionReport(database.getAllEmployees());
        
        // Performance rating report
        EmployeeDisplay.generatePerformanceReport(database.getAllEmployees());
        
        System.out.println("\n==========================================================");
        System.out.println("                    END OF DEMONSTRATION                   ");
        System.out.println("==========================================================");
    }
}