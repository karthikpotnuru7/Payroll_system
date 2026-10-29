package service;

import dao.EmployeeDAO;
import model.Employee;
import util.DuplicateEmployeeException;
import util.EmployeeNotFoundException;

import java.util.ArrayList;

/**
 * PayrollService is still the single place Main.java talks to for all
 * business logic. The difference from before: it no longer keeps an
 * ArrayList of its own — it delegates storage to EmployeeDAO, which
 * reads and writes an SQLite database. Every method here keeps the exact
 * same name and signature as before, so Main.java did not need to change
 * at all when we added the database.
 */
public class PayrollService {

    private EmployeeDAO employeeDAO;

    public PayrollService() {
        employeeDAO = new EmployeeDAO();
    }

    // Add

    public void addEmployee(Employee employee) throws DuplicateEmployeeException {
        employeeDAO.add(employee);
    }

    // View

    public ArrayList<Employee> getAllEmployees() {
        return employeeDAO.getAll();
    }

    public boolean isEmpty() {
        return employeeDAO.getAll().isEmpty();
    }

    // Search

    public Employee searchById(int id) throws EmployeeNotFoundException {
        return employeeDAO.findById(id);
    }

    // Update

    public void updateBasicDetails(int id, String name, String department) throws EmployeeNotFoundException {
        employeeDAO.updateBasicDetails(id, name, department);
    }

    public void updateFullTimeSalaryDetails(int id, double basicSalary, double hra, double da, double tax)
            throws EmployeeNotFoundException {
        employeeDAO.updateFullTimeSalaryDetails(id, basicSalary, hra, da, tax);
    }

    public void updatePartTimeSalaryDetails(int id, double hoursWorked, double hourlyRate)
            throws EmployeeNotFoundException {
        employeeDAO.updatePartTimeSalaryDetails(id, hoursWorked, hourlyRate);
    }

    // Delete

    public void deleteEmployee(int id) throws EmployeeNotFoundException {
        employeeDAO.delete(id);
    }

    // Payroll

    public double calculateTotalPayroll() {
        double total = 0;
        for (Employee e : employeeDAO.getAll()) {
            total += e.calculateSalary();
        }
        return total;
    }

    public int getEmployeeCount() {
        return employeeDAO.getAll().size();
    }
}