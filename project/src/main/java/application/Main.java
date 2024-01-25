package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.sql.SQLException;
import java.util.Objects;

import application.email.PDFBox;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        DatabaseController.loadEnvVariables();
        DatabaseController.databaseChecks();

        // Testing directory creation for PDFs
        //PDFBox.createPDF("1");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/login-view.fxml")));
        Scene scene = new Scene(root);

        // Setting css
        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/" + ThemeManager.getCurrentMode() + ".css")).toExternalForm());

        // Add a listener to the currentModeProperty
        ThemeManager.currentModeProperty().addListener((observable, oldValue, newValue) -> {
            scene.getStylesheets().clear(); // Clear existing stylesheets
            scene.getStylesheets().add(Objects.requireNonNull(application.Main.class.getResource("css/" + newValue + ".css")).toExternalForm());
        });

        // Manually set the initial stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(application.Main.class.getResource("css/" + ThemeManager.getCurrentMode() + ".css")).toExternalForm());


        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Payroll - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}