package model;

/**
 * FullTimeEmployee extends Employee (INHERITANCE).
 *
 * Net Salary = Basic Salary + HRA + DA - Tax
 */
public class FullTimeEmployee extends Employee {

    private double basicSalary;
    private double hra;
    private double da;
    private double tax;

    public FullTimeEmployee(int id, String name, String department,
            double basicSalary, double hra, double da, double tax) {
        super(id, name, department);
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.tax = tax;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getHra() {
        return hra;
    }

    public void setHra(double hra) {
        this.hra = hra;
    }

    public double getDa() {
        return da;
    }

    public void setDa(double da) {
        this.da = da;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    // METHOD OVERRIDING: FullTimeEmployee's own version of calculateSalary().
    @Override
    public double calculateSalary() {
        return basicSalary + hra + da - tax;
    }

    @Override
    public String getEmployeeType() {
        return "Full-Time";
    }
}