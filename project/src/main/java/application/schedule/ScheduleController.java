package application.schedule;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.util.Duration;

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

    @FXML
    private Label txtEmptyError;

    @FXML
    private TextField txtSearch;

    private int selectedWeek = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Integer.parseInt(DatabaseController.getAccessLevel(DatabaseController.getEmailById(DatabaseController.getCurrentLoggedInEmployeeId()))) == 0) {
            adminBox.setVisible(true);
            btnPurple.setVisible(true);
            btnGreen.setVisible(true);
            txtEmptyError.setVisible(true);
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

    @FXML
    private void searchForSchedule() {
        ObservableList<Schedule> filteredData = FXCollections.observableArrayList();

        String searchQuery = txtSearch.getText();

        // Iterate through your data and add matching rows to the filteredData
        for (Schedule schedule : getScheduleData(String.valueOf(selectedWeek))) {
            if (schedule.getEmployeeID().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    schedule.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredData.add(schedule);
            }
        }

        // Update the TableView with the filtered data
        Week0Table.setItems(filteredData);
    }

    private void updateSelectedWeekLabel() {
        lblSelectedWeek.setText("Week " + (selectedWeek + 1));
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
        String name = txtName.getText();

        // Get the employee ID from the text field
        String employeeID = txtEmployeeID.getText();

        Schedule schedule = new Schedule(name, employeeID);
        schedule.setWeekID(String.valueOf(selectedWeek));
        txtEmptyError.setText("");

        // Define the array of TextField pairs and their corresponding days
        TextField[] startFields = {txtMonStart, txtTueStart, txtWedStart, txtThuStart, txtFriStart, txtSatStart, txtSunStart};
        TextField[] endFields = {txtMonEnd, txtTueEnd, txtWedEnd, txtThuEnd, txtFriEnd, txtSatEnd, txtSunEnd};
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        // Validate and set schedule for each day
        for (int i = 0; i < startFields.length; i++) {
            TextField startField = startFields[i];
            TextField endField = endFields[i];

            if (startField.getText() != null && endField.getText() != null &&
                    isValidTime(startField.getText()) && isValidTime(endField.getText())) {
                setScheduleForDay(schedule, daysOfWeek[i], startField.getText(), endField.getText());
            } else if ((startField.getText() != null && endField.getText() != null) &&
                    (!isValidTime(startField.getText()) || !isValidTime(endField.getText()))) {
                txtEmptyError.setText("Please check " + daysOfWeek[i] + "'s times and try again!");
                // Set up a timeline to clear the error message after 5 seconds
                setupErrorTimer();
            }
        }

        return schedule;
    }

    // Validate time format (24-hour)
    private boolean isValidTime(String time) {
        if (time == null) {
            return false;
        }

        // Using regex to validate 24-hour time format (HH:mm)
        String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

        return time.matches(timeRegex);
    }

    // Helper method to set schedule for a specific day
    private void setScheduleForDay(Schedule schedule, String day, String startTime, String endTime) {
        switch (day) {
            case "Monday":
                schedule.setMonday(startTime, endTime);
                break;
            case "Tuesday":
                schedule.setTuesday(startTime, endTime);
                break;
            case "Wednesday":
                schedule.setWednesday(startTime, endTime);
                break;
            case "Thursday":
                schedule.setThursday(startTime, endTime);
                break;
            case "Friday":
                schedule.setFriday(startTime, endTime);
                break;
            case "Saturday":
                schedule.setSaturday(startTime, endTime);
                break;
            case "Sunday":
                schedule.setSunday(startTime, endTime);
                break;
            // Handle any additional cases or throw an exception for invalid day
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
    private void btnUpdate() {
        Schedule schedule = createScheduleFromTextFields();
        DatabaseController.updateSchedule(schedule);

        // Refresh the table
        loadTableData();
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
        txtEmptyError.setText("");
        txtSearch.setText("");
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
