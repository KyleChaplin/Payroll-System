package application.admin;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private Pane paneMain;
    @FXML
    private Pane paneEmailServer;
    @FXML
    private Pane paneBackupData;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;
    @FXML
    private DatePicker dateEmailDate;
    @FXML
    private Button btnAdmin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Integer.parseInt(DatabaseController.getAccessLevel(DatabaseController.getEmailById(DatabaseController.getCurrentLoggedInEmployeeId()))) == 0) {
            btnAdmin.setVisible(true);
        }

        paneMain.setVisible(true);
        paneBackupData.setVisible(false);
        paneEmailServer.setVisible(false);
    }

    @FXML
    public void updateDatabase() {
        DatabaseController.updateEmailInfo(txtEmail.getText(), txtPassword.getText(),
                formatDatabaseDate(dateEmailDate.getValue()));
        changePane("paneMain");
    }

    // Method to format date for database insertion
    private String formatDatabaseDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " 23:00:00";
    }

    @FXML
    public void showBackupPane() {
        changePane("paneBackupData");
    }

    @FXML
    public void showEmailPane() {
        changePane("paneEmailServer");

        // TODO: Get data from database
        txtEmail.setText(DatabaseController.getEmailInfo());
        txtPassword.setText(DatabaseController.getPasswordInfo());
        dateEmailDate.setValue(formatDate(DatabaseController.getEmailDateInfo()));
    }

    private LocalDate formatDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDate.parse(dateString, formatter);
    }

    @FXML
    public void closeEmailPane() {
        changePane("paneMain");
    }

    private void changePane(String paneName) {
        switch (paneName) {
            case "paneMain":
                paneMain.setVisible(true);
                paneBackupData.setVisible(false);
                paneEmailServer.setVisible(false);
                break;
            case "paneEmailServer":
                paneMain.setVisible(false);
                paneBackupData.setVisible(false);
                paneEmailServer.setVisible(true);
                break;
            case "paneBackupData":
                paneMain.setVisible(false);
                paneBackupData.setVisible(true);
                paneEmailServer.setVisible(false);
                break;
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
