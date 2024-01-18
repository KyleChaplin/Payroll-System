package application.help;

import application.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: This will populate the scroll pane with the help information
        //  by dynamically creating the labels and text

        VBox helpVbox = new VBox();

        // For loop to create the labels and text
        for (int i = 0; i < 100; i++) {
            Label title = new Label();
            title.setText("This is a test");

            Label description = new Label();
            description.setText("This is a test description");

            VBox vbox = new VBox();
            vbox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;" );
            vbox.getChildren().add(title);
            vbox.getChildren().add(description);

            pane = new Pane();
            pane.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20px; -fx-border-color: #3c3c3c; -fx-border-width: 1px;");

            pane.getChildren().add(vbox);

            helpVbox.getChildren().add(pane);
        }

        scrollHelp.setContent(helpVbox);
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
}
