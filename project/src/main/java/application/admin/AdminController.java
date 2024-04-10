package application.admin;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import application.help.HelpInfo;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
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
    private Pane paneHelp;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;
    @FXML
    private DatePicker dateEmailDate;
    @FXML
    private Button btnAdmin;
    @FXML
    private TableView<HelpInfo> tblHelp;
    @FXML
    private TableColumn<HelpInfo, String> tblRErrorCode;
    @FXML
    private TableColumn<HelpInfo, String> tblRTitle;
    @FXML
    private TableColumn<HelpInfo, String> tblRDesc;
    @FXML
    private TableColumn<HelpInfo, String> tblRAddedBy;
    @FXML
    private TextField txtErrorCode;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextArea txtDesc;
    @FXML
    private TextField txtAddedBy;
    @FXML
    private TableView<DeletedUser> tblDeletedUsers;
    @FXML
    private TableColumn<DeletedUser, String> tblRAccountNum;
    @FXML
    private TableColumn<DeletedUser, String> tblRAddedDate;
    @FXML
    private TableColumn<DeletedUser, String> tblRAddress1;
    @FXML
    private TableColumn<DeletedUser, String> tblRAddress2;
    @FXML
    private TableColumn<DeletedUser, String> tblRBankName;
    @FXML
    private TableColumn<DeletedUser, String> tblRCity;
    @FXML
    private TableColumn<DeletedUser, String> tblRDeleteDate;
    @FXML
    private TableColumn<DeletedUser, String> tblRDeletedBy;
    @FXML
    private TableColumn<DeletedUser, String> tblREmail;
    @FXML
    private TableColumn<DeletedUser, String> tblRFirstName;
    @FXML
    private TableColumn<DeletedUser, String> tblRLastName;
    @FXML
    private TableColumn<DeletedUser, String> tblRNiNum;
    @FXML
    private TableColumn<DeletedUser, String> tblRPhone;
    @FXML
    private TableColumn<DeletedUser, String> tblRPostcode;
    @FXML
    private TableColumn<DeletedUser, String> tblRSortcode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Integer.parseInt(DatabaseController.GetTableData.getAccessLevel(
                DatabaseController.GetTableData.getEmailById(
                        DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()))) == 0) {
            btnAdmin.setVisible(true);
        }

        openMainPane();
    }

    // *************
    // Email methods
    // *************
    @FXML
    public void updateDatabase() {
        DatabaseController.UpdateTableData.updateEmailInfo(txtEmail.getText(), txtPassword.getText(),
                formatDatabaseDate(dateEmailDate.getValue()));
        changePane("paneMain");
    }

    // Method to format date for database insertion
    private String formatDatabaseDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " 23:00:00";
    }

    private LocalDate formatDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDate.parse(dateString, formatter);
    }

    // ******************************
    // Deleted users (backup) methods
    // ******************************
    private void loadDeletedUserData() {
        ObservableList<DeletedUser> deletedUsersInfo = DatabaseController.GetTableData.getAllDeletedUserInfo();

        tblRAddedDate.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        tblRDeleteDate.setCellValueFactory(new PropertyValueFactory<>("deleteDate"));
        tblRDeletedBy.setCellValueFactory(new PropertyValueFactory<>("deletedBy"));
        tblRFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tblRLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tblREmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tblRPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tblRNiNum.setCellValueFactory(new PropertyValueFactory<>("niNumber"));
        tblRAddress1.setCellValueFactory(new PropertyValueFactory<>("addressLine1"));
        tblRAddress2.setCellValueFactory(new PropertyValueFactory<>("addressLine2"));
        tblRCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        tblRPostcode.setCellValueFactory(new PropertyValueFactory<>("postcode"));
        tblRAccountNum.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        tblRBankName.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        tblRSortcode.setCellValueFactory(new PropertyValueFactory<>("sortCode"));

        tblDeletedUsers.setItems(deletedUsersInfo);
    }

    @FXML
    public void btnDeleteUser() {
        // Get the selected item from the table view
        DeletedUser selectedUser = tblDeletedUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Delete the user from the database
            DatabaseController.DeleteTableData.removeUserData(selectedUser.getEmail(), selectedUser.getDeletedBy());
            // Remove the user from the table view
            tblDeletedUsers.getItems().remove(selectedUser);
        }
    }

    // *****************
    // Help pane methods
    // *****************
    private void loadHelpData() {
        // Load data from database
        ObservableList<HelpInfo> helpInfo = DatabaseController.GetTableData.getHelpInfo();

        tblRErrorCode.setCellValueFactory(new PropertyValueFactory<>("errorCode"));
        tblRTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblRDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        tblRAddedBy.setCellValueFactory(new PropertyValueFactory<>("addedBy"));

        tblHelp.setItems(helpInfo);
    }

    @FXML
    public void onTableHelpClick() {
        HelpInfo info = tblHelp.getSelectionModel().getSelectedItem();

        txtErrorCode.setText(info.getErrorCode());
        txtTitle.setText(info.getTitle());
        txtDesc.setText(info.getDescription());
        txtAddedBy.setText(info.getAddedBy());
    }

    @FXML
    public void btnUpdateHelp() {
        DatabaseController.UpdateTableData.updateHelp(txtErrorCode.getText(), txtTitle.getText(), txtDesc.getText(),
                DatabaseController.GetTableData.getEmailById(
                        DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()));

        loadHelpData();

        txtErrorCode.clear();
        txtTitle.clear();
        txtDesc.clear();
        txtAddedBy.clear();
    }

    @FXML
    public void btnDeleteHelp() {
        // Get the selected item from the table view
        HelpInfo info = tblHelp.getSelectionModel().getSelectedItem();

        if (info != null) {
            // Delete the user from the database
            DatabaseController.DeleteTableData.deleteHelp(info.getErrorCode());
            // Remove the user from the table view
            tblHelp.getItems().remove(info);

            txtErrorCode.clear();
            txtTitle.clear();
            txtDesc.clear();
            txtAddedBy.clear();
        }
    }

    // ***********************
    // Showing different panes
    // ***********************
    @FXML
    public void showBackupPane() {
        changePane("paneBackupData");

        loadDeletedUserData();
    }

    @FXML
    public void showEmailPane() {
        changePane("paneEmailServer");


        txtEmail.setText(DatabaseController.GetTableData.getEmailInfo());
        txtPassword.setText(DatabaseController.GetTableData.getPasswordInfo());
        dateEmailDate.setValue(formatDate(DatabaseController.GetTableData.getEmailDateInfo()));
    }

    @FXML
    public void showHelpPane() {
        changePane("paneHelp");

        loadHelpData();
    }

    @FXML
    public void openMainPane() {
        changePane("paneMain");
    }

    private void changePane(String paneName) {
        switch (paneName) {
            case "paneMain":
                paneMain.setVisible(true);
                paneBackupData.setVisible(false);
                paneEmailServer.setVisible(false);
                paneHelp.setVisible(false);
                break;
            case "paneEmailServer":
                paneMain.setVisible(false);
                paneBackupData.setVisible(false);
                paneEmailServer.setVisible(true);
                paneHelp.setVisible(false);
                break;
            case "paneBackupData":
                paneMain.setVisible(false);
                paneBackupData.setVisible(true);
                paneEmailServer.setVisible(false);
                paneHelp.setVisible(false);
                break;
            case "paneHelp":
                paneMain.setVisible(false);
                paneBackupData.setVisible(false);
                paneEmailServer.setVisible(false);
                paneHelp.setVisible(true);
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

    public void toggleTheme() throws IOException {
        ThemeManager.toggleMode();
    }
}
