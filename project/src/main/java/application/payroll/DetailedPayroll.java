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
    private String payDay;
    private Double pension;
    private Double netPay;
    private Double salary;

    public DetailedPayroll() {

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

    public Double getsalary() { return salary; }

    public Double getNetPay() { return netPay; }

    // Setters
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void setBasePay(Double basePay) {
        this.basePay = basePay;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public void setOvertimePay(Double overtimePay) {
        this.overtimePay = overtimePay;
    }

    public void setTaxPaid(Double taxPaid) {
        this.taxPaid = taxPaid;
    }

    public void setPayDay(String payDay) {
        this.payDay = payDay;
    }

    public void setPension(double pension) {
        this.pension = pension;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public void setsalary(double salary) {
        this.salary = salary;
    }
}
