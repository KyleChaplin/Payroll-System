package application.payroll;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.DatabaseController.getAllEmployees;

public class PayrollController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private TableView<PayrollOverview> tblPayroll;

    @FXML
    private TableColumn<PayrollOverview, String> PayDate;

    @FXML
    private TableColumn<PayrollOverview, String> Month;

    @FXML
    private TableColumn<PayrollOverview, String> Total;

    @FXML
    private TableColumn<PayrollOverview, String> NoEmployees;

    @FXML
    private TableView<DetailedPayroll> tblEmployee;

    @FXML
    private TableColumn<DetailedPayroll, String> EmployeeID;

    @FXML
    private TableColumn<DetailedPayroll, String> FirstName;

    @FXML
    private TableColumn<DetailedPayroll, String> LastName;

    @FXML
    private TableColumn<DetailedPayroll, String> HoursWorked;

    @FXML
    private TableColumn<DetailedPayroll, String> BasePay;

    @FXML
    private TableColumn<DetailedPayroll, String> OvertimeWorked;

    @FXML
    private TableColumn<DetailedPayroll, String> OvertimePay;

    @FXML
    private TableColumn<DetailedPayroll, String> TaxPaid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up top table - displays general payroll information
        PayDate.setCellValueFactory(new PropertyValueFactory<PayrollOverview, String>("payDay"));
        Month.setCellValueFactory(new PropertyValueFactory<PayrollOverview, String>("month"));
        Total.setCellValueFactory(new PropertyValueFactory<PayrollOverview, String>("total"));
        NoEmployees.setCellValueFactory(new PropertyValueFactory<PayrollOverview, String>("noEmployees"));

        tblPayroll.setItems(DatabaseController.getPayrollOverviewForMonth());

        // Set up a listener for selection changes in the payroll overview table
        tblPayroll.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handlePayrollOverviewSelection(newValue));
    }

    private void handlePayrollOverviewSelection(PayrollOverview selectedPayroll) {
        if (selectedPayroll != null) {
            // Call the method to fetch detailed employee information for the selected month
            ObservableList<DetailedPayroll> employeeDetails =
                    DatabaseController.getEmployeeDetailsForMonth(selectedPayroll.getMonth());

            // Update the employee details table with the fetched data
            EmployeeID.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("employeeID"));
            FirstName.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("firstName"));
            LastName.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("lastName"));
            HoursWorked.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("hoursWorked"));
            BasePay.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("basePay"));
            OvertimeWorked.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("overtimeHours"));
            OvertimePay.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("overtimePay"));
            TaxPaid.setCellValueFactory(new PropertyValueFactory<DetailedPayroll, String>("taxPaid"));

            tblEmployee.setItems(employeeDetails);
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

    public void toggleTheme(ActionEvent event) throws IOException {
        ThemeManager.toggleMode();
    }

}
