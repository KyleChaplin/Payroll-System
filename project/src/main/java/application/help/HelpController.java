package application.help;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class HelpController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private TextField txtSearch;
    @FXML
    private ScrollPane scrollHelp;
    @FXML
    private VBox vboxHelp;
    @FXML
    private Pane addPane;
    @FXML
    private TextField txtErrorCode;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button btnAdmin;
    @FXML
    private Label txtEmptyError;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Integer.parseInt(DatabaseController.GetTableData.getAccessLevel(
                DatabaseController.GetTableData.getEmailById(
                        DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()))) == 0) {
            btnAdmin.setVisible(true);
        }

        ObservableList<HelpInfo> helpInfo = DatabaseController.GetTableData.getHelpInfo();

        VBox helpVbox = new VBox();

        // For loop to create the labels and text
        for (HelpInfo info : helpInfo) {
            Pane pane = createHelpPane(info);
            helpVbox.getChildren().add(pane);
        }

        scrollHelp.setContent(helpVbox);
    }

    public void showAddPanel(ActionEvent event) {
        addPane.setVisible(true);
        addPane.setDisable(false);
    }

    public void addHelpInfoToDB(ActionEvent event) {
        SceneController.removeErrorHighlight(txtErrorCode);
        SceneController.removeErrorHighlight(txtTitle);
        txtDescription.getStyleClass().remove("error-border");

        // Get input from text fields
        String errorCode = txtErrorCode.getText();
        String title = txtTitle.getText();
        String description = txtDescription.getText();

        // Check if any required fields are empty
        if (errorCode.isEmpty() || title.isEmpty() || description.isEmpty()) {
            txtEmptyError.setText("All fields are required!");

            SceneController.highlightError(txtErrorCode);
            SceneController.highlightError(txtTitle);
            txtDescription.getStyleClass().add("error-border");

            return;
        }

        if (!errorCode.matches("^[A-Z]{3}\\d{3}$")) {
            txtEmptyError.setText("Invalid error code! Please use the format: ABC123");
            SceneController.highlightError(txtErrorCode);
            return;
        }

        // Add the help information to the database
        DatabaseController.AddTableData.addHelp(errorCode, title, description,
                DatabaseController.GetTableData.getEmailById(DatabaseController.GetTableData.getCurrentLoggedInEmployeeId()));

        // clear the text fields after adding the information
        txtErrorCode.clear();
        txtTitle.clear();
        txtDescription.clear();

        addPane.setVisible(false);
        addPane.setDisable(true);
    }

    @FXML
    public void closeAddPane() {
        SceneController.removeErrorHighlight(txtErrorCode);
        SceneController.removeErrorHighlight(txtTitle);
        txtDescription.getStyleClass().remove("error-border");

        txtEmptyError.setText("");

        txtErrorCode.clear();
        txtTitle.clear();
        txtDescription.clear();

        addPane.setVisible(false);
        addPane.setDisable(true);
    }

    @FXML
    private void searchForHelp() {
        //ObservableList<Schedule> filteredData = FXCollections.observableArrayList();
        // Get help information from the database
        ObservableList<HelpInfo> helpInfo = DatabaseController.GetTableData.getHelpInfo();

        // Clear existing panes before adding filtered ones
        VBox helpVbox = new VBox();

        String searchQuery = txtSearch.getText();

        // Filter the help information based on the search query
        for (HelpInfo info : helpInfo) {
            if (info.getErrorCode().toLowerCase().contains(searchQuery) ||
                    info.getTitle().toLowerCase().contains(searchQuery) ||
                    info.getDescription().toLowerCase().contains(searchQuery)) {

                // Create and add a pane for each matching help item
                Pane pane = createHelpPane(info);
                helpVbox.getChildren().add(pane);
            }
        }


        // Update the scroll pane content with the filtered panes
        scrollHelp.setContent(helpVbox);
    }

    private Pane createHelpPane(HelpInfo info) {
        Label errorCode = new Label(info.getErrorCode());
        Label title = new Label(info.getTitle());
        Label description = new Label(info.getDescription());

        VBox vbox = new VBox(errorCode, title, description);
        vbox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;");

        Pane pane = new Pane(vbox);
        pane.getStyleClass().add("help-pane");

        return pane;
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
