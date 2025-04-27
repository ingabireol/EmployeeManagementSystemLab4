package com.olim.employeemanagementsystem.display;

import com.olim.employeemanagementsystem.model.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EmployeeDisplay {

    // Constants for formatting
    private static final String HEADER_FORMAT = "| %-10s | %-20s | %-15s | %12s | %10s | %8s | %8s |%n";
    private static final String DATA_FORMAT = "| %-10s | %-20s | %-15s | %12.2f | %10.1f | %8d | %8s |%n";
    private static final String LINE = "+------------+----------------------+-----------------+--------------+------------+----------+----------+%n";
    
    //display employees using printf and stream api
    public static <T> void displayEmployees(Collection<Employee<T>> employees) {
        System.out.println("\nEMPLOYEE LIST (using Stream API):");
        System.out.printf(LINE);
        System.out.printf(HEADER_FORMAT, "ID", "Name", "Department", "Salary ($)", "Rating", "Exp (Yrs)", "Active");
        System.out.printf(LINE);
        
        employees.stream().forEach(emp -> 
            System.out.printf(DATA_FORMAT,
                    emp.getEmployeeId(),
                    emp.getName(),
                    emp.getDepartment(),
                    emp.getSalary(),
                    emp.getPerformanceRating(),
                    emp.getYearsOfExperience(),
                    emp.isActive() ? "Yes" : "No")
        );
        
        System.out.printf(LINE);
        System.out.println("Total employees: " + employees.size());
    }

    //generate Department summary report by going
    public static <T> void generateDepartmentSummaryReport(Collection<Employee<T>> employees) {
        System.out.println("\nDEPARTMENT SUMMARY REPORT:");
        System.out.println("+----------------------+-----------+-----------------+-----------------+-----------------+");
        System.out.println("| Department           | Emp Count | Avg Salary ($)  | Avg Experience  | Avg Performance |");
        System.out.println("+----------------------+-----------+-----------------+-----------------+-----------------+");
        
        // Group employees by department and calculate stats
        Map<String, List<Employee<T>>> empByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        
        empByDept.forEach((dept, deptEmps) -> {
            double avgSalary = deptEmps.stream()
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0);
                    
            double avgExp = deptEmps.stream()
                    .mapToDouble(Employee::getYearsOfExperience)
                    .average()
                    .orElse(0);
                    
            double avgPerf = deptEmps.stream()
                    .mapToDouble(Employee::getPerformanceRating)
                    .average()
                    .orElse(0);
                    
            System.out.printf("| %-20s | %9d | %15.2f | %15.1f | %15.1f |%n",
                    dept, deptEmps.size(), avgSalary, avgExp, avgPerf);
        });
        
        System.out.println("+----------------------+-----------+-----------------+-----------------+-----------------+");
    }

    //Display performance rating report by categorizing performance
    public static <T> void generatePerformanceReport(Collection<Employee<T>> employees) {
        System.out.println("\nPERFORMANCE RATING REPORT:");
        System.out.println("+------------------+-----------+------------------+");
        System.out.println("| Performance      | Count     | Avg Salary ($)   |");
        System.out.println("+------------------+-----------+------------------+");

        // Outstanding (≥4.5)
        List<Employee<T>> outstandingEmps = employees.stream()
                .filter(emp -> emp.getPerformanceRating() >= 4.5)
                .collect(Collectors.toList());
        double outstandingAvgSalary = outstandingEmps.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        System.out.printf("| Outstanding      | %9d | %16.2f |%n",
                outstandingEmps.size(), outstandingAvgSalary);

        // Excellent (4.0-4.4)
        List<Employee<T>> excellentEmps = employees.stream()
                .filter(emp -> emp.getPerformanceRating() >= 4.0 && emp.getPerformanceRating() < 4.5)
                .collect(Collectors.toList());
        double excellentAvgSalary = excellentEmps.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        System.out.printf("| Excellent        | %9d | %16.2f |%n",
                excellentEmps.size(), excellentAvgSalary);

        // Good (3.5-3.9)
        List<Employee<T>> goodEmps = employees.stream()
                .filter(emp -> emp.getPerformanceRating() >= 3.5 && emp.getPerformanceRating() < 4.0)
                .collect(Collectors.toList());
        double goodAvgSalary = goodEmps.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        System.out.printf("| Good             | %9d | %16.2f |%n",
                goodEmps.size(), goodAvgSalary);

        // Average (3.0-3.4)
        List<Employee<T>> averageEmps = employees.stream()
                .filter(emp -> emp.getPerformanceRating() >= 3.0 && emp.getPerformanceRating() < 3.5)
                .collect(Collectors.toList());
        double averageAvgSalary = averageEmps.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        System.out.printf("| Average          | %9d | %16.2f |%n",
                averageEmps.size(), averageAvgSalary);

        // Below Average (<3)
        List<Employee<T>> belowAvgEmps = employees.stream()
                .filter(emp -> emp.getPerformanceRating() < 3.0)
                .collect(Collectors.toList());
        double belowAvgSalary = belowAvgEmps.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        System.out.printf("| Below Average    | %9d | %16.2f |%n",
                belowAvgEmps.size(), belowAvgSalary);

        System.out.println("+------------------+-----------+------------------+");

        // Add a legend to explain the rating ranges
        System.out.println("\nPerformance Rating Ranges:");
        System.out.println("- Outstanding:    ≥4.5");
        System.out.println("- Excellent:      4.0-4.4");
        System.out.println("- Good:           3.5-3.9");
        System.out.println("- Average:        3.0-3.4");
        System.out.println("- Below Average:  <3.0");
    }
    //Display format reports by having salary ranges
    public static <T> void generateSalaryDistributionReport(Collection<Employee<T>> employees) {
        System.out.println("\nSALARY DISTRIBUTION REPORT:");
        System.out.println("+---------------------------+-----------+");
        System.out.println("| Salary Range              | Count     |");
        System.out.println("+---------------------------+-----------+");

        // Below $50,000
        long belowFiftyK = employees.stream()
                .filter(emp -> emp.getSalary() < 50000)
                .count();
        System.out.printf("| %-25s | %9d |%n", "Below $50,000", belowFiftyK);

        // $50,000 - $70,000
        long fiftyToSeventyK = employees.stream()
                .filter(emp -> emp.getSalary() >= 50000 && emp.getSalary() < 70000)
                .count();
        System.out.printf("| %-25s | %9d |%n", "$50,000 - $70,000", fiftyToSeventyK);

        // $70,000 - $90,000
        long seventyToNinetyK = employees.stream()
                .filter(emp -> emp.getSalary() >= 70000 && emp.getSalary() < 90000)
                .count();
        System.out.printf("| %-25s | %9d |%n", "$70,000 - $90,000", seventyToNinetyK);

        // $90,000 - $110,000
        long ninetyToOneHundredTenK = employees.stream()
                .filter(emp -> emp.getSalary() >= 90000 && emp.getSalary() < 110000)
                .count();
        System.out.printf("| %-25s | %9d |%n", "$90,000 - $110,000", ninetyToOneHundredTenK);

        // $110,000 and above
        long aboveOneHundredTenK = employees.stream()
                .filter(emp -> emp.getSalary() >= 110000)
                .count();
        System.out.printf("| %-25s | %9d |%n", "$110,000 and above", aboveOneHundredTenK);

        System.out.println("+---------------------------+-----------+");
    }
}