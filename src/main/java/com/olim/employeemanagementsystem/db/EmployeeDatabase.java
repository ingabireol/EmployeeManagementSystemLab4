package com.olim.employeemanagementsystem.db;

import com.olim.employeemanagementsystem.comparator.EmployeePerformanceComparator;
import com.olim.employeemanagementsystem.comparator.EmployeeSalaryComparator;
import com.olim.employeemanagementsystem.exception.EmployeeExistsException;
import com.olim.employeemanagementsystem.exception.EmptyAttributeException;
import com.olim.employeemanagementsystem.model.Employee;
import com.olim.employeemanagementsystem.service.SalaryManagementService;
import com.olim.employeemanagementsystem.service.SearchService;
import com.olim.employeemanagementsystem.service.SortService;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase<T> implements SearchService<T>, SortService<T>, SalaryManagementService<T> {
    private final  HashMap<T, Employee<T>> employees;
    public EmployeeDatabase(HashMap<T, Employee<T>> employees) {
        this.employees = employees;
    }
    //adds an employee to the database
    public T addEmployee(Employee<T> employee) throws EmptyAttributeException, EmployeeExistsException {
        if(employee == null){
            throw new IllegalArgumentException("Employee can not be empty");
        }
        if(employee.getName().isEmpty()){
            throw new EmptyAttributeException("Name can not be empty");
        }

        if(employee.getName().isEmpty()){
            throw new EmptyAttributeException("Name can not be empty");
        }
        if(employees.containsKey(employee.getEmployeeId()))
            throw new EmployeeExistsException();
        if(employee.getEmployeeId() instanceof Integer id){
            if(id < 0){
                throw new IllegalArgumentException("Invalid Id");
            }
        }
        if(employee.getSalary() < 10 || employee.getPerformanceRating() <= 1 || employee.getYearsOfExperience() < 0 )
            throw  new IllegalArgumentException("Invalid salary, rating, years of Experience values");
        employees.put(employee.getEmployeeId(),employee);
        return employee.getEmployeeId();
    }
    public Employee<T> removeEmployee(T employeeId){
        if(employeeId == null){
            throw new IllegalArgumentException("employee Id no not be null");
        }
        return employees.remove(employeeId);
    }
    public Employee<T> updateEmployeeDetails(T employeeId, String field, Object newValue){
        if(employeeId == null) {
            throw new IllegalArgumentException("Employee Id can not be null");
        }
        Employee<T> employee = employees.get(employeeId);
        switch(field.toLowerCase()){
            case "name":
                if (newValue instanceof  String){
                    employee.setName((String)newValue);
                }
                else{
                    throw new IllegalArgumentException("Name must be string");
                }
                break;
            case "department":
                if (newValue instanceof  String){
                    employee.setDepartment((String)newValue);
                }
                else{
                    throw new IllegalArgumentException("Department must be string");
                }
                break;
            case "salary":
                if (newValue instanceof Number){
                    employee.setSalary(((Number)newValue).doubleValue());
                    if(employee.getSalary() < 10){
                        throw new IllegalArgumentException("Salary must be greater than 10");
                    }
                }
                else{
                    throw new IllegalArgumentException("Salary must be of double");
                }
                break;
            case "performancerating":
                if (newValue instanceof Number){
                    employee.setPerformanceRating(((Number)newValue).doubleValue());
                    if(employee.getPerformanceRating() < 1 && employee.getYearsOfExperience() > 5){
                        throw new IllegalArgumentException("Performance rating is between 1 and 5");
                    }
                }
                else{
                    throw new IllegalArgumentException("Performance rating must be of double");
                }
                break;
            case "yearsofexperience":
                if (newValue instanceof Number){
                    employee.setYearsOfExperience(((Number)newValue).intValue());
                    if(employee.getYearsOfExperience() < 0){
                        throw new IllegalArgumentException("Experience must be greater than 0");
                    }
                }
                else{
                    throw new IllegalArgumentException("Years of experience rating must be of double");
                }
                break;
            case "isactive":
                if (newValue instanceof Boolean){
                    employee.setActive((Boolean) newValue);
                }
                else{
                    throw new IllegalArgumentException("isActive must be a boolean value");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid argument");
        }
        return employee;
    }
    public Collection<Employee<T>> getAllEmployees() {
        return employees.values();
    }

    @Override
    public List<Employee<T>> findByDepartment(String department) {
        return getAllEmployees()
                .stream()
                .filter(emp -> emp.getDepartment().equals(department))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee<T>> findByName(String name) {
        return getAllEmployees()
                .stream()
                .filter(emp -> emp.getName().equals(name))
                .collect(Collectors.toList());
    }
    @Override
    public List<Employee<T>> findByRating(double rating) {
        return getAllEmployees()
                .stream()
                .filter(emp -> emp.getPerformanceRating() >= rating)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee<T>> findBySalaryBetween(double min, double max) {
        return getAllEmployees()
                .stream()
                .filter(emp -> (emp.getSalary() > max && emp.getSalary() < min))
                .collect(Collectors.toList());
    }
    @Override
    public void displayAll() {
        Iterator<Employee<T>> employeeIterator = getAllEmployees().iterator();
        System.out.println("|----------|-------------------------|------------|----------|----------|------------|----------|");
        System.out.printf("|%-10s|%-25s|%-12s|%-10s|%-8s|%-12s|%-10s|\n",
                "EmpId", "Name", "Department", "Performance", "Experience", "Salary", "Status");
        System.out.println("|----------|-------------------------|------------|----------|----------|------------|----------|");
        while(employeeIterator.hasNext()){
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
    }
    @Override
    public Map<String, List<Employee<T>>> groupByDepartment() {
        return getAllEmployees()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    @Override
    public List<Employee<T>> findSortedBySalary() {
        return employees.values()
                .stream()
                .sorted(new EmployeeSalaryComparator<>())
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee<T>> findSortedByPerformanceRating() {
        return employees.values()
                .stream()
                .sorted(new EmployeePerformanceComparator<>())
                .collect(Collectors.toList());
    }
    @Override
    public List<Employee<T>> findSortedByExperience() {
        return employees.values()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee<T>> giveSalaryRaiseByPerformanceRating(double performanceRating,double percentageRaise) {
        return employees.values()
                .stream()
                .filter(emp -> emp.getPerformanceRating() >= performanceRating)
                .map(emp -> {
                    emp.setSalary(emp.getSalary() + emp.getSalary() * percentageRaise);
                    return emp;
                }).collect(Collectors.toList());

    }

    @Override
    public List<Employee<T>> findTopHighestPaid() {
        return findSortedBySalary()
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee<T>> findTopHighestPaid(int numberOfEmployees) {
        return findSortedBySalary()
                .stream()
                .limit(numberOfEmployees)
                .collect(Collectors.toList());
    }

    @Override
    public double calculateAverageSalaryByDepartment(String department) {
         return getAllEmployees()
                .stream()
                 .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                 .mapToDouble(Employee::getSalary)
                 .average()
                 .orElse(0.0);
    }
    @Override
    public Map<String, Double> calculateAverageSalaryPerDepartment(){
        return getAllEmployees()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,Collectors.averagingDouble(Employee::getSalary)));
    }

}