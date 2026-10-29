package dao;

import model.Employee;
import model.FullTimeEmployee;
import model.PartTimeEmployee;
import util.DuplicateEmployeeException;
import util.EmployeeNotFoundException;

import java.sql.*;
import java.util.ArrayList;

/**
 * EmployeeDAO (Data Access Object) is the ONLY class that talks to the
 * database. It knows SQL; nothing else in the project does.
 *
 * Every other class (PayrollService, Main) still works with plain
 * Employee/FullTimeEmployee/PartTimeEmployee objects — this class is
 * responsible for turning those objects into database rows and back.
 *
 * Each method opens its own short-lived Connection using try-with-resources,
 * so the connection is automatically closed even if an error happens.
 * This is simpler to reason about than sharing one long-lived connection,
 * which is exactly what we want for a student project.
 */
public class EmployeeDAO {

    // The database is a single file, payroll.db, created in the folder
    // you run the program from. SQLite needs no server or install.
    private static final String DB_URL = "jdbc:sqlite:payroll.db";

    public EmployeeDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS employees (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "department TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "basic_salary REAL, " +
                "hra REAL, " +
                "da REAL, " +
                "tax REAL, " +
                "hours_worked REAL, " +
                "hourly_rate REAL)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    public void add(Employee employee) throws DuplicateEmployeeException {
        if (exists(employee.getId())) {
            throw new DuplicateEmployeeException("Employee ID " + employee.getId() + " already exists.");
        }

        String sql = "INSERT INTO employees " +
                "(id, name, department, type, basic_salary, hra, da, tax, hours_worked, hourly_rate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employee.getId());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getDepartment());
            ps.setString(4, employee.getEmployeeType());

            if (employee instanceof FullTimeEmployee) {
                FullTimeEmployee fte = (FullTimeEmployee) employee;
                ps.setDouble(5, fte.getBasicSalary());
                ps.setDouble(6, fte.getHra());
                ps.setDouble(7, fte.getDa());
                ps.setDouble(8, fte.getTax());
                ps.setNull(9, Types.REAL);
                ps.setNull(10, Types.REAL);
            } else {
                PartTimeEmployee pte = (PartTimeEmployee) employee;
                ps.setNull(5, Types.REAL);
                ps.setNull(6, Types.REAL);
                ps.setNull(7, Types.REAL);
                ps.setNull(8, Types.REAL);
                ps.setDouble(9, pte.getHoursWorked());
                ps.setDouble(10, pte.getHourlyRate());
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add employee: " + e.getMessage(), e);
        }
    }

    private boolean exists(int id) {
        String sql = "SELECT 1 FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public ArrayList<Employee> getAll() {
        ArrayList<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch employees: " + e.getMessage(), e);
        }
        return employees;
    }

    public Employee findById(int id) throws EmployeeNotFoundException {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
        throw new EmployeeNotFoundException("No employee found with ID " + id);
    }

    public void updateBasicDetails(int id, String name, String department) throws EmployeeNotFoundException {
        findById(id); // throws EmployeeNotFoundException if the ID doesn't exist

        String sql = "UPDATE employees SET name = ?, department = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, department);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee: " + e.getMessage(), e);
        }
    }

    public void updateFullTimeSalaryDetails(int id, double basicSalary, double hra, double da, double tax)
            throws EmployeeNotFoundException {
        Employee e = findById(id);
        if (!(e instanceof FullTimeEmployee)) {
            throw new EmployeeNotFoundException("Employee ID " + id + " is not a Full-Time employee.");
        }

        String sql = "UPDATE employees SET basic_salary = ?, hra = ?, da = ?, tax = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, basicSalary);
            ps.setDouble(2, hra);
            ps.setDouble(3, da);
            ps.setDouble(4, tax);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update salary details: " + ex.getMessage(), ex);
        }
    }

    public void updatePartTimeSalaryDetails(int id, double hoursWorked, double hourlyRate)
            throws EmployeeNotFoundException {
        Employee e = findById(id);
        if (!(e instanceof PartTimeEmployee)) {
            throw new EmployeeNotFoundException("Employee ID " + id + " is not a Part-Time employee.");
        }

        String sql = "UPDATE employees SET hours_worked = ?, hourly_rate = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, hoursWorked);
            ps.setDouble(2, hourlyRate);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update salary details: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id) throws EmployeeNotFoundException {
        findById(id); // throws EmployeeNotFoundException if the ID doesn't exist

        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    // Turns one row of the result set back into the right Employee subclass.
    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String department = rs.getString("department");
        String type = rs.getString("type");

        if ("Full-Time".equals(type)) {
            return new FullTimeEmployee(id, name, department,
                    rs.getDouble("basic_salary"), rs.getDouble("hra"),
                    rs.getDouble("da"), rs.getDouble("tax"));
        } else {
            return new PartTimeEmployee(id, name, department,
                    rs.getDouble("hours_worked"), rs.getDouble("hourly_rate"));
        }
    }
}
