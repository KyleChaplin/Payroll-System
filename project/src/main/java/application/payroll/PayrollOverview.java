package application.payroll;

public class PayrollOverview {
    private String payDay;
    private String month;
    private double total;
    private int noEmployees;
    private int year;

    public PayrollOverview(String month, int year, String payDay, double total, int noEmployees) {
        this.payDay = payDay;
        this.month = month;
        this.total = total;
        this.noEmployees = noEmployees;
        this.year = year;
    }

    public String getPayDay() {
        return payDay;
    }

    public String getMonth() {
        return month;
    }

    public double getTotal() {
        return total;
    }

    public int getNoEmployees() {
        return noEmployees;
    }

    public int getYear() {
        return year;
    }

}