package application.schedule;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static application.DatabaseController.getScheduleData;

public class ScheduleController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private TableView<Schedule> Week0Table;

    @FXML
    private TableView<Schedule> Week1Table;

    @FXML
    private TableView<Schedule> Week2Table;

    @FXML
    private TableView<Schedule> Week3Table;

    @FXML
    private Button btnDark;

    @FXML
    private Button btnGreen;

    @FXML
    private Button btnPurple;

    @FXML
    private TextField txtContractHours;

    @FXML
    private TextField txtEmployeeID;

    @FXML
    private TextField txtFriEnd;

    @FXML
    private TextField txtFriStart;

    @FXML
    private TextField txtMonEnd;

    @FXML
    private TextField txtMonStart;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPlannedHours;

    @FXML
    private TextField txtSatEnd;

    @FXML
    private TextField txtSatStart;

    @FXML
    private TextField txtSunEnd;

    @FXML
    private TextField txtSunStart;

    @FXML
    private TextField txtThuEnd;

    @FXML
    private TextField txtThuStart;

    @FXML
    private TextField txtTueEnd;

    @FXML
    private TextField txtTueStart;

    @FXML
    private TextField txtWedEnd;

    @FXML
    private TextField txtWedStart;

    @FXML
    private TableColumn<Schedule, String> employeeName;

    @FXML
    private TableColumn<Schedule, String> employeeID;

    @FXML
    private TableColumn<Schedule, String> FriEnd;

    @FXML
    private TableColumn<Schedule, String> FriStart;

    @FXML
    private TableColumn<Schedule, String> MonEnd;

    @FXML
    private TableColumn<Schedule, String> MonStart;

    @FXML
    private TableColumn<Schedule, String> SatEnd;

    @FXML
    private TableColumn<Schedule, String> SatStart;

    @FXML
    private TableColumn<Schedule, String> SunEnd;

    @FXML
    private TableColumn<Schedule, String> SunStart;

    @FXML
    private TableColumn<Schedule, String> ThuEnd;

    @FXML
    private TableColumn<Schedule, String> ThuStart;

    @FXML
    private TableColumn<Schedule, String> TueEnd;

    @FXML
    private TableColumn<Schedule, String> TueStart;

    @FXML
    private TableColumn<Schedule, String> WedEnd;

    @FXML
    private TableColumn<Schedule, String> WedStart;

    @FXML
    private HBox adminBox;

    @FXML
    private Label lblSelectedWeek;

    private int selectedWeek = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Integer.parseInt(DatabaseController.getAccessLevel(DatabaseController.getEmailById(DatabaseController.getCurrentLoggedInEmployeeId()))) == 0) {
            adminBox.setVisible(true);
            btnPurple.setVisible(true);
            btnGreen.setVisible(true);
        }

        DatabaseController.initializeScheduleForAllEmployees();

        //initializeColumns();
        loadTableData();

        updateSelectedWeekLabel();
    }

    private void initializeColumns() {
        // Organise the list of schedules into a table
        employeeName.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeID.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        MonStart.setCellValueFactory(new PropertyValueFactory<>("mondayStart"));
        MonEnd.setCellValueFactory(new PropertyValueFactory<>("mondayEnd"));
        TueStart.setCellValueFactory(new PropertyValueFactory<>("tuesdayStart"));
        TueEnd.setCellValueFactory(new PropertyValueFactory<>("tuesdayEnd"));
        WedStart.setCellValueFactory(new PropertyValueFactory<>("wednesdayStart"));
        WedEnd.setCellValueFactory(new PropertyValueFactory<>("wednesdayEnd"));
        ThuStart.setCellValueFactory(new PropertyValueFactory<>("thursdayStart"));
        ThuEnd.setCellValueFactory(new PropertyValueFactory<>("thursdayEnd"));
        FriStart.setCellValueFactory(new PropertyValueFactory<>("fridayStart"));
        FriEnd.setCellValueFactory(new PropertyValueFactory<>("fridayEnd"));
        SatStart.setCellValueFactory(new PropertyValueFactory<>("saturdayStart"));
        SatEnd.setCellValueFactory(new PropertyValueFactory<>("saturdayEnd"));
        SunStart.setCellValueFactory(new PropertyValueFactory<>("sundayStart"));
        SunEnd.setCellValueFactory(new PropertyValueFactory<>("sundayEnd"));
    }

    @FXML
    private void loadTableData() {
        // Get the data for the selected week
        ObservableList<Schedule> scheduleData = getScheduleData(String.valueOf(selectedWeek));

        initializeColumns();

        Week0Table.setItems(scheduleData);
    }


    private void updateSelectedWeekLabel() {
        lblSelectedWeek.setText("Week " + (selectedWeek + 1));
    }

    @FXML
    private void btnWeek0Click() {
        selectedWeek = 0;
        loadTableData();
        btnClear();
        updateSelectedWeekLabel();
    }

    @FXML
    private void btnWeek1Click() {
        selectedWeek = 1;
        loadTableData();
        btnClear();
        updateSelectedWeekLabel();
    }

    @FXML
    private void btnWeek2Click() {
        selectedWeek = 2;
        loadTableData();
        btnClear();
        updateSelectedWeekLabel();
    }

    @FXML
    private void btnWeek3Click() {
        selectedWeek = 3;
        loadTableData();
        btnClear();
        updateSelectedWeekLabel();
    }

    @FXML
    private void onTable0Click() {
        // Check if the tableview is empty
        if (!Week0Table.getSelectionModel().isEmpty()) {
            // Get the selected schedule
            Schedule schedule = Week0Table.getSelectionModel().getSelectedItem();

            setTextFields(schedule);
        }
    }

    private void setTextFields(Schedule schedule) {
        // Set the text fields to the values of the selected schedule
        txtName.setText(schedule.getName());
        txtEmployeeID.setText(schedule.getEmployeeID());
        txtMonStart.setText(schedule.getMondayStart());
        txtMonEnd.setText(schedule.getMondayEnd());
        txtTueStart.setText(schedule.getTuesdayStart());
        txtTueEnd.setText(schedule.getTuesdayEnd());
        txtWedStart.setText(schedule.getWednesdayStart());
        txtWedEnd.setText(schedule.getWednesdayEnd());
        txtThuStart.setText(schedule.getThursdayStart());
        txtThuEnd.setText(schedule.getThursdayEnd());
        txtFriStart.setText(schedule.getFridayStart());
        txtFriEnd.setText(schedule.getFridayEnd());
        txtSatStart.setText(schedule.getSaturdayStart());
        txtSatEnd.setText(schedule.getSaturdayEnd());
        txtSunStart.setText(schedule.getSundayStart());
        txtSunEnd.setText(schedule.getSundayEnd());
    }

    private Schedule createScheduleFromTextFields() {
        String MonStart = txtMonStart.getText();
        String MonEnd = txtMonEnd.getText();
        String TueStart = txtTueStart.getText();
        String TueEnd = txtTueEnd.getText();
        String WedStart = txtWedStart.getText();
        String WedEnd = txtWedEnd.getText();
        String ThuStart = txtThuStart.getText();
        String ThuEnd = txtThuEnd.getText();
        String FriStart = txtFriStart.getText();
        String FriEnd = txtFriEnd.getText();
        String SatStart = txtSatStart.getText();
        String SatEnd = txtSatEnd.getText();
        String SunStart = txtSunStart.getText();
        String SunEnd = txtSunEnd.getText();

        String name = txtName.getText();

        // Get the employee ID from the text field
        String employeeID = txtEmployeeID.getText();

        Schedule schedule = new Schedule(name, employeeID);
        schedule.setWeekID(String.valueOf(selectedWeek));
        schedule.setMonday(MonStart, MonEnd);
        schedule.setTuesday(TueStart, TueEnd);
        schedule.setWednesday(WedStart, WedEnd);
        schedule.setThursday(ThuStart, ThuEnd);
        schedule.setFriday(FriStart, FriEnd);
        schedule.setSaturday(SatStart, SatEnd);
        schedule.setSunday(SunStart, SunEnd);

        return schedule;
    }

    @FXML
    private void btnUpdate() {
        Schedule schedule = createScheduleFromTextFields();
        DatabaseController.updateSchedule(schedule);

        // Refresh the table
        loadTableData();
    }

    @FXML
    private void btnClear() {
        TextField[] textFields = {txtEmployeeID, txtName, txtMonStart, txtMonEnd, txtTueStart, txtTueEnd, txtWedStart, txtWedEnd, txtThuStart,
                txtThuEnd, txtFriStart, txtFriEnd, txtSatStart, txtSatEnd, txtSunStart, txtSunEnd};

        for (TextField textField : textFields) {
            // Check if the text field is empty
            if (textField.getText() != null) {
                // Clear the text field
                textField.clear();
            }
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
