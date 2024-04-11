package application.employees;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

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
    private TableColumn<Person, String> HourlySalary;
    @FXML
    private TableColumn<Person, String> AccessLevel;
    @FXML
    private TableColumn<Person, String> NiNumber;
    @FXML
    private TableColumn<Person, String> Location;
    @FXML
    private TableColumn<Person, String> ContractType;
    @FXML
    private TableColumn<Person, String> ContractedHours;
    @FXML
    private TableColumn<Person, String> Department;
    @FXML
    private TableColumn<Person, String> JobTitle;
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
    private TextField txtHourlySalary;
    @FXML
    private TextField txtLocation;
    @FXML
    private TextField txtContractType;
    @FXML
    private TextField txtContractedHours;
    @FXML
    private TextField txtDepartment;
    @FXML
    private TextField txtJobTitle;
    @FXML
    private ComboBox cboAccessLevel;
    @FXML
    private Label txtEmptyError;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAdmin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Integer.parseInt(DatabaseController.GetTableData.getAccessLevel(
                DatabaseController.GetTableData.getEmailById(
                        DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()))) == 0) {
            btnAdmin.setVisible(true);
        }

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
        HourlySalary.setCellValueFactory(new PropertyValueFactory<Person, String>("HourlySalary"));
        AccessLevel.setCellValueFactory(new PropertyValueFactory<Person, String>("AccessLevel"));
        NiNumber.setCellValueFactory(new PropertyValueFactory<Person, String>("NiNumber"));
        Location.setCellValueFactory(new PropertyValueFactory<Person, String>("Location"));
        ContractType.setCellValueFactory(new PropertyValueFactory<Person, String>("ContractType"));
        ContractedHours.setCellValueFactory(new PropertyValueFactory<Person, String>("ContractedHours"));
        Department.setCellValueFactory(new PropertyValueFactory<Person, String>("Department"));
        JobTitle.setCellValueFactory(new PropertyValueFactory<Person, String>("JobTitle"));

        EmployeeTable.setItems(DatabaseController.GetTableData.getAllEmployees());
    }

    @FXML
    private void generateData() {
        String[] employeeData = EmployeeGenerator.generateEmployeeData();

        if (employeeData != null) {
            txtFirstName.setText(employeeData[0]);
            txtLastName.setText(employeeData[1]);
            txtEmail.setText(employeeData[2]);
            txtPhone.setText(employeeData[3]);
            txtHourlySalary.setText(employeeData[4]);
            txtNiNumber.setText(employeeData[5]);
            txtLocation.setText(employeeData[6]);
            txtContractType.setText(employeeData[7]);
            txtContractedHours.setText(employeeData[8]);
            txtDepartment.setText(employeeData[9]);
            txtJobTitle.setText(employeeData[10]);
            cboAccessLevel.setValue(employeeData[11]);
        }
    }

    private void setupErrorTimer() {
        Duration duration = Duration.seconds(5);
        KeyFrame keyFrame = new KeyFrame(duration, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Clear the error message after the specified duration
                txtEmptyError.setText("");
            }
        });

        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    @FXML
    private void btnRefresh() {
        // Refresh the table
        EmployeeTable.setItems(DatabaseController.GetTableData.getAllEmployees());
    }

    public void btnAdd() {
        try {
            // Check if the employee already exists
            if (DatabaseController.CheckTableData.employeeExists(txtEmail.getText())) {
                // Show an error message
                txtEmptyError.setText("Employee already exists!");

                setupErrorTimer();
            } else {

                // Check if the text fields are empty
                // Only add the employee if all the text fields are filled in
                if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtEmail.getText().isEmpty() ||
                        txtPhone.getText().isEmpty() || txtNiNumber.getText().isEmpty() ||
                        cboAccessLevel.getSelectionModel().isEmpty() || txtHourlySalary.getText().isEmpty() ||
                        txtLocation.getText().isEmpty() || txtContractType.getText().isEmpty() ||
                        txtContractedHours.getText().isEmpty() || txtDepartment.getText().isEmpty() ||
                        txtJobTitle.getText().isEmpty()) {
                    // Show an error message
                    txtEmptyError.setText("Fields should not be empty!");
                } else {
                    // Perform additional input validation
                    String email = txtEmail.getText();
                    String phone = txtPhone.getText();
                    String niNumber = txtNiNumber.getText();
                    String hourlySalary = txtHourlySalary.getText();

                    if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                        txtEmptyError.setText("Invalid email address!");
                        return;
                    }

                    if (!phone.matches("^[0-9]{11}$")) {
                        txtEmptyError.setText("Invalid phone number!");
                        return;
                    }

//                    if (!niNumber.matches("^[A-CEGHJ-PR-TW-Z]{1}[A-CEGHJ-NPR-TW-Z]{1}[0-9]{6}[A-D]{1}$")) {
//                        txtEmptyError.setText("Invalid NI number!");
//                        return;
//                    }

                    if (!niNumber.matches("^[ABCDEFGHKLMNPRSTWXYZ][ABCDEFGHKLMNPRSTUVWXYZ] [0-9]{2} [0-9]{2} [0-9]{2} [ABCD]$")) {
                        txtEmptyError.setText("Invalid NI number!");
                        return;
                    }

                    // Ensure hour salary is not negative and has at most 2 decimal places
                    try {
                        double salary = Double.parseDouble(hourlySalary);
                        if (salary < 0) {
                            txtEmptyError.setText("Hourly salary cannot be negative!");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        txtEmptyError.setText("Invalid hourly salary!");
                        return;
                    }

                    if (!hourlySalary.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                        txtEmptyError.setText("Invalid hourly salary!");
                        return;
                    }
                }

                // Once input validation is passed, add the employee
                DatabaseController.AddTableData.addEmployee(txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(), txtPhone.getText(),
                        txtHourlySalary.getText(), txtNiNumber.getText(), Integer.parseInt((String)cboAccessLevel.getValue()),
                        txtLocation.getText(), txtContractType.getText(), txtContractedHours.getText(),
                        txtDepartment.getText(), txtJobTitle.getText(),false);

                // Clear the text fields and refresh the table
                btnClear();
                btnRefresh();
                // Clear the error message
                txtEmptyError.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnUpdate() {
        // Check if the text fields are empty
        // Only add the employee if all the text fields are filled in
        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtEmail.getText().isEmpty() ||
                txtPhone.getText().isEmpty() || txtNiNumber.getText().isEmpty() ||
                cboAccessLevel.getSelectionModel().isEmpty() || txtHourlySalary.getText().isEmpty() ||
                txtLocation.getText().isEmpty() || txtContractType.getText().isEmpty() ||
                txtContractedHours.getText().isEmpty() || txtDepartment.getText().isEmpty()
                || txtJobTitle.getText().isEmpty()) {
            // Show an error message
            txtEmptyError.setText("Fields should not be empty!");
        } else {
            // Perform additional input validation
            String email = txtEmail.getText();
            String phone = txtPhone.getText();
            String niNumber = txtNiNumber.getText();
            String hourlySalary = txtHourlySalary.getText();
            double contractedHours = Double.parseDouble(txtContractedHours.getText());

            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                txtEmptyError.setText("Invalid email address!");
                return;
            }

            if (!phone.matches("^[0-9]{11}$")) {
                txtEmptyError.setText("Invalid phone number!");
                return;
            }

            if (!niNumber.matches("^[A-CEGHJ-PR-TW-Z]{1}[A-CEGHJ-NPR-TW-Z]{1}[0-9]{6}[A-D]{1}$")) {
                txtEmptyError.setText("Invalid NI number!");
                return;
            }

            // Ensure hour salary is not negative and has at most 2 decimal places
            try {
                double salary = Double.parseDouble(hourlySalary);
                if (salary < 0) {
                    txtEmptyError.setText("Hourly salary cannot be negative!");
                    return;
                }
            } catch (NumberFormatException e) {
                txtEmptyError.setText("Invalid hourly salary!");
                return;
            }

            if (!hourlySalary.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                txtEmptyError.setText("Invalid hourly salary!");
                return;
            }

            // Checks if contracted hours is negative
            if (contractedHours < 0) {
                txtEmptyError.setText("Contracted hours cannot be negative!");
                return;
            }

            // Once input validation is passed, update the employee
            // Update the employee
            DatabaseController.UpdateTableData.updateEmployee(txtID.getText(), txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(),
                    txtPhone.getText(), txtHourlySalary.getText(), txtNiNumber.getText(),
                    Integer.parseInt((String)cboAccessLevel.getValue()), txtLocation.getText(),
                    txtContractType.getText(), txtContractedHours.getText(), txtDepartment.getText(),
                    txtJobTitle.getText());

            btnClear();
            btnRefresh();
            // Clear the error message
            txtEmptyError.setText("");
        }
    }

    public void btnDelete() {
        // Select the employee from the tableview and delete them
        TableView.TableViewSelectionModel<Person> selectionModel = EmployeeTable.getSelectionModel();

        if (selectionModel.isEmpty()) {
            // Show an error message
            txtEmptyError.setText("Please select an employee to delete!");
        } else {
            // Delete the employee
            DatabaseController.DeleteTableData.deleteEmployee(selectionModel.getSelectedItem().getEmployeeID(),
                    DatabaseController.GetTableData.getEmailById(
                            DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()));

            btnClear();
            btnRefresh();
        }

        DatabaseController.DeleteTableData.deleteEmployee(txtID.getText(),
                DatabaseController.GetTableData.getEmailById(
                        DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()));
    }

    public void btnClear() {
        // Clear the text fields
        txtID.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtHourlySalary.clear();
        txtNiNumber.clear();
        cboAccessLevel.setValue(null);
        txtLocation.clear();
        txtContractType.clear();
        txtContractedHours.clear();
        txtDepartment.clear();
        txtJobTitle.clear();
        txtSearch.clear();
    }

    @FXML
    private void searchForEmployee() {
        ObservableList<Person> filteredData = FXCollections.observableArrayList();

        String searchQuery = txtSearch.getText();

        // Iterate through your data and add matching rows to the filteredData
        for (Person person : DatabaseController.GetTableData.getAllEmployees()) {
            if (person.getEmployeeID().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    person.getFirstName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    person.getLastName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    person.getEmail().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredData.add(person);
            }
        }

        // Update the TableView with the filtered data
        EmployeeTable.setItems(filteredData);
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
            txtHourlySalary.setText(person.getHourlySalary());
            cboAccessLevel.setValue(person.getAccessLevel());
            txtNiNumber.setText(person.getNiNumber());
            txtLocation.setText(person.getLocation());
            txtContractType.setText(person.getContractType());
            txtContractedHours.setText(person.getContractedHours());
            txtDepartment.setText(person.getDepartment());
            txtJobTitle.setText(person.getJobTitle());
        }
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

    public void openAdmin(ActionEvent event) throws IOException {
        SceneController.openScene(event, "admin", stage, scene);
    }

    public void openHelp(ActionEvent event) throws IOException {
        SceneController.openScene(event, "help", stage, scene);
    }

    public void toggleTheme(ActionEvent event) throws IOException {
        ThemeManager.toggleMode();
    }
}

