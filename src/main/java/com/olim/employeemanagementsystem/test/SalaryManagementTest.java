package com.olim.employeemanagementsystem.test;

import com.olim.employeemanagementsystem.db.EmployeeDatabase;
import com.olim.employeemanagementsystem.exception.InvalidDepartmentException;
import com.olim.employeemanagementsystem.exception.InvalidSalaryException;
import com.olim.employeemanagementsystem.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the salary management functionality of EmployeeDatabase
 */
public class SalaryManagementTest {

    private EmployeeDatabase<Integer> database;

    @BeforeEach
    void setUp() throws InvalidSalaryException, InvalidDepartmentException {
        // Initialize database with sample data
        database = new EmployeeDatabase<>(new HashMap<>());
        
        // Add test employees
        database.addEmployee(new Employee<>(1001, "John Smith", "IT", 75000.0, 4.2, 5, true));
        database.addEmployee(new Employee<>(1002, "Sarah Johnson", "HR", 65000.0, 4.5, 3, true));
        database.addEmployee(new Employee<>(1003, "Michael Chen", "Finance", 85000.0, 3.8, 7, true));
        database.addEmployee(new Employee<>(1004, "Emily Davis", "IT", 95000.0, 4.8, 6, true));
        database.addEmployee(new Employee<>(1005, "Robert Wilson", "Marketing", 72000.0, 3.5, 4, true));
    }

    @Test
    void testGiveSalaryRaiseByPerformanceRating() throws InvalidSalaryException {
        // Give a 10% raise to employees with performance rating >= 4.0
        List<Employee<Integer>> raisedEmployees = database.giveSalaryRaiseByPerformanceRating(4.0, 0.10);
        
        // Should have applied to 3 employees: John, Sarah, and Emily
        assertEquals(3, raisedEmployees.size());
        
        // Verify the salary increases
        for (Employee<Integer> emp : raisedEmployees) {
            switch (emp.getEmployeeId()) {
                case 1001: // John
                    assertEquals(82500.0, emp.getSalary(), 0.01); // 75000 * 1.1 = 82500
                    break;
                case 1002: // Sarah
                    assertEquals(71500.0, emp.getSalary(), 0.01); // 65000 * 1.1 = 71500
                    break;
                case 1004: // Emily
                    assertEquals(104500.0, emp.getSalary(), 0.01); // 95000 * 1.1 = 104500
                    break;
                default:
                    fail("Unexpected employee received a raise: " + emp.getName());
            }
        }
        
        // Check that other employees were not affected
        List<Employee<Integer>> allEmployees = database.findSortedBySalary();
        for (Employee<Integer> emp : allEmployees) {
            if (emp.getEmployeeId() == 1003) { // Michael
                assertEquals(85000.0, emp.getSalary(), 0.01);
            } else if (emp.getEmployeeId() == 1005) { // Robert
                assertEquals(72000.0, emp.getSalary(), 0.01);
            }
        }
    }
    
    @Test
    void testInvalidRaiseParameters() {
        // Negative performance rating
        assertThrows(IllegalArgumentException.class, 
                () -> database.giveSalaryRaiseByPerformanceRating(-1.0, 0.10));
        
        // Performance rating > 5
        assertThrows(IllegalArgumentException.class, 
                () -> database.giveSalaryRaiseByPerformanceRating(5.5, 0.10));
        
        // Negative percentage raise
        assertThrows(InvalidSalaryException.class, 
                () -> database.giveSalaryRaiseByPerformanceRating(4.0, -0.10));
    }
    
    @Test
    void testFindTopHighestPaid() {
        // Get top 3 highest paid employees
        List<Employee<Integer>> topPaid = database.findTopHighestPaid(3);
        
        // Should be sorted by salary (highest first)
        assertEquals(3, topPaid.size());
        assertEquals(1004, topPaid.get(0).getEmployeeId()); // Emily (95000)
        assertEquals(1003, topPaid.get(1).getEmployeeId()); // Michael (85000)
        assertEquals(1001, topPaid.get(2).getEmployeeId()); // John (75000)
    }
    
    @Test
    void testFindTopHighestPaidDefaultCount() {
        // Default should return top 5
        List<Employee<Integer>> topPaid = database.findTopHighestPaid();
        
        // Should return all 5 employees sorted by salary
        assertEquals(5, topPaid.size());
        assertEquals(1004, topPaid.get(0).getEmployeeId()); // Emily (95000)
        assertEquals(1003, topPaid.get(1).getEmployeeId()); // Michael (85000)
        assertEquals(1001, topPaid.get(2).getEmployeeId()); // John (75000)
        assertEquals(1005, topPaid.get(3).getEmployeeId()); // Robert (72000)
        assertEquals(1002, topPaid.get(4).getEmployeeId()); // Sarah (65000)
    }
    
    @Test
    void testCalculateAverageSalaryByDepartment() throws InvalidDepartmentException {
        // Calculate average salary for IT department
        double itAvgSalary = database.calculateAverageSalaryByDepartment("IT");
        
        // IT has 2 employees: John (75000) and Emily (95000)
        assertEquals(85000.0, itAvgSalary, 0.01);
        
        // Calculate average salary for HR department
        double hrAvgSalary = database.calculateAverageSalaryByDepartment("HR");
        
        // HR has 1 employee: Sarah (65000)
        assertEquals(65000.0, hrAvgSalary, 0.01);
    }
    
    @Test
    void testCalculateAverageSalaryForInvalidDepartment() {
        // Try to calculate average for non-existent department
        assertThrows(InvalidDepartmentException.class, 
                () -> database.calculateAverageSalaryByDepartment("NonExistentDept"));
    }
    
    @Test
    void testCalculateAverageSalaryPerDepartment() {
        // Calculate average salary for all departments
        Map<String, Double> deptAvgSalaries = database.calculateAverageSalaryPerDepartment();
        
        // Should have entries for all 4 departments
        assertEquals(4, deptAvgSalaries.size());
        
        // Verify average salaries
        assertEquals(85000.0, deptAvgSalaries.get("IT"), 0.01); // (75000 + 95000) / 2
        assertEquals(65000.0, deptAvgSalaries.get("HR"), 0.01);
        assertEquals(85000.0, deptAvgSalaries.get("Finance"), 0.01);
        assertEquals(72000.0, deptAvgSalaries.get("Marketing"), 0.01);
    }
}