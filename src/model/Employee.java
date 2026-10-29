package model;

/**
 * Employee is the ABSTRACT base class for all employee types.
 *
 * Why abstract?
 * - We never want a plain "Employee" object on its own — every real
 * employee is either Full-Time or Part-Time.
 * - It lets us share common fields (id, name, department) while forcing
 * every subclass to provide its own calculateSalary() and
 * getEmployeeType() logic.
 */
public abstract class Employee {

    // ENCAPSULATION: private fields, only reachable through getters/setters.
    private int id;
    private String name;
    private String department;

    public Employee(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // ABSTRACTION: every employee can calculate its own salary, but HOW is
    // left to the subclass. This is what makes calculateSalary() behave
    // differently (POLYMORPHISM) depending on whether the object is a
    // FullTimeEmployee or a PartTimeEmployee, even when called through a
    // plain "Employee" reference.
    public abstract double calculateSalary();

    // Used by PayrollService when printing payroll (Module 6) so we can
    // show "Full-Time" / "Part-Time" without using instanceof checks
    // scattered around the code.
    public abstract String getEmployeeType();
}