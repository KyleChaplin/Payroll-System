package application.employees;

import application.SceneController;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.DatabaseController.*;

public class EmployeeController implements Initializable {
    private Stage stage;
    private Scene scene;

    @FXML
    private TableView<Person> EmployeeTable;

    @FXML
    private TableColumn<Person, String> ID;

    @FXML
    private TableColumn<Person, String> FirstName;

    @FXML
    private TableColumn<Person, String> LastName;

    @FXML
    private TableColumn<Person, String> Email;

    @FXML
    private TableColumn<Person, String> Phone;

    @FXML
    private TableColumn<Person, String> AccessLevel;

    @FXML
    private TableColumn<Person, String> NiNumber;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtNiNumber;

    @FXML
    private ComboBox cboAccessLevel;

    @FXML
    private Label txtEmptyError;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Populate the combo box
        cboAccessLevel.getItems().addAll(
                "0",
                "1",
                "2",
                "3",
                "4",
                "5"
        );

        // Add employees to the table
        ID.setCellValueFactory(new PropertyValueFactory<Person, String>("employeeID"));
        FirstName.setCellValueFactory(new PropertyValueFactory<Person, String>("FirstName"));
        LastName.setCellValueFactory(new PropertyValueFactory<Person, String>("LastName"));
        Email.setCellValueFactory(new PropertyValueFactory<Person, String>("Email"));
        Phone.setCellValueFactory(new PropertyValueFactory<Person, String>("Phone"));
        AccessLevel.setCellValueFactory(new PropertyValueFactory<Person, String>("AccessLevel"));
        NiNumber.setCellValueFactory(new PropertyValueFactory<Person, String>("NiNumber"));

        EmployeeTable.setItems(getAllEmployees());
    }

    @FXML
    private void btnAdd(ActionEvent event) throws IOException {
        // Check if the text fields are empty
        // Only add the employee if all the text fields are filled in
        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPhone.getText().isEmpty() || txtNiNumber.getText().isEmpty() || cboAccessLevel.getSelectionModel().isEmpty()) {
            // Show an error message
            txtEmptyError.setText("Fields should not be empty!");
        } else {
            // Add the employee
            addEmployee(txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(), txtPhone.getText(), txtNiNumber.getText(), Integer.parseInt((String)cboAccessLevel.getValue()), false);

            btnClear(event);
            btnRefresh(event);
        }
    }

    @FXML
    private void btnRefresh(ActionEvent event) throws IOException {
        // Refresh the table
        EmployeeTable.setItems(getAllEmployees());
    }

    @FXML
    private void btnUpdate(ActionEvent event) throws IOException {

    }

    @FXML
    private void btnDelete(ActionEvent event) throws IOException {
        // Select the employee from the tableview and delete them
        TableView.TableViewSelectionModel<Person> selectionModel = EmployeeTable.getSelectionModel();
        if (selectionModel.isEmpty()) {
            // Show an error message
            txtEmptyError.setText("Please select an employee to delete!");
        } else {
            // Delete the employee
            deleteEmployee(selectionModel.getSelectedItem().getEmployeeID());

            btnClear(event);
            btnRefresh(event);
        }


        deleteEmployee(txtID.getText());
    }

    @FXML
    private void btnClear(ActionEvent event) throws IOException {
        // Clear the text fields
        txtID.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        cboAccessLevel.getSelectionModel().clearSelection();
        cboAccessLevel.setPromptText("Access Level");
    }

    @FXML
    private void onTableClick() {
        // Check if the tableview is empty
        if (!EmployeeTable.getSelectionModel().isEmpty()) {
            // Get the selected employee from the tableview
            Person person = EmployeeTable.getSelectionModel().getSelectedItem();
            txtID.setText(person.getEmployeeID());
            txtFirstName.setText(person.getFirstName());
            txtLastName.setText(person.getLastName());
            txtEmail.setText(person.getEmail());
            txtPhone.setText(person.getPhone());
            cboAccessLevel.setValue(person.getAccessLevel());
            txtNiNumber.setText(person.getNiNumber());
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

    public void openTimeoff(ActionEvent event) throws IOException {
        SceneController.openScene(event, "timeoff", stage, scene);
    }

    public void openHelp(ActionEvent event) throws IOException {
        SceneController.openScene(event, "help", stage, scene);
    }
}
