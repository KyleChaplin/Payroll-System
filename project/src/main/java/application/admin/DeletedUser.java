package application.admin;

public class DeletedUser {
    private String addedDate;
    private String deleteDate;
    private String deletedBy;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String niNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postcode;
    private String bankName;
    private String accountNumber;
    private String sortCode;

    public DeletedUser(String addedDate, String deleteDate, String deletedBy, String firstName, String lastName,
                       String email, String phone, String niNumber, String addressLine1, String addressLine2,
                       String city, String postcode, String bankName, String accountNumber, String sortCode) {
        this.addedDate = addedDate;
        this.deleteDate = deleteDate;
        this.deletedBy = deletedBy;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.niNumber = niNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postcode = postcode;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public String getDeleteDate() {
        return deleteDate;
    }

    public String getDeletedBy() {
        return deletedBy;
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

    public String getNiNumber() {
        return niNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
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
}
