package application.payroll;

public class PayrollOverview {
    private String payDay;
    private String month;
    private String total;
    private String noEmployees;

    public PayrollOverview(String payDay, String month, String total, String noEmployees) {
        this.payDay = payDay;
        this.month = month;
        this.total = total;
        this.noEmployees = noEmployees;
    }

    public String getPayDay() {
        return payDay;
    }

    public String getMonth() {
        return month;
    }

    public String getTotal() {
        return total;
    }

    public String getNoEmployees() {
        return noEmployees;
    }

}