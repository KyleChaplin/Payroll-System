package application.schedule;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import application.payroll.DetailedPayroll;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScheduleController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private TableView<Schedule> Week0Table;
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
    @FXML
    private Button btnAdmin;

    private int selectedWeek = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Integer.parseInt(DatabaseController.GetTableData.getAccessLevel(
                DatabaseController.GetTableData.getEmailById(
                        DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()))) == 0) {
            adminBox.setVisible(true);
            btnPurple.setVisible(true);
            btnGreen.setVisible(true);
            txtEmptyError.setVisible(true);
            btnAdmin.setVisible(true);
        }

        DatabaseController.InitialiseTables.initialiseScheduleForAllEmployees();

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
        ObservableList<Schedule> scheduleData = DatabaseController.GetTableData.getScheduleData(String.valueOf(selectedWeek));

        initializeColumns();

        Week0Table.setItems(scheduleData);
    }

    @FXML
    private void searchForSchedule() {
        ObservableList<Schedule> filteredData = FXCollections.observableArrayList();

        String searchQuery = txtSearch.getText();

        // Iterate through your data and add matching rows to the filteredData
        for (Schedule schedule : DatabaseController.GetTableData.getScheduleData(String.valueOf(selectedWeek))) {
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
            changeBorder(true);
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
        txtContractHours.setText(String.valueOf(DatabaseController.GetTableData.getContractedHours(schedule.getEmployeeID())));
        txtPlannedHours.setText(String.valueOf(calculateTotalHoursWorked(schedule)));
    }

    public double calculateTotalHoursWorked(Schedule schedule) {
        double totalHours = 0;

        // Calculate hours worked for Monday
        double monHours = calculateHoursBetween(schedule.getMondayStart(), schedule.getMondayEnd());
        totalHours += monHours;

        // Calculate hours worked for Tuesday
        double tueHours = calculateHoursBetween(schedule.getTuesdayStart(), schedule.getTuesdayEnd());
        totalHours += tueHours;

        // Calculate hours worked for Wednesday
        double wedHours = calculateHoursBetween(schedule.getWednesdayStart(), schedule.getWednesdayEnd());
        totalHours += wedHours;

        // Calculate hours worked for Thursday
        double thuHours = calculateHoursBetween(schedule.getThursdayStart(), schedule.getThursdayEnd());
        totalHours += thuHours;

        // Calculate hours worked for Friday
        double friHours = calculateHoursBetween(schedule.getFridayStart(), schedule.getFridayEnd());
        totalHours += friHours;

        // Calculate hours worked for Saturday
        double satHours = calculateHoursBetween(schedule.getSaturdayStart(), schedule.getSaturdayEnd());
        totalHours += satHours;

        // Calculate hours worked for Sunday
        double sunHours = calculateHoursBetween(schedule.getSundayStart(), schedule.getSundayEnd());
        totalHours += sunHours;

        return totalHours;
    }

    private double calculateHoursBetween(String startTime, String endTime) {
        // Check if start time and end time are not null
        if (startTime != null && endTime != null) {
            String[] startTokens = startTime.split(":");
            String[] endTokens = endTime.split(":");

            // Extract hours and minutes from the time strings
            int startHour = Integer.parseInt(startTokens[0]);
            int startMinute = Integer.parseInt(startTokens[1]);
            int endHour = Integer.parseInt(endTokens[0]);
            int endMinute = Integer.parseInt(endTokens[1]);

            // Calculate total hours between start time and end time
            int totalMinutes = (endHour - startHour) * 60 + (endMinute - startMinute);
            return (double) totalMinutes / 60;
        } else {
            return 0.0; // Return 0 if either start time or end time is null
        }
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
        DatabaseController.UpdateTableData.updateSchedule(schedule);

        // Refresh the table
        loadTableData();

        calculatePayroll();

        // Clear all fields
        btnClear();
    }

    private void calculatePayroll() {
        // Update the payroll info for employees
        Map<String, Map<String, Double>> employeePayrollMap = new HashMap<>();

        // Iterate over each week to aggregate data
        for (int i = 0; i <= 3; i++) {
            ObservableList<Schedule> scheduleData = DatabaseController.GetTableData.getScheduleData(String.valueOf(i));

            for (Schedule s : scheduleData) {
                // Get employee ID for the current schedule
                String employeeId = s.getEmployeeID();

                // Get or initialize payroll info for the employee
                Map<String, Double> payrollInfo = employeePayrollMap.getOrDefault(employeeId, new HashMap<>());
                DetailedPayroll employeePayroll = DatabaseController.GetTableData.getSpecificEmployeePayroll(employeeId);

                String pensionString = employeePayroll.getPension();

                System.out.println(pensionString);

                // Parse the percentage value from the pension string
                double pensionPercentage = Double.parseDouble(pensionString.substring(0, pensionString.length() - 1)) / 100.0;

                // Get the employees hourly salary
                payrollInfo.put("hourlySalary", employeePayroll.getsalary());

                // Calculate overtime worked over the 4 weeks
                double contractedHours = DatabaseController.GetTableData.getContractedHours(s.getEmployeeID());
                payrollInfo.put("contractedHours", (contractedHours));
                double plannedHours = calculateTotalHoursWorked(s);
                payrollInfo.put("plannedHours", payrollInfo.getOrDefault("plannedHours", 0.0) + plannedHours);

                double overTimeWorked = Math.max(plannedHours - contractedHours, 0.0);
                s.setTotalOvertimeHours(s.getTotalOvertimeHours() + overTimeWorked);
                payrollInfo.put("totalOvertimeHours", payrollInfo.getOrDefault("totalOvertimeHours", 0.0) + overTimeWorked);


                // Update payroll info with data from the current schedule
                payrollInfo.put("totalHoursWorked", payrollInfo.getOrDefault("totalHoursWorked", 0.0) + calculateTotalHoursWorked(s));
                payrollInfo.put("pensionCon", pensionPercentage);
                payrollInfo.put("totalOvertimePay", payrollInfo.getOrDefault("totalOvertimeHours", 0.0) * payrollInfo.get("hourlySalary") + s.getTotalOvertimePay());
                payrollInfo.put("totalGrossPay", payrollInfo.get("totalHoursWorked") * payrollInfo.get("hourlySalary") + s.getTotalGrossPay());

                s.setTotalHoursWorked(payrollInfo.get("totalHoursWorked"));
                s.setTotalOvertimePay(payrollInfo.get("totalOvertimePay"));
                s.setTotalGrossPay(payrollInfo.get("totalGrossPay"));

                // Put updated payroll info back into the map
                employeePayrollMap.put(employeeId, payrollInfo);
            }

            // Loop for pension
            for (Map.Entry<String, Map<String, Double>> entry : employeePayrollMap.entrySet()) {
                Map<String, Double> payrollInfo = entry.getValue();

                // Retrieve total gross pay for the current employee
                double totalGrossPay = payrollInfo.get("totalGrossPay");

                // Fetch pension information for the current employee
                double pensionPercentage = payrollInfo.get("pensionCon");

                // Calculate pension deduction from gross pay
                double pensionDeduction = totalGrossPay * pensionPercentage;

                // Calculate net pay after pension deduction
                double netPay = totalGrossPay - pensionDeduction;

                double pensionPaid = totalGrossPay - netPay;
                payrollInfo.put("totalPensionPaid", pensionPaid);

                payrollInfo.put("netPay", netPay);
            }

            // Loop for tax
            for (Map.Entry<String, Map<String, Double>> entry : employeePayrollMap.entrySet()) {
                String employeeId = entry.getKey();
                Map<String, Double> payrollInfo = entry.getValue();

                // Retrieve total gross pay for the current employee
                double totalGrossPay = payrollInfo.get("totalGrossPay");
                double currentNetPay = payrollInfo.get("netPay");

                // Tax brackets and rates
                //double personalAllowance = 12570.0; // Personal allowance threshold for year
                //double basicRateThreshold = 50270.0; // Basic rate tax threshold for year
                //double higherRateThreshold = 125140.0; // Higher rate tax threshold for year

                double personalAllowance = 1047.50; // Personal allowance threshold for month
                double basicRateThreshold = 4189.16; // Basic rate tax threshold for month
                double higherRateThreshold = 10428.33; // Higher rate tax threshold for month

                // Calculate taxable income
                double taxableIncome = Math.max(currentNetPay - personalAllowance, 0);

                // Calculate tax based on different tax brackets
                double basicRate = Math.min(Math.max(taxableIncome, 0), basicRateThreshold) * 0.20;
                double higherRate = Math.min(Math.max(taxableIncome - basicRateThreshold, 0), higherRateThreshold - basicRateThreshold) * 0.40;
                double additionalRate = Math.max(taxableIncome - higherRateThreshold, 0) * 0.45;

                // Calculate total tax
                double totalTax = basicRate + higherRate + additionalRate;

                // Calculate net pay after taxes

                double netPay = currentNetPay - totalTax;

                // Update payroll information for the current employee
                payrollInfo.put("totalTax", totalTax);
                payrollInfo.put("netPay", netPay);
            }
        }

        // Loop to update payroll info
        for (Map.Entry<String, Map<String, Double>> entry : employeePayrollMap.entrySet()) {
            String employeeId = entry.getKey();
            Map<String, Double> payrollInfo = entry.getValue();

            DatabaseController.AddTableData.addOrUpdatePayrollInfo(employeeId, DatabaseController.GetTableData.getEmailDateInfo(),
            getCurrentMonthString(), getCurrentYear(), payrollInfo.get("totalHoursWorked"),
            String.format("%.0f%%", payrollInfo.get("pensionCon") * 100), payrollInfo.get("totalPensionPaid"),
            payrollInfo.get("totalOvertimeHours"), payrollInfo.get("totalOvertimePay"),
            payrollInfo.get("totalGrossPay"), payrollInfo.get("totalTax"), payrollInfo.get("netPay"));
        }
    }

    private static String getCurrentMonthString() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Format the current date to get the month name
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);

        return currentDate.format(formatter);
    }

    public static int getCurrentYear() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Extract the current year
        int currentYear = currentDate.getYear();

        return currentYear;
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
                txtThuEnd, txtFriStart, txtFriEnd, txtSatStart, txtSatEnd, txtSunStart, txtSunEnd, txtContractHours, txtPlannedHours};

        for (TextField textField : textFields) {
            // Check if the text field is empty
            if (textField.getText() != null) {
                // Clear the text field
                textField.clear();
            }
        }
        changeBorder(false);
        txtEmptyError.setText("");
        txtSearch.setText("");
    }

    private void changeBorder(boolean choice) {
        TextField[] startFields = {txtMonStart, txtTueStart, txtWedStart, txtThuStart, txtFriStart, txtSatStart, txtSunStart};
        TextField[] endFields = {txtMonEnd, txtTueEnd, txtWedEnd, txtThuEnd, txtFriEnd, txtSatEnd, txtSunEnd};

        List<TextField> TextFields = new ArrayList<>();

        for (int i = 0; i < startFields.length; i++) {
            TextField startField = startFields[i];
            TextField endField = endFields[i];

            TextFields.add(startField); // Add the start field to the list of error text fields
            TextFields.add(endField); // Add the end field to the list of error text fields

            if (choice) {
                for (TextField textField : TextFields) {
                    SceneController.highlightEditable(textField);
                }
            } else {
                for (TextField textField : TextFields) {
                    SceneController.removeEditableHighlight(textField);
                }
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
