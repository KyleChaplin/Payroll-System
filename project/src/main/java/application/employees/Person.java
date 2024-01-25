package application.employees;

public class Person {
    private String employeeID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String hourlySalary;
    private String accessLevel;
    private String niNumber;
    private String pension; // Percentage
    private String bankName;
    private String accountNumber;
    private String sortCode;
    private String jobTitle;
    private String department;
    private String location;
    private String contractType;
    private String address1;
    private String address2;
    private String postcode;
    private String emergencyContactFName;
    private String emergencyContactLName;
    private String emergencyContactMobile;
    private String emergencyContactRelationship;

    private String hoursWorked;
    private String overtimeHours;
    private String overtimeRate;
    private String grossPay;
    private String tax;
    private String netPay;

    public Person(String employeeID, String firstName, String lastName, String email, String phone, String hourlySalary, String accessLevel,
                  String niNumber, String location, String contractType, String department, String jobTitle) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.accessLevel = accessLevel;
        this.niNumber = niNumber;
        this.hourlySalary = hourlySalary;
        this.location = location;
        this.contractType = contractType;
        this.department = department;
        this.jobTitle = jobTitle;

        hoursWorked = "0";
        overtimeHours = "0";
        overtimeRate = "0";
        grossPay = "0";
        tax = "0";
        netPay = "0";

        pension = "0"; // Percentage
        bankName = "Bank Name";
        accountNumber = "Account Number";
        sortCode = "Sort Code";

        address1 = "Address Line 1";
        address2 = "Address Line 2";
        postcode = "Postcode";

        emergencyContactFName = "Emergency Contact First Name";
        emergencyContactLName = "Emergency Contact Last Name";
        emergencyContactMobile = "Emergency Contact Mobile";
        emergencyContactRelationship = "Emergency Contact Relationship";
    }

    // Getters
    public String getEmployeeID() {
        return employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public String getNiNumber() {
        return niNumber;
    }

    public String getHourlySalary() {
        return hourlySalary;
    }

    public String getPension() {
        return pension;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public String getLocation() {
        return location;
    }

    public String getContractType() {
        return contractType;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getEFirstName() {
        return emergencyContactFName;
    }

    public String getELastName() {
        return emergencyContactLName;
    }

    public String getEMobile() {
        return emergencyContactMobile;
    }

    public String getERelationship() {
        return emergencyContactRelationship;
    }

    // Setters
    public void setHoursWorked(String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void setOvertimeHours(String overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public void setOvertimePay(String overtimeRate) {
        this.overtimeRate = overtimeRate;
    }

    public void setGrossPay(String grossPay) {
        this.grossPay = grossPay;
    }

    public void setTaxes(String tax) {
        this.tax = tax;
    }

    public void setNetPay(String netPay) {
        this.netPay = netPay;
    }

    public void setPension(String pension) {
        this.pension = pension;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public void setAddressLine1(String address) {
        this.address1 = address;
    }

    public void setAddressLine2(String address) {
        this.address2 = address;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setEFirstName(String emergencyContactFName) {
        this.emergencyContactFName = emergencyContactFName;
    }

    public void setELastName(String emergencyContactLName) {
        this.emergencyContactLName = emergencyContactLName;
    }

    public void setEMobile(String emergencyContactMobile) {
        this.emergencyContactMobile = emergencyContactMobile;
    }

    public void setERelationship(String emergencyContactRelationship) {
        this.emergencyContactRelationship = emergencyContactRelationship;
    }
}
