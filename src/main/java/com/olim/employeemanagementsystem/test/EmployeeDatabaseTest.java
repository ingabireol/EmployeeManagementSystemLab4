package com.olim.employeemanagementsystem.test;

import com.olim.employeemanagementsystem.db.EmployeeDatabase;
import com.olim.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.olim.employeemanagementsystem.exception.InvalidDepartmentException;
import com.olim.employeemanagementsystem.exception.InvalidSalaryException;
import com.olim.employeemanagementsystem.model.Employee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EmployeeDatabase class
 */
public class EmployeeDatabaseTest {

    private EmployeeDatabase<Integer> database;

    @BeforeEach
    void setUp() {
        // Initialize a fresh database before each test
        database = new EmployeeDatabase<>(new HashMap<>());
    }

    @Test
    void testAddEmployee() throws InvalidSalaryException, InvalidDepartmentException {
        // Create a test employee
        Employee<Integer> employee = new Employee<>(1001, "John Doe", "IT", 75000.0, 4.0, 5, true);
        
        // Add the employee to the database
        Integer result = database.addEmployee(employee);
        
        // Verify the employee was added successfully
        assertEquals(1001, result);
        
        // Try to retrieve the employee and verify it exists
        try {
            Employee<Integer> retrievedEmployee = database.getEmployeeById(1001);
            assertNotNull(retrievedEmployee);
            assertEquals("John Doe", retrievedEmployee.getName());
            assertEquals("IT", retrievedEmployee.getDepartment());
            assertEquals(75000.0, retrievedEmployee.getSalary());
        } catch (EmployeeNotFoundException e) {
            fail("Employee should exist but was not found");
        }
    }
    
    @Test
    void testAddEmployeeWithInvalidSalary() {
        // Create an employee with invalid (negative) salary
        Employee<Integer> employee = new Employee<>(1002, "Jane Smith", "HR", -5000.0, 4.0, 3, true);
        
        // Verify that adding this employee throws InvalidSalaryException
        assertThrows(InvalidSalaryException.class, () -> database.addEmployee(employee));
    }
    
    @Test
    void testAddEmployeeWithInvalidDepartment() {
        // Create an employee with invalid department
        Employee<Integer> employee = new Employee<>(1003, "Mike Johnson", "InvalidDept", 65000.0, 4.0, 4, true);
        
        // Verify that adding this employee throws InvalidDepartmentException
        assertThrows(InvalidDepartmentException.class, () -> database.addEmployee(employee));
    }
    
    @Test
    void testDuplicateEmployeeId() throws InvalidSalaryException, InvalidDepartmentException {
        // Add first employee
        Employee<Integer> employee1 = new Employee<>(1001, "John Doe", "IT", 75000.0, 4.0, 5, true);
        database.addEmployee(employee1);
        
        // Try to add another employee with same ID
        Employee<Integer> employee2 = new Employee<>(1001, "Different Name", "Finance", 80000.0, 4.5, 6, true);
        
        // The result should be null as we can't add duplicate IDs
        Integer result = database.addEmployee(employee2);
        assertNull(result);
    }

    @Test
    void testFindByDepartment() throws InvalidDepartmentException, InvalidSalaryException {
        // Add multiple employees with different departments
        database.addEmployee(new Employee<>(1001, "John Doe", "IT", 75000.0, 4.0, 5, true));
        database.addEmployee(new Employee<>(1002, "Jane Smith", "HR", 65000.0, 4.2, 3, true));
        database.addEmployee(new Employee<>(1003, "Mike Johnson", "IT", 80000.0, 3.8, 6, true));
        database.addEmployee(new Employee<>(1004, "Sarah Williams", "Finance", 90000.0, 4.5, 7, true));
        
        // Search for employees in IT department
        List<Employee<Integer>> itEmployees = database.findByDepartment("IT");
        
        // Verify correct employees are found
        assertEquals(2, itEmployees.size());
        assertTrue(itEmployees.stream().anyMatch(emp -> emp.getEmployeeId().equals(1001)));
        assertTrue(itEmployees.stream().anyMatch(emp -> emp.getEmployeeId().equals(1003)));
        
        // Search for employees in HR department
        List<Employee<Integer>> hrEmployees = database.findByDepartment("HR");
        
        // Verify correct employees are found
        assertEquals(1, hrEmployees.size());
        assertTrue(hrEmployees.stream().anyMatch(emp -> emp.getEmployeeId().equals(1002)));
    }
    
    @Test
    void testFindByInvalidDepartment() {
        // Verify that searching for invalid department throws InvalidDepartmentException
        assertThrows(InvalidDepartmentException.class, () -> database.findByDepartment("InvalidDept"));
    }
    
    @Test
    void testRemoveEmployee() throws InvalidSalaryException, InvalidDepartmentException, EmployeeNotFoundException {
        // Add employee
        Employee<Integer> employee = new Employee<>(1001, "John Doe", "IT", 75000.0, 4.0, 5, true);
        database.addEmployee(employee);
        
        // Verify employee exists
        assertNotNull(database.getEmployeeById(1001));
        
        // Remove employee
        Employee<Integer> removed = database.removeEmployee(1001);
        
        // Verify the returned employee is the same one we added
        assertNotNull(removed);
        assertEquals("John Doe", removed.getName());
        
        // Verify employee no longer exists in the database
        assertThrows(EmployeeNotFoundException.class, () -> database.getEmployeeById(1001));
    }
    
    @Test
    void testRemoveNonExistentEmployee() {
        // Try to remove an employee that doesn't exist
        assertThrows(EmployeeNotFoundException.class, () -> database.removeEmployee(9999));
    }
    
    @Test
    void testRemoveEmployeeWithNullId() {
        // Try to remove an employee with null ID
        assertThrows(IllegalArgumentException.class, () -> database.removeEmployee(null));
    }
}