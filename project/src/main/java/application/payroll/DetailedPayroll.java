package application.payroll;

public class DetailedPayroll {
    private String employeeID;
    private String firstName;
    private String lastName;
    private double hoursWorked;
    private double basePay;
    private double overtimeHours;
    private double overtimePay;
    private double taxPaid;
    private String payDay;
    private String pension;
    private double pensionPaid;
    private double netPay;
    private double salary;

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

    public double getHoursWorked() {
        return hoursWorked;
    }

    public double getBasePay() {
        return basePay;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public double getOvertimePay() {
        return overtimePay;
    }

    public double getTaxPaid() {
        return taxPaid;
    }

    public double getsalary() { return salary; }

    public double getNetPay() { return netPay; }

    public String getPension() {
        return pension;
    }

    public double getPensionPaid() {
        return pensionPaid;
    }

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

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void setBasePay(double basePay) {
        this.basePay = basePay;
    }

    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public void setOvertimePay(double overtimePay) {
        this.overtimePay = overtimePay;
    }

    public void setTaxPaid(double taxPaid) {
        this.taxPaid = taxPaid;
    }

    public void setPayDay(String payDay) {
        this.payDay = payDay;
    }

    public void setPension(String pension) {
        this.pension = pension;
    }

    public void setPensionPaid(double pensionPaid) {
        this.pensionPaid = pensionPaid;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
