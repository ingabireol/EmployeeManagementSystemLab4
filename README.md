# Employee Management System

A Java application that enables companies to efficiently manage their employee records using the Java Collections Framework and Generics. This system provides comprehensive functionality for adding, managing, searching, filtering, and sorting employee data.

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Core Concepts](#core-concepts)
- [Usage Examples](#usage-examples)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Video description](#video-link)
- [Documentation](#documentation)

## Features

### Employee Management
- Add new employees to the system
- Remove employees from the database
- Update employee details dynamically
- Display all employees in a formatted table

### Search and Filtering
- Search employees by department
- Find employees by name (partial match)
- Filter employees by minimum performance rating
- Find employees within specific salary ranges
- Group employees by department

### Sorting Capabilities
- Sort by years of experience (natural ordering)
- Sort by salary (highest first)
- Sort by performance rating (best first)
- Sort by name or department
- Custom and multi-criteria sorting


## Core Concepts

### Generics
The system uses generics to provide type safety while allowing flexibility in the type of employee ID used:

```java
public class Employee<T> implements Comparable<Employee<T>> {
    private T employeeId;
    // ...
}
```

### Collections Framework
- **HashMap**: Used as the primary storage mechanism for employee data
- **List**: Used for search results and sorted collections
- **Map**: Used for grouping employees by attributes like department

### Interfaces and Implementation
- **Comparable**: Employee implements Comparable for natural ordering by years of experience
- **Comparator**: Custom comparators are provided for flexible sorting criteria
- **Iterator**: Used for traversing employee collections

### Stream API
The system leverages Java's Stream API for filtering, mapping, and collecting data:

```java
// Example: Finding employees with a minimum rating
return database.getAllEmployees().stream()
        .filter(employee -> employee.getPerformanceRating() >= minRating)
        .collect(Collectors.toList());
```


## Technologies Used

- Java SE 21
- Java Collections Framework
- Generics
- Stream API
- Lambda Expressions
- Method References

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 21 or higher
- Maven (for dependency management)

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/ingabireol/EmployeeManagementSystem.git
   ```

2. Navigate to the project directory:
   ```
   cd EmployeeManagementSystem
   ```

3. Build the project:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   java -jar target/EmployeeManagementSystem-1.0-SNAPSHOT.jar
   ```
## video-link
    https://screenrec.com/share/ghRLbouMAc

## Documentation

### Employee Class
The core model class representing an employee with generic ID type.

```java
public class Employee<T> implements Comparable<Employee<T>> {
    private T employeeId;
    private String name;
    private String department;
    private double salary;
    private double performanceRating;
    private int yearsOfExperience;
    private boolean isActive;
    
    // Constructor, getters, setters, and other methods...
    
    @Override
    public int compareTo(Employee<T> other) {
        // Sort by years of experience (descending)
        return Integer.compare(other.yearsOfExperience, this.yearsOfExperience);
    }
}
```

### EmployeeDatabase Class
Manages the storage and retrieval of employee records with CRUD operations.

```java
public class EmployeeDatabase<T> {
    private final Map<T, Employee<T>> employees = new HashMap<>();
    
    // CRUD methods:
    public boolean addEmployee(Employee<T> employee) { /* ... */ }
    public Employee<T> removeEmployee(T employeeId) { /* ... */ }
    public boolean updateEmployeeDetails(T employeeId, String field, Object newValue) { /* ... */ }
    public Collection<Employee<T>> getAllEmployees() { /* ... */ }
    public void displayAll() { /* ... */ }
}
```

For more detailed documentation, refer to the Javadoc comments in the source code.

---

