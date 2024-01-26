package application.profile;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import application.employees.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static application.DatabaseController.getEmployeeInfo;

public class ProfileController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private Label lblID;

    @FXML
    private Label lblName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNiNumber;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtfName;

    @FXML
    private TextField txtlName;

    @FXML
    private TextField txtSalaryHourly;

    @FXML
    private TextField txtPension;

    @FXML
    private TextField txtBankName;

    @FXML
    private TextField txtAccountNumber;

    @FXML
    private TextField txtSortCode;

    @FXML
    private TextField txtJobTitle;

    @FXML
    private TextField txtDepartment;

    @FXML
    private TextField txtContractType;

    @FXML
    private TextField txtLocation;

    @FXML
    private TextField txtAddress1;

    @FXML
    private TextField txtAddress2;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtPostcode;

    @FXML
    private TextField txtEFirstName;

    @FXML
    private TextField txtELastName;

    @FXML
    private TextField txtERelationship;

    @FXML
    private TextField txtEMobile;

    @FXML
    private Button btnGreen; // This button will either be "Edit" or "Update"

    private String id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the text fields with the current user's information
        Person person = getEmployeeInfo();

        lblName.setText("Employee: " + person.getFirstName() + " " + person.getLastName());
        id = person.getEmployeeID();
        lblID.setText("ID: #" + id);

        // Populate the text fields with the current user's information
        txtfName.setText(person.getFirstName());
        txtlName.setText(person.getLastName());
        txtEmail.setText(person.getEmail());
        txtPhone.setText(person.getPhone());
        txtNiNumber.setText(person.getNiNumber());

        // Populate the text fields with the payroll information
        txtSalaryHourly.setText(String.valueOf(person.getHourlySalary()));
        txtPension.setText(String.valueOf(person.getPension()));
        txtBankName.setText(person.getBankName());
        txtAccountNumber.setText(person.getAccountNumber());
        txtSortCode.setText(person.getSortCode());

        // Populate the text fields with the job information
        txtJobTitle.setText(person.getJobTitle());
        txtDepartment.setText(person.getDepartment());
        txtContractType.setText(person.getContractType());
        txtLocation.setText(person.getLocation());

        // Populate the text fields with the address information
        txtAddress1.setText(person.getAddress1());
        txtAddress2.setText(person.getAddress2());
        txtPostcode.setText(person.getPostcode());

        // Populate the text fields with the emergency contact information
        txtEFirstName.setText(person.getEFirstName());
        txtELastName.setText(person.getELastName());
        txtEMobile.setText(person.getEMobile());
        txtERelationship.setText(person.getERelationship());

        // Check if the text fields are editable
        if (txtfName.isEditable()) {
            // If they are editable, change the button text to "Update"
            btnGreen.setText("Update");
        } else {
            // If they are not editable, change the button text to "Cancel"
            btnGreen.setText("Edit");
        }
    }

    public void btnToggleUpdate() {

        // Check if the text fields are editable
        if (Objects.equals(btnGreen.getText(), "Update")) {
            // Update the user's information

            // Toggle the text fields to be editable or not
            txtfName.setEditable(!txtfName.isEditable());
            txtlName.setEditable(!txtlName.isEditable());
            txtEmail.setEditable(!txtEmail.isEditable());
            txtPhone.setEditable(!txtPhone.isEditable());
            txtNiNumber.setEditable(!txtNiNumber.isEditable());
            txtAddress1.setEditable(!txtAddress1.isEditable());
            txtAddress2.setEditable(!txtAddress2.isEditable());
            txtCity.setEditable(!txtCity.isEditable());
            txtPostcode.setEditable(!txtPostcode.isEditable());
            txtEFirstName.setEditable(!txtEFirstName.isEditable());
            txtELastName.setEditable(!txtELastName.isEditable());
            txtEMobile.setEditable(!txtEMobile.isEditable());
            txtERelationship.setEditable(!txtERelationship.isEditable());
            txtPension.setEditable(!txtPension.isEditable());
            txtBankName.setEditable(!txtBankName.isEditable());
            txtAccountNumber.setEditable(!txtAccountNumber.isEditable());
            txtSortCode.setEditable(!txtSortCode.isEditable());

            btnGreen.setText("Edit");

        } else {
            // Toggle the text fields to be editable or not
            txtfName.setEditable(!txtfName.isEditable());
            txtlName.setEditable(!txtlName.isEditable());
            txtEmail.setEditable(!txtEmail.isEditable());
            txtPhone.setEditable(!txtPhone.isEditable());
            txtNiNumber.setEditable(!txtNiNumber.isEditable());
            txtAddress1.setEditable(!txtAddress1.isEditable());
            txtAddress2.setEditable(!txtAddress2.isEditable());
            txtCity.setEditable(!txtCity.isEditable());
            txtPostcode.setEditable(!txtPostcode.isEditable());
            txtEFirstName.setEditable(!txtEFirstName.isEditable());
            txtELastName.setEditable(!txtELastName.isEditable());
            txtEMobile.setEditable(!txtEMobile.isEditable());
            txtERelationship.setEditable(!txtERelationship.isEditable());
            txtPension.setEditable(!txtPension.isEditable());
            txtBankName.setEditable(!txtBankName.isEditable());
            txtAccountNumber.setEditable(!txtAccountNumber.isEditable());
            txtSortCode.setEditable(!txtSortCode.isEditable());

            btnGreen.setText("Update");
        }

        if (!txtEFirstName.isEditable()) {
            DatabaseController.updateEmployeeProfile(id, txtlName.getText(), txtlName.getText(),
                    txtEmail.getText(), txtPhone.getText(), txtNiNumber.getText(), txtAddress1.getText(),
                    txtAddress2.getText(), txtCity.getText(), txtPostcode.getText(), txtBankName.getText(),
                    txtAccountNumber.getText(), txtSortCode.getText(), txtEFirstName.getText(),
                    txtELastName.getText(), txtEMobile.getText(), txtERelationship.getText());
        }
    }

    public void CloseApplication(ActionEvent event) throws IOException {
        // Close the application
        System.exit(0);
    }

    public void minimiseApplication(ActionEvent event) throws IOException {
        // Minimise the application
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();


    }

    public void openDashboard(ActionEvent event) throws IOException {
        SceneController.openScene(event, "home", stage, scene);
    }

    public void openSchedule(ActionEvent event) throws IOException {
        SceneController.openScene(event, "schedule", stage, scene);
    }

    public void openPayroll(ActionEvent event) throws IOException {
        SceneController.openScene(event, "payroll", stage, scene);
    }

    public void openPeople(ActionEvent event) throws IOException {
        SceneController.openScene(event, "people", stage, scene);
    }

    public void openProfile(ActionEvent event) throws IOException {
        SceneController.openScene(event, "profile", stage, scene);
    }

    public void openTimeoff(ActionEvent event) throws IOException {
        SceneController.openScene(event, "timeoff", stage, scene);
    }

    public void openHelp(ActionEvent event) throws IOException {
        SceneController.openScene(event, "help", stage, scene);
    }

    public void toggleTheme(ActionEvent event) throws IOException {
        ThemeManager.toggleMode();
    }

}
