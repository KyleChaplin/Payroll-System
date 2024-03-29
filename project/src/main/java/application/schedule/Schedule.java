package application.schedule;

import javafx.collections.ObservableList;

public class Schedule {
    private String name;
    private String employeeID;
    private String weekId;
    private String mondayStart;
    private String mondayEnd;
    private String tuesdayStart;
    private String tuesdayEnd;
    private String wednesdayStart;
    private String wednesdayEnd;
    private String thursdayStart;
    private String thursdayEnd;
    private String fridayStart;
    private String fridayEnd;
    private String saturdayStart;
    private String saturdayEnd;
    private String sundayStart;
    private String sundayEnd;
    private double totalHoursWorked;
    private double totalPensionPaid;
    private double totalOvertimeHours;
    private double totalOvertimePay;
    private double totalGrossPay;
    private double totalTaxesPaid;
    private double totalNetEarned;

    public Schedule(String name, String employeeID) {
        this.name = name;
        this.employeeID = employeeID;
    }

    // Setters
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setWeekID(String weekId) {
        this.weekId = weekId;
    }

    public void setMonday(String start, String end) {
        this.mondayStart = start;
        this.mondayEnd = end;
    }

    public void setTuesday(String start, String end) {
        this.tuesdayStart = start;
        this.tuesdayEnd = end;
    }

    public void setWednesday(String start, String end) {
        this.wednesdayStart = start;
        this.wednesdayEnd = end;
    }

    public void setThursday(String start, String end) {
        this.thursdayStart = start;
        this.thursdayEnd = end;
    }

    public void setFriday(String start, String end) {
        this.fridayStart = start;
        this.fridayEnd = end;
    }

    public void setSaturday(String start, String end) {
        this.saturdayStart = start;
        this.saturdayEnd = end;
    }

    public void setSunday(String start, String end) {
        this.sundayStart = start;
        this.sundayEnd = end;
    }

    // Getters
    public String[] getDays() {
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }

    public String getName() {
        return name;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getWeekId() {
        return weekId;
    }

    public String getStartTime(String day) {
        switch (day) {
            case "Monday":
                return mondayStart;
            case "Tuesday":
                return tuesdayStart;
            case "Wednesday":
                return wednesdayStart;
            case "Thursday":
                return thursdayStart;
            case "Friday":
                return fridayStart;
            case "Saturday":
                return saturdayStart;
            case "Sunday":
                return sundayStart;
            default:
                return "";
        }
    }

    public String getEndTime(String day) {
        switch (day) {
            case "Monday":
                return mondayEnd;
            case "Tuesday":
                return tuesdayEnd;
            case "Wednesday":
                return wednesdayEnd;
            case "Thursday":
                return thursdayEnd;
            case "Friday":
                return fridayEnd;
            case "Saturday":
                return saturdayEnd;
            case "Sunday":
                return sundayEnd;
            default:
                return "";
        }
    }

    public String getMondayStart() {
        return mondayStart;
    }

    public String getMondayEnd() {
        return mondayEnd;
    }

    public String getTuesdayStart() {
        return tuesdayStart;
    }

    public String getTuesdayEnd() {
        return tuesdayEnd;
    }

    public String getWednesdayStart() {
        return wednesdayStart;
    }

    public String getWednesdayEnd() {
        return wednesdayEnd;
    }

    public String getThursdayStart() {
        return thursdayStart;
    }

    public String getThursdayEnd() {
        return thursdayEnd;
    }

    public String getFridayStart() {
        return fridayStart;
    }

    public String getFridayEnd() {
        return fridayEnd;
    }

    public String getSaturdayStart() {
        return saturdayStart;
    }

    public String getSaturdayEnd() {
        return saturdayEnd;
    }

    public String getSundayStart() {
        return sundayStart;
    }

    public String getSundayEnd() {
        return sundayEnd;
    }


    // Setters and getters for total

    public double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(double totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public double getTotalPensionPaid() {
        return totalPensionPaid;
    }

    public void setTotalPensionPaid(double totalPensionPaid) {
        this.totalPensionPaid = totalPensionPaid;
    }

    public double getTotalOvertimeHours() {
        return totalOvertimeHours;
    }

    public void setTotalOvertimeHours(double totalOvertimeHours) {
        this.totalOvertimeHours = totalOvertimeHours;
    }

    public double getTotalOvertimePay() {
        return totalOvertimePay;
    }

    public void setTotalOvertimePay(double totalOvertimePay) {
        this.totalOvertimePay = totalOvertimePay;
    }

    public double getTotalGrossPay() {
        return totalGrossPay;
    }

    public void setTotalGrossPay(double totalGrossPay) {
        this.totalGrossPay = totalGrossPay;
    }

    public double getTotalTaxesPaid() {
        return totalTaxesPaid;
    }

    public void setTotalTaxesPaid(double totalTaxesPaid) {
        this.totalTaxesPaid = totalTaxesPaid;
    }

    public double getTotalNetEarned() {
        return totalNetEarned;
    }

    public void setTotalNetEarned(double totalNetEarned) {
        this.totalNetEarned = totalNetEarned;
    }
}
