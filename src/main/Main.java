package main;

import model.Employee;
import model.FullTimeEmployee;
import model.PartTimeEmployee;
import service.PayrollService;
import util.DuplicateEmployeeException;
import util.EmployeeNotFoundException;
import util.InputValidator;

import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static PayrollService payrollService = new PayrollService();

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            printMenu();

            int choice = readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    addEmployee();
                    break;

                case 2:
                    viewAllEmployees();
                    break;

                case 3:
                    searchEmployee();
                    break;

                case 4:
                    updateEmployee();
                    break;

                case 5:
                    deleteEmployee();
                    break;

                case 6:
                    generatePayroll();
                    break;

                case 7:
                    running = false;
                    System.out.println("Exiting Employee Payroll System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {

        System.out.println("\nEMPLOYEE PAYROLL SYSTEM");
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Search Employee");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Generate Payroll");
        System.out.println("7. Exit");
    }

    private static void addEmployee() {

        System.out.println("\nSelect Employee Type:");
        System.out.println("1. Full-Time");
        System.out.println("2. Part-Time");

        int typeChoice = readInt("Enter choice: ");

        if (typeChoice != 1 && typeChoice != 2) {
            System.out.println("Invalid employee type. Please choose 1 or 2.");
            return;
        }

        int id = readInt("Enter Employee ID: ");

        if (!InputValidator.isValidId(id)) {
            System.out.println("Invalid ID. ID must be a positive number.");
            return;
        }

        String name = readNonEmptyString("Enter Name: ");
        String department = readNonEmptyString("Enter Department: ");

        try {

            if (typeChoice == 1) {

                double basicSalary = readNonNegativeDouble("Enter Basic Salary: ");

                double hra = readNonNegativeDouble("Enter HRA: ");

                double da = readNonNegativeDouble("Enter DA: ");

                double tax = readNonNegativeDouble("Enter Tax: ");

                FullTimeEmployee employee = new FullTimeEmployee(
                        id,
                        name,
                        department,
                        basicSalary,
                        hra,
                        da,
                        tax);

                payrollService.addEmployee(employee);

                System.out.println("Full-Time employee added successfully.");

            } else {

                double hoursWorked = readNonNegativeDouble("Enter Hours Worked: ");

                double hourlyRate = readNonNegativeDouble("Enter Hourly Rate: ");

                PartTimeEmployee employee = new PartTimeEmployee(
                        id,
                        name,
                        department,
                        hoursWorked,
                        hourlyRate);

                payrollService.addEmployee(employee);

                System.out.println("Part-Time employee added successfully.");
            }

        } catch (DuplicateEmployeeException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllEmployees() {

        if (payrollService.isEmpty()) {
            System.out.println("\nNo employees found.");
            return;
        }

        System.out.println("\nALL EMPLOYEES");

        System.out.printf(
                "%-6s %-15s %-15s %-12s %-15s%n",
                "ID",
                "Name",
                "Department",
                "Type",
                "Net Salary");

        for (Employee e : payrollService.getAllEmployees()) {

            System.out.printf(
                    "%-6d %-15s %-15s %-12s ₹%-14.2f%n",
                    e.getId(),
                    e.getName(),
                    e.getDepartment(),
                    e.getEmployeeType(),
                    e.calculateSalary());
        }
    }

    private static void searchEmployee() {

        int id = readInt("Enter Employee ID to search: ");

        try {

            Employee employee = payrollService.searchById(id);

            System.out.println("\nEMPLOYEE DETAILS");

            printEmployeeDetails(employee);

        } catch (EmployeeNotFoundException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateEmployee() {

        int id = readInt("Enter Employee ID to update: ");

        try {

            Employee existing = payrollService.searchById(id);

            System.out.println("\nCurrent Employee Type: "
                    + existing.getEmployeeType());

            String name = readNonEmptyString("Enter new Name: ");

            String department = readNonEmptyString("Enter new Department: ");

            payrollService.updateBasicDetails(
                    id,
                    name,
                    department);

            if (existing instanceof FullTimeEmployee) {

                double basicSalary = readNonNegativeDouble(
                        "Enter new Basic Salary: ");

                double hra = readNonNegativeDouble(
                        "Enter new HRA: ");

                double da = readNonNegativeDouble(
                        "Enter new DA: ");

                double tax = readNonNegativeDouble(
                        "Enter new Tax: ");

                payrollService.updateFullTimeSalaryDetails(
                        id,
                        basicSalary,
                        hra,
                        da,
                        tax);

            } else if (existing instanceof PartTimeEmployee) {

                double hoursWorked = readNonNegativeDouble(
                        "Enter new Hours Worked: ");

                double hourlyRate = readNonNegativeDouble(
                        "Enter new Hourly Rate: ");

                payrollService.updatePartTimeSalaryDetails(
                        id,
                        hoursWorked,
                        hourlyRate);
            }

            System.out.println("Employee updated successfully.");

        } catch (EmployeeNotFoundException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteEmployee() {

        int id = readInt("Enter Employee ID to delete: ");

        try {

            payrollService.deleteEmployee(id);

            System.out.println("Employee deleted successfully.");

        } catch (EmployeeNotFoundException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generatePayroll() {

        if (payrollService.isEmpty()) {

            System.out.println(
                    "\nNo employees found. Nothing to generate.");

            return;
        }

        System.out.println("\nGENERATE PAYROLL");
        System.out.println("1. Individual Employee Payroll");
        System.out.println("2. Total Payroll Summary");

        int choice = readInt("Enter choice: ");

        if (choice == 1) {

            int id = readInt("Enter Employee ID: ");

            try {

                Employee employee = payrollService.searchById(id);

                printPayrollSlip(employee);

            } catch (EmployeeNotFoundException e) {

                System.out.println(
                        "Error: " + e.getMessage());
            }

        } else if (choice == 2) {

            System.out.println("\nTOTAL PAYROLL SUMMARY");

            System.out.println(
                    "Total Employees : "
                            + payrollService.getEmployeeCount());

            System.out.printf(
                    "Total Payroll   : ₹%.2f%n",
                    payrollService.calculateTotalPayroll());

        } else {

            System.out.println("Invalid choice.");
        }
    }

    private static void printEmployeeDetails(Employee employee) {

        System.out.println("Employee ID : "
                + employee.getId());

        System.out.println("Name        : "
                + employee.getName());

        System.out.println("Department  : "
                + employee.getDepartment());

        System.out.println("Type        : "
                + employee.getEmployeeType());

        if (employee instanceof FullTimeEmployee) {

            FullTimeEmployee fullTimeEmployee = (FullTimeEmployee) employee;

            System.out.println("Basic Salary: ₹"
                    + fullTimeEmployee.getBasicSalary());

            System.out.println("HRA         : ₹"
                    + fullTimeEmployee.getHra());

            System.out.println("DA          : ₹"
                    + fullTimeEmployee.getDa());

            System.out.println("Tax         : ₹"
                    + fullTimeEmployee.getTax());

        } else if (employee instanceof PartTimeEmployee) {

            PartTimeEmployee partTimeEmployee = (PartTimeEmployee) employee;

            System.out.println("Hours Worked: "
                    + partTimeEmployee.getHoursWorked());

            System.out.println("Hourly Rate : ₹"
                    + partTimeEmployee.getHourlyRate());
        }

        System.out.printf(
                "Net Salary  : ₹%.2f%n",
                employee.calculateSalary());
    }

    private static void printPayrollSlip(Employee employee) {

        System.out.println("\nEMPLOYEE PAYROLL");

        printEmployeeDetails(employee);
    }

    private static int readInt(String prompt) {

        while (true) {

            try {

                System.out.print(prompt);

                return Integer.parseInt(
                        scanner.nextLine().trim());

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter a whole number.");
            }
        }
    }

    private static double readNonNegativeDouble(String prompt) {

        while (true) {

            try {

                System.out.print(prompt);

                double value = Double.parseDouble(
                        scanner.nextLine().trim());

                if (!InputValidator.isNonNegative(value)) {

                    System.out.println(
                            "Value cannot be negative. Try again.");

                    continue;
                }

                return value;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter a valid number.");
            }
        }
    }

    private static String readNonEmptyString(String prompt) {

        while (true) {

            System.out.print(prompt);

            String value = scanner.nextLine().trim();

            if (InputValidator.isValidName(value)) {

                return value;
            }

            System.out.println(
                    "This field cannot be empty. Try again.");
        }
    }
}