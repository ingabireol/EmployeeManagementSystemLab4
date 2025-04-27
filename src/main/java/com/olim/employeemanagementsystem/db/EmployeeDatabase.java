package com.olim.employeemanagementsystem.db;

import com.olim.employeemanagementsystem.comparator.EmployeePerformanceComparator;
import com.olim.employeemanagementsystem.comparator.EmployeeSalaryComparator;
import com.olim.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.olim.employeemanagementsystem.exception.InvalidDepartmentException;
import com.olim.employeemanagementsystem.exception.InvalidSalaryException;
import com.olim.employeemanagementsystem.model.Employee;
import com.olim.employeemanagementsystem.service.SalaryManagementService;
import com.olim.employeemanagementsystem.service.SearchService;
import com.olim.employeemanagementsystem.service.SortService;
import com.olim.employeemanagementsystem.util.LoggingUtility;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmployeeDatabase<T> implements SearchService<T>, SortService<T>, SalaryManagementService<T> {
    private static final Logger logger = Logger.getLogger(EmployeeDatabase.class.getName());

    // List of valid departments
    private static final Set<String> VALID_DEPARTMENTS = new HashSet<>(
            Arrays.asList("IT", "HR", "Finance", "Marketing", "Sales", "Operations", "Legal", "R&D")
    );

    private final HashMap<T, Employee<T>> employees;

    public EmployeeDatabase(HashMap<T, Employee<T>> employees) {
        this.employees = employees;
    }

    /**
     * Adds a new employee to the database after validating their information
     *
     * @param employee The employee to add
     * @return The employee ID if added successfully, null if employee already exists
     * @throws InvalidSalaryException If the salary is negative or invalid
     * @throws InvalidDepartmentException If the department is not valid
     * @throws IllegalArgumentException If any input data is invalid
     */
    public T addEmployee(Employee<T> employee) throws InvalidSalaryException, InvalidDepartmentException {
        try {
            // Validate employee is not null
            if(employee == null) {
                logger.warning("Attempt to add null employee");
                throw new IllegalArgumentException("Employee cannot be empty");
            }

            // Check if employee already exists
            if(employees.containsKey(employee.getEmployeeId())) {
                logger.warning("Attempt to add duplicate employee ID: " + employee.getEmployeeId());
                return null;
            }

            // Validate employee ID
            if(employee.getEmployeeId() == null) {
                logger.warning("Attempt to add employee with null ID");
                throw new IllegalArgumentException("Employee ID cannot be null");
            }

            if(employee.getEmployeeId() instanceof Integer id) {
                if(id < 0) {
                    logger.warning("Attempt to add employee with negative ID: " + id);
                    throw new IllegalArgumentException("Employee ID cannot be negative");
                }
            }

            // Validate employee name
            if(employee.getName() == null || employee.getName().trim().isEmpty()) {
                logger.warning("Attempt to add employee with null or empty name");
                throw new IllegalArgumentException("Employee name cannot be empty");
            }

            // Validate department
            if(employee.getDepartment() == null || employee.getDepartment().trim().isEmpty()) {
                logger.warning("Attempt to add employee with null or empty department");
                throw new IllegalArgumentException("Department cannot be empty");
            }

            if(!VALID_DEPARTMENTS.contains(employee.getDepartment())) {
                logger.warning("Attempt to add employee with invalid department: " + employee.getDepartment());
                throw new InvalidDepartmentException("Department '" + employee.getDepartment() + "' is not valid. Valid departments are: " + VALID_DEPARTMENTS);
            }

            // Validate salary
            if(employee.getSalary() < 0) {
                logger.warning("Attempt to add employee with negative salary: " + employee.getSalary());
                throw new InvalidSalaryException("Salary cannot be negative");
            }

            // Validate performance rating
            if(employee.getPerformanceRating() < 0 || employee.getPerformanceRating() > 5) {
                logger.warning("Attempt to add employee with invalid performance rating: " + employee.getPerformanceRating());
                throw new IllegalArgumentException("Performance rating must be between 0 and 5");
            }

            // Validate years of experience
            if(employee.getYearsOfExperience() < 0) {
                logger.warning("Attempt to add employee with negative years of experience: " + employee.getYearsOfExperience());
                throw new IllegalArgumentException("Years of experience cannot be negative");
            }

            // Add employee to database
            employees.put(employee.getEmployeeId(), employee);
            logger.info("Employee added successfully: " + employee.getEmployeeId());
            return employee.getEmployeeId();

        } catch (InvalidSalaryException | InvalidDepartmentException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error adding employee", e);
            throw new RuntimeException("Failed to add employee: " + e.getMessage(), e);
        }
    }

    /**
     * Removes an employee from the database
     *
     * @param employeeId The ID of the employee to remove
     * @return The removed employee, or null if no employee found with the given ID
     * @throws IllegalArgumentException If the employee ID is null
     */
    public Employee<T> removeEmployee(T employeeId) throws EmployeeNotFoundException {
        try {
            if(employeeId == null) {
                logger.warning("Attempt to remove employee with null ID");
                throw new IllegalArgumentException("Employee ID cannot be null");
            }

            Employee<T> removedEmployee = employees.remove(employeeId);

            if(removedEmployee == null) {
                logger.warning("Attempt to remove non-existent employee: " + employeeId);
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
            }

            logger.info("Employee removed successfully: " + employeeId);
            return removedEmployee;

        } catch (EmployeeNotFoundException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error removing employee", e);
            throw new RuntimeException("Failed to remove employee: " + e.getMessage(), e);
        }
    }

    // Fixed method without the salary division bug
    public Employee<T> updateEmployeeDetails(T employeeId, String field, Object newValue)
            throws EmployeeNotFoundException, InvalidSalaryException, InvalidDepartmentException {
        try {
            if(employeeId == null) {
                logger.warning("Attempt to update employee with null ID");
                throw new IllegalArgumentException("Employee ID cannot be null");
            }

            Employee<T> employee = employees.get(employeeId);

            if(employee == null) {
                logger.warning("Attempt to update non-existent employee: " + employeeId);
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
            }

            switch(field.toLowerCase()) {
                case "name":
                    if (newValue instanceof String) {
                        String name = (String)newValue;
                        if(name == null || name.trim().isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        }
                        employee.setName(name);
                    } else {
                        throw new IllegalArgumentException("Name must be a string");
                    }
                    break;

                case "department":
                    if (newValue instanceof String) {
                        String department = (String)newValue;
                        if(department == null || department.trim().isEmpty()) {
                            throw new IllegalArgumentException("Department cannot be empty");
                        }
                        if(!VALID_DEPARTMENTS.contains(department)) {
                            throw new InvalidDepartmentException("Department '" + department + "' is not valid. Valid departments are: " + VALID_DEPARTMENTS);
                        }
                        employee.setDepartment(department);
                    } else {
                        throw new IllegalArgumentException("Department must be a string");
                    }
                    break;

                case "salary":
                    if (newValue instanceof Number) {
                        double salary = ((Number)newValue).doubleValue();
                        if(salary < 0) {
                            throw new InvalidSalaryException("Salary cannot be negative");
                        }
                        // FIX: Use the actual salary value without division
                        employee.setSalary(salary);
                    } else {
                        throw new IllegalArgumentException("Salary must be a number");
                    }
                    break;

                case "performancerating":
                    if (newValue instanceof Number) {
                        double rating = ((Number)newValue).doubleValue();
                        if(rating < 0 || rating > 5) {
                            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
                        }
                        employee.setPerformanceRating(rating);
                    } else {
                        throw new IllegalArgumentException("Performance rating must be a number");
                    }
                    break;

                case "yearsofexperience":
                    if (newValue instanceof Number) {
                        int years = ((Number)newValue).intValue();
                        if(years < 0) {
                            throw new IllegalArgumentException("Years of experience cannot be negative");
                        }
                        employee.setYearsOfExperience(years);
                    } else {
                        throw new IllegalArgumentException("Years of experience must be a number");
                    }
                    break;

                case "isactive":
                    if (newValue instanceof Boolean) {
                        employee.setActive((Boolean)newValue);
                    } else {
                        throw new IllegalArgumentException("Active status must be a boolean");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid field: " + field);
            }

            logger.info("Employee updated successfully: " + employeeId + ", field: " + field);
            return employee;

        } catch (EmployeeNotFoundException | InvalidSalaryException | InvalidDepartmentException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error updating employee", e);
            throw new RuntimeException("Failed to update employee: " + e.getMessage(), e);
        }
    }
    /**
     * Gets all employees in the database
     *
     * @return A collection of all employees
     */
    public Collection<Employee<T>> getAllEmployees() {
        return employees.values();
    }

    /**
     * Gets an employee by ID
     *
     * @param employeeId The ID of the employee to get
     * @return The employee with the given ID
     * @throws EmployeeNotFoundException If no employee found with the given ID
     */
    public Employee<T> getEmployeeById(T employeeId) throws EmployeeNotFoundException {
        if(employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }

        Employee<T> employee = employees.get(employeeId);

        if(employee == null) {
            throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
        }

        return employee;
    }

    @Override
    public List<Employee<T>> findByDepartment(String department) throws InvalidDepartmentException {
        try {
            if(department == null || department.trim().isEmpty()) {
                throw new IllegalArgumentException("Department cannot be empty");
            }

            if(!VALID_DEPARTMENTS.contains(department)) {
                throw new InvalidDepartmentException("Department '" + department + "' is not valid. Valid departments are: " + VALID_DEPARTMENTS);
            }

            List<Employee<T>> result = getAllEmployees()
                    .stream()
                    .filter(emp -> emp.getDepartment().equals(department))
                    .collect(Collectors.toList());

            logger.info("Found " + result.size() + " employees in department: " + department);
            return result;

        } catch (InvalidDepartmentException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error finding employees by department", e);
            throw new RuntimeException("Failed to find employees by department: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findByName(String name) {
        try {
            if(name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            List<Employee<T>> result = getAllEmployees()
                    .stream()
                    .filter(emp -> emp.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());

            logger.info("Found " + result.size() + " employees matching name: " + name);
            return result;

        } catch (IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error finding employees by name", e);
            throw new RuntimeException("Failed to find employees by name: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findByRating(double rating) {
        try {
            if(rating < 0 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }

            List<Employee<T>> result = getAllEmployees()
                    .stream()
                    .filter(emp -> emp.getPerformanceRating() >= rating)
                    .collect(Collectors.toList());

            logger.info("Found " + result.size() + " employees with rating >= " + rating);
            return result;

        } catch (IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error finding employees by rating", e);
            throw new RuntimeException("Failed to find employees by rating: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findBySalaryBetween(double min, double max) throws InvalidSalaryException {
        try {
            if(min < 0 || max < 0) {
                throw new InvalidSalaryException("Salary cannot be negative");
            }

            if(min > max) {
                throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
            }

            List<Employee<T>> result = getAllEmployees()
                    .stream()
                    .filter(emp -> emp.getSalary() >= min && emp.getSalary() <= max)
                    .collect(Collectors.toList());

            logger.info("Found " + result.size() + " employees with salary between " + min + " and " + max);
            return result;

        } catch (InvalidSalaryException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error finding employees by salary range", e);
            throw new RuntimeException("Failed to find employees by salary range: " + e.getMessage(), e);
        }
    }

    @Override
    public void displayAll() {
        try {
            Iterator<Employee<T>> employeeIterator = getAllEmployees().iterator();
            System.out.println("|----------|-------------------------|------------|----------|----------|------------|----------|");
            System.out.printf("|%-10s|%-25s|%-12s|%-10s|%-8s|%-12s|%-10s|\n",
                    "EmpId", "Name", "Department", "Performance", "Experience", "Salary", "Status");
            System.out.println("|----------|-------------------------|------------|----------|----------|------------|----------|");

            while(employeeIterator.hasNext()) {
                Employee<T> employee = employeeIterator.next();
                System.out.printf("|%-10s|%-25s|%-12s|%-10.1f|%-10d|$%-11.2f|%-10s|\n",
                        employee.getEmployeeId(),
                        employee.getName(),
                        employee.getDepartment(),
                        employee.getPerformanceRating(),
                        employee.getYearsOfExperience(),
                        employee.getSalary(),
                        employee.isActive() ? "Active" : "Inactive");
            }

            logger.info("Displayed all employees");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error displaying employees", e);
            System.out.println("Error displaying employees: " + e.getMessage());
        }
    }

    @Override
    public Map<String, List<Employee<T>>> groupByDepartment() {
        try {
            Map<String, List<Employee<T>>> result = getAllEmployees()
                    .stream()
                    .collect(Collectors.groupingBy(Employee::getDepartment));

            logger.info("Grouped employees by department into " + result.size() + " groups");
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error grouping employees by department", e);
            throw new RuntimeException("Failed to group employees by department: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findSortedBySalary() {
        try {
            List<Employee<T>> result = employees.values()
                    .stream()
                    .sorted(new EmployeeSalaryComparator<>())
                    .collect(Collectors.toList());

            logger.info("Sorted employees by salary");
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sorting employees by salary", e);
            throw new RuntimeException("Failed to sort employees by salary: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findSortedByPerformanceRating() {
        try {
            List<Employee<T>> result = employees.values()
                    .stream()
                    .sorted(new EmployeePerformanceComparator<>())
                    .collect(Collectors.toList());

            logger.info("Sorted employees by performance rating");
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sorting employees by performance rating", e);
            throw new RuntimeException("Failed to sort employees by performance rating: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findSortedByExperience() {
        try {
            List<Employee<T>> result = employees.values()
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());

            logger.info("Sorted employees by years of experience");
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sorting employees by experience", e);
            throw new RuntimeException("Failed to sort employees by experience: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> giveSalaryRaiseByPerformanceRating(double performanceRating, double percentageRaise)
            throws InvalidSalaryException {
        try {
            if(performanceRating < 0 || performanceRating > 5) {
                throw new IllegalArgumentException("Performance rating must be between 0 and 5");
            }

            if(percentageRaise < 0) {
                throw new InvalidSalaryException("Percentage raise cannot be negative");
            }

            List<Employee<T>> result = employees.values()
                    .stream()
                    .filter(emp -> emp.getPerformanceRating() >= performanceRating)
                    .map(emp -> {
                        try {
                            emp.setSalary(emp.getSalary() + emp.getSalary() * percentageRaise);
                            return emp;
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Error applying salary raise to employee: " + emp.getEmployeeId(), e);
                            return emp;
                        }
                    })
                    .collect(Collectors.toList());

            logger.info("Applied salary raise of " + (percentageRaise * 100) + "% to " + result.size() +
                    " employees with performance rating >= " + performanceRating);
            return result;

        } catch (InvalidSalaryException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error giving salary raise", e);
            throw new RuntimeException("Failed to give salary raise: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findTopHighestPaid() {
        try {
            List<Employee<T>> result = findSortedBySalary()
                    .stream()
                    .limit(5)
                    .collect(Collectors.toList());

            logger.info("Found top 5 highest paid employees");
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding top highest paid employees", e);
            throw new RuntimeException("Failed to find top highest paid employees: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee<T>> findTopHighestPaid(int numberOfEmployees) {
        try {
            if(numberOfEmployees <= 0) {
                throw new IllegalArgumentException("Number of employees must be positive");
            }

            List<Employee<T>> result = findSortedBySalary()
                    .stream()
                    .limit(numberOfEmployees)
                    .collect(Collectors.toList());

            logger.info("Found top " + numberOfEmployees + " highest paid employees");
            return result;

        } catch (IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error finding top highest paid employees", e);
            throw new RuntimeException("Failed to find top highest paid employees: " + e.getMessage(), e);
        }
    }

    @Override
    public double calculateAverageSalaryByDepartment(String department) throws InvalidDepartmentException {
        try {
            if(department == null || department.trim().isEmpty()) {
                throw new IllegalArgumentException("Department cannot be empty");
            }

            if(!VALID_DEPARTMENTS.contains(department)) {
                throw new InvalidDepartmentException("Department '" + department + "' is not valid. Valid departments are: " + VALID_DEPARTMENTS);
            }

            double result = getAllEmployees()
                    .stream()
                    .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0.0);

            logger.info("Calculated average salary for department " + department + ": " + result);
            return result;

        } catch (InvalidDepartmentException | IllegalArgumentException e) {
            // Rethrow these exceptions to be handled by UI
            throw e;
        } catch (Exception e) {
            // Log unexpected exceptions
            logger.log(Level.SEVERE, "Unexpected error calculating average salary by department", e);
            throw new RuntimeException("Failed to calculate average salary by department: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Double> calculateAverageSalaryPerDepartment() {
        try {
            Map<String, Double> result = getAllEmployees()
                    .stream()
                    .collect(Collectors.groupingBy(
                            Employee::getDepartment,
                            Collectors.averagingDouble(Employee::getSalary)));

            logger.info("Calculated average salary per department for " + result.size() + " departments");
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating average salary per department", e);
            throw new RuntimeException("Failed to calculate average salary per department: " + e.getMessage(), e);
        }
    }
}