package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    public void switchToSceneLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/login-view.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loginButtonOnAction(ActionEvent event) throws IOException {

        int loginCheck = checkLoginDetails();

        switch (loginCheck) {
            case 0:
                loginMessageLabel.setText("Please enter username and password.");
                break;
            case 1:
                loginMessageLabel.setText("Login Successful");

                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/home-view.fxml")));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                break;
            case -1:
                loginMessageLabel.setText("Invalid Login. Please try again.");
                break;
        }
    }

    public void closeApplication(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public int checkLoginDetails() {
        // TODO: Check login details against database
        int loginStatus;

        if (usernameTextField.getText().isBlank() && passwordTextField.getText().isBlank()) {
            loginStatus = 0;
        }
        else if (usernameTextField.getText().equals("admin") && passwordTextField.getText().equals("admin")) {
            loginStatus = 1;
        }
        else {
            loginStatus = -1;
        }

        return loginStatus;
    }
}
