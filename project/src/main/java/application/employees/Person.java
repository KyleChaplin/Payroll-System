package application.employees;

public class Person {
    private String employeeID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String accessLevel;
    private String niNumber;

    public Person(String employeeID, String firstName, String lastName, String email, String phone, String accessLevel, String niNumber) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.accessLevel = accessLevel;
        this.niNumber = niNumber;
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
}
