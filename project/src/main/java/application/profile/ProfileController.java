package application.profile;

import application.SceneController;
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
    private Button btnUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the text fields with the current user's information

        Person person = getEmployeeInfo();

        lblName.setText("Employee: " + person.getFirstName() + " " + person.getLastName());
        lblID.setText("ID: #" + person.getEmployeeID());
        txtfName.setText(person.getFirstName());
        txtlName.setText(person.getLastName());
        txtEmail.setText(person.getEmail());
        txtPhone.setText(person.getPhone());
        txtNiNumber.setText(person.getNiNumber());


        // Check if the text fields are editable
        if (txtfName.isEditable()) {
            // If they are editable, change the button text to "Update"
            btnUpdate.setText("Update");
        } else {
            // If they are not editable, change the button text to "Cancel"
            btnUpdate.setText("Edit");
        }
    }

    public void btnToggleUpdate() {

        // Check if the text fields are editable
        if (Objects.equals(btnUpdate.getText(), "Update")) {
            // Update the user's information


            // Toggle the text fields to be editable or not
            txtfName.setEditable(!txtfName.isEditable());
            txtlName.setEditable(!txtlName.isEditable());
            txtEmail.setEditable(!txtEmail.isEditable());
            txtPhone.setEditable(!txtPhone.isEditable());
            txtNiNumber.setEditable(!txtNiNumber.isEditable());

            btnUpdate.setText("Edit");

        } else {
            // Toggle the text fields to be editable or not
            txtfName.setEditable(!txtfName.isEditable());
            txtlName.setEditable(!txtlName.isEditable());
            txtEmail.setEditable(!txtEmail.isEditable());
            txtPhone.setEditable(!txtPhone.isEditable());
            txtNiNumber.setEditable(!txtNiNumber.isEditable());

            btnUpdate.setText("Update");
        }



    }

    public void CloseApplication(ActionEvent event) throws IOException {
        // Close the application
        System.exit(0);
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

}
