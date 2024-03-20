package application.help;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private TextField txtHelp;

    @FXML
    private ScrollPane scrollHelp;

    @FXML
    private VBox vboxHelp;

    @FXML
    private Pane pane;

    @FXML
    private Pane addPane;

    @FXML
    private TextField txtErrorCode;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextArea txtDescription;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: This currently only works with dark mode - Setup support for light mode

        ObservableList<HelpInfo> helpInfo = (ObservableList<HelpInfo>) DatabaseController.getHelpInfo();

        VBox helpVbox = new VBox();

        // For loop to create the labels and text
        for (HelpInfo info : helpInfo) {

            Label errorCode = new Label();
            errorCode.setText(info.getErrorCode());

            Label title = new Label();
            title.setText(info.getTitle());

            Label description = new Label();
            description.setText(info.getDescription());

            VBox vbox = new VBox();
            vbox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;");
            vbox.getChildren().add(errorCode);
            vbox.getChildren().add(title);
            vbox.getChildren().add(description);

            pane = new Pane();
            pane.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20px; -fx-border-color: #3c3c3c; -fx-border-width: 1px;");

            pane.getChildren().add(vbox);

            helpVbox.getChildren().add(pane);
        }

        scrollHelp.setContent(helpVbox);
    }

    public void showAddPanel(ActionEvent event) throws IOException {
        addPane.setVisible(true);
        addPane.setDisable(false);
    }

    public void addHelpInfoToDB(ActionEvent event) throws IOException {
        DatabaseController.addHelp(txtErrorCode.getText(), txtTitle.getText(), txtDescription.getText());

        addPane.setVisible(false);
        addPane.setDisable(true);
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
        SceneController.openScene(event, "admin", stage, scene);
    }

    public void openHelp(ActionEvent event) throws IOException {
        SceneController.openScene(event, "help", stage, scene);
    }

    public void toggleTheme(ActionEvent event) throws IOException {
        ThemeManager.toggleMode();
    }
}
