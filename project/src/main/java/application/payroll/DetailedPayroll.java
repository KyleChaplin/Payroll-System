package application.payroll;

public class DetailedPayroll {
    private String employeeID;
    private String firstName;
    private String lastName;
    private Double hoursWorked;
    private Double basePay;
    private Double overtimeHours;
    private Double overtimePay;
    private Double taxPaid;

    public DetailedPayroll(String employeeID, String firstName, String lastName, Double hoursWorked, Double basePay, Double overtimeHours, Double overtimePay, Double taxPaid) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hoursWorked = hoursWorked;
        this.basePay = basePay;
        this.overtimeHours = overtimeHours;
        this.overtimePay = overtimePay;
        this.taxPaid = taxPaid;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public Double getBasePay() {
        return basePay;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public Double getOvertimePay() {
        return overtimePay;
    }

    public Double getTaxPaid() {
        return taxPaid;
    }
}
