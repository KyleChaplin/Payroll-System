package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class SceneController {

    private Stage stage;
    private Scene scene;

    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    public void loginButtonOnAction(ActionEvent event) throws IOException {
        removeErrorHighlight(usernameTextField);
        removeErrorHighlight(passwordTextField);

        switch (checkLoginDetails()) {
            case 0:
                loginMessageLabel.setText("Please enter username and password.");

                highlightError(usernameTextField);
                highlightError(passwordTextField);

                break;
            case 1:
                loginMessageLabel.setText("Login Successful");

                openScene(event, "home", stage, scene);

                break;
            case -1:
                loginMessageLabel.setText("Invalid Login. Please try again.");
                break;
        }
    }

    public int checkLoginDetails() {
        int loginStatus;

        if (usernameTextField.getText().isBlank() && passwordTextField.getText().isBlank()) {
            loginStatus = 0;
        } else if (DatabaseController.CheckTableData.checkLogin(usernameTextField.getText(), passwordTextField.getText())) {
            loginStatus = 1;
        } else {
            loginStatus = -1;
        }

        return loginStatus;
    }

    public static void openScene(ActionEvent event, String name, Stage stg, Scene scn) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(application.Main.class.getResource("views/" + name + "-view.fxml")));
        stg = (Stage)((Node)event.getSource()).getScene().getWindow();
        scn = new Scene(root);


        // Add a listener to the currentModeProperty
        Scene finalScn = scn;
        ThemeManager.currentModeProperty().addListener((observable, oldValue, newValue) -> {
            finalScn.getStylesheets().clear(); // Clear existing stylesheets
            finalScn.getStylesheets().add(Objects.requireNonNull(application.Main.class.getResource("css/" + newValue + ".css")).toExternalForm());
        });

        // Manually set the initial stylesheet
        scn.getStylesheets().add(Objects.requireNonNull(application.Main.class.getResource("css/" + ThemeManager.getCurrentMode() + ".css")).toExternalForm());

        stg.setTitle("Payroll - " + name);
        stg.setScene(scn);
        stg.show();
    }

    // Method to highlight the error border of the text field
    public static void highlightError(TextField textField) {
        textField.getStyleClass().add("error-border");
    }

    // Method to remove the error border from the text field
    public static void removeErrorHighlight(TextField textField) {
        textField.getStyleClass().remove("error-border");
    }

    public static void highlightEditable(TextField textField) {
        textField.getStyleClass().add("edit-border");
    }

    public static void removeEditableHighlight(TextField textField) {
        textField.getStyleClass().remove("edit-border");
    }
}
