package application.employees;

import application.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.DatabaseController.addEmployee;
import static application.DatabaseController.getAllEmployees;

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
    private ComboBox cboAccessLevel;


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

        EmployeeTable.setItems(getAllEmployees());
    }

    @FXML
    private void btnAdd(ActionEvent event) throws IOException {
        addEmployee(txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(), txtPhone.getText(), Integer.parseInt((String)cboAccessLevel.getValue()), false);

        // Refresh the table
        EmployeeTable.refresh();
    }

    @FXML
    private void btnUpdate(ActionEvent event) throws IOException {

    }

    @FXML
    private void btnDelete(ActionEvent event) throws IOException {

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
