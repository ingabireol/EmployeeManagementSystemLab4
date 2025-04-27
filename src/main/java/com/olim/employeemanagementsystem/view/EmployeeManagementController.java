package com.olim.employeemanagementsystem.view;

import com.olim.employeemanagementsystem.comparator.EmployeePerformanceComparator;
import com.olim.employeemanagementsystem.comparator.EmployeeSalaryComparator;
import com.olim.employeemanagementsystem.db.EmployeeDatabase;
import com.olim.employeemanagementsystem.display.EmployeeDisplay;
import com.olim.employeemanagementsystem.model.Employee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeManagementController implements Initializable {

    // Table and data
    @FXML private TableView<Employee<Integer>> employeeTable;
    @FXML private TableColumn<Employee<Integer>, Integer> idColumn;
    @FXML private TableColumn<Employee<Integer>, String> nameColumn;
    @FXML private TableColumn<Employee<Integer>, String> departmentColumn;
    @FXML private TableColumn<Employee<Integer>, Double> salaryColumn;
    @FXML private TableColumn<Employee<Integer>, Double> ratingColumn;
    @FXML private TableColumn<Employee<Integer>, Integer> yearsColumn;
    @FXML private TableColumn<Employee<Integer>, Boolean> activeColumn;

    // Form fields
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField departmentField;
    @FXML private TextField salaryField;
    @FXML private TextField ratingField;
    @FXML private TextField yearsField;
    @FXML private CheckBox activeCheck;

    // Search and filter fields
    @FXML private TextField searchNameField;
    @FXML private ComboBox<String> filterDepartmentComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField minSalaryField;
    @FXML private TextField maxSalaryField;
    @FXML private TextField minRatingField;

    // Database and data
    private EmployeeDatabase<Integer> database;
    private ObservableList<Employee<Integer>> employeeData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize database and load sample data
        database = new EmployeeDatabase<>(new HashMap<>());
        loadSampleData();
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("performanceRating"));
        yearsColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Initialize data
        employeeData = FXCollections.observableArrayList();
        employeeTable.setItems(employeeData);

        // Initialize dropdown fields
        filterDepartmentComboBox.getItems().addAll("All", "IT", "HR", "Finance", "Marketing", "Sales");
        filterDepartmentComboBox.setValue("All");

        sortComboBox.getItems().addAll(
                "ID", "Name", "Department",
                "Salary (High to Low)", "Performance (High to Low)", "Experience (High to Low)"
        );
        sortComboBox.setValue("ID");
        // Add selection listener to table
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormFields(newSelection);
            }
        });

        // Initial refresh
        refreshEmployeeTable();
    }

    @FXML
    private void addEmployee() {
        try {
            Integer id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String department = departmentField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            double rating = Double.parseDouble(ratingField.getText().trim());
            int years = Integer.parseInt(yearsField.getText().trim());
            boolean active = activeCheck.isSelected();

            // Validate input
            if (name.isEmpty() || department.isEmpty() || rating < 0 || rating > 5) {
                showAlert("Invalid Input", "Please check your input values.");
                return;
            }

            // Create and add employee
            Employee<Integer> newEmployee = new Employee<>(id, name, department, salary, rating, years, active);

            Integer result = database.addEmployee(newEmployee);

            if (result == null) {
                showAlert("Duplicate ID", "An employee with this ID already exists.");
            } else {
                refreshEmployeeTable();
                clearForm();
                applyFilters(); // Apply any active filters to the updated list
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for ID, salary, rating, and years.");
        } catch (IllegalArgumentException e){
            showAlert("Invalid Input",e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void updateEmployee() {
        try {
            if (idField.getText().trim().isEmpty()) {
                showAlert("No Selection", "Please select an employee to update.");
                return;
            }

            Integer id = Integer.parseInt(idField.getText().trim());

            // Update employee fields
            database.updateEmployeeDetails(id, "name", nameField.getText().trim());
            database.updateEmployeeDetails(id, "department", departmentField.getText().trim());
            database.updateEmployeeDetails(id, "salary", Double.parseDouble(salaryField.getText().trim()));
            database.updateEmployeeDetails(id, "performanceRating", Double.parseDouble(ratingField.getText().trim()));
            database.updateEmployeeDetails(id, "yearsOfExperience", Integer.parseInt(yearsField.getText().trim()));
            database.updateEmployeeDetails(id, "isActive", activeCheck.isSelected());

            refreshEmployeeTable();
            clearForm();
            applyFilters(); // Apply any active filters to the updated list

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for ID, salary, rating, and years.");
        } catch (IllegalArgumentException e){
            showAlert("Invalid Input",e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void removeEmployee() {
        try {
            if (idField.getText().trim().isEmpty()) {
                showAlert("No Selection", "Please select an employee to remove.");
                return;
            }

            Integer id = Integer.parseInt(idField.getText().trim());
            Employee<Integer> removed = database.removeEmployee(id);

            if (removed == null) {
                showAlert("Not Found", "No employee found with ID: " + id);
            } else {
                refreshEmployeeTable();
                clearForm();
                applyFilters(); // Apply any active filters to the updated list
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid ID number.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        idField.clear();
        nameField.clear();
        departmentField.clear();
        salaryField.clear();
        ratingField.clear();
        yearsField.clear();
        activeCheck.setSelected(true);
        employeeTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void applyFilters() {
        // Get all employees first
        List<Employee<Integer>> filteredList = database.getAllEmployees().stream().collect(Collectors.toList());

        // Apply name search filter (if provided)
        String nameSearch = searchNameField.getText().trim().toLowerCase();
        if (!nameSearch.isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(emp -> emp.getName().toLowerCase().contains(nameSearch))
                    .collect(Collectors.toList());
        }

        // Apply department filter (if not "All")
        String deptFilter = filterDepartmentComboBox.getValue();
        if (!"All".equals(deptFilter)) {
            filteredList = filteredList.stream()
                    .filter(emp -> emp.getDepartment().equals(deptFilter))
                    .collect(Collectors.toList());
        }

        // Apply salary range filter (if provided)
        try {
            if (!minSalaryField.getText().trim().isEmpty()) {
                double minSalary = Double.parseDouble(minSalaryField.getText().trim());
                filteredList = filteredList.stream()
                        .filter(emp -> emp.getSalary() >= minSalary)
                        .collect(Collectors.toList());
            }

            if (!maxSalaryField.getText().trim().isEmpty()) {
                double maxSalary = Double.parseDouble(maxSalaryField.getText().trim());
                filteredList = filteredList.stream()
                        .filter(emp -> emp.getSalary() <= maxSalary)
                        .collect(Collectors.toList());
            }
        } catch (NumberFormatException e) {
            // Ignore invalid number formats in filters
        }

        // Apply minimum rating filter (if provided)
        try {
            if (!minRatingField.getText().trim().isEmpty()) {
                double minRating = Double.parseDouble(minRatingField.getText().trim());
                filteredList = filteredList.stream()
                        .filter(emp -> emp.getPerformanceRating() >= minRating)
                        .collect(Collectors.toList());
            }
        } catch (NumberFormatException e) {
            // Ignore invalid number formats in filters
        }

        // Apply sorting
        String sortOption = sortComboBox.getValue();
        switch (sortOption) {
            case "ID":
                filteredList.sort((e1, e2) -> e1.getEmployeeId().compareTo(e2.getEmployeeId()));
                break;
            case "Name":
                filteredList.sort((e1, e2) -> e1.getName().compareTo(e2.getName()));
                break;
            case "Department":
                filteredList.sort((e1, e2) -> e1.getDepartment().compareTo(e2.getDepartment()));
                break;
            case "Salary (High to Low)":
                filteredList.sort(new EmployeeSalaryComparator<>());
                break;
            case "Performance (High to Low)":
                filteredList.sort(new EmployeePerformanceComparator<>());
                break;
            case "Experience (High to Low)":
                filteredList.sort((e1, e2) -> e2.getYearsOfExperience() - e1.getYearsOfExperience());
                break;
        }

        // Update table with filtered and sorted data
        employeeData.clear();
        employeeData.addAll(filteredList);
    }

    @FXML
    private void resetFilters() {
        searchNameField.clear();
        filterDepartmentComboBox.setValue("All");
        minSalaryField.clear();
        maxSalaryField.clear();
        minRatingField.clear();
        sortComboBox.setValue("ID");
        refreshEmployeeTable();
    }

    @FXML
    private void showDepartmentReport() {
        displayReport("Department Summary Report", () -> {
            StringBuilder report = new StringBuilder();
            EmployeeDisplay.generateDepartmentSummaryReport(database.getAllEmployees());
            return report.toString();
        });
    }

    @FXML
    private void showSalaryReport() {
        displayReport("Salary Distribution Report", () -> {
            StringBuilder report = new StringBuilder();
            EmployeeDisplay.generateSalaryDistributionReport(database.getAllEmployees());
            return report.toString();
        });
    }

    @FXML
    private void showPerformanceReport() {
        displayReport("Performance Rating Report", () -> {
            StringBuilder report = new StringBuilder();
            EmployeeDisplay.generatePerformanceReport(database.getAllEmployees());
            return report.toString();
        });
    }

    private void displayReport(String title, ReportGenerator reportGenerator) {
//        EmployeeDisplay.generatePerformanceReport(database.getAllEmployees());
        showAlert("Report Generation", title+" Report has been printed to console");
    }

    // Helper methods
    private void refreshEmployeeTable() {
        employeeData.setAll(database.getAllEmployees());
    }

    private void populateFormFields(Employee<Integer> employee) {
        idField.setText(employee.getEmployeeId().toString());
        nameField.setText(employee.getName());
        departmentField.setText(employee.getDepartment());
        salaryField.setText(String.valueOf(employee.getSalary()));
        ratingField.setText(String.valueOf(employee.getPerformanceRating()));
        yearsField.setText(String.valueOf(employee.getYearsOfExperience()));
        activeCheck.setSelected(employee.isActive());
    }

    private void loadSampleData() {
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
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Functional interface for report generation
    private interface ReportGenerator {
        String generateReport();
    }
}