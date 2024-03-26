package application;

import application.email.Email;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.mail.MessagingException;
import java.io.*;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        DatabaseController.loadEnvVariables();
        DatabaseController.databaseChecks();

        //Email.sendEmailTask();    // Uncomment to enable emails sending

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/login-view.fxml")));
        Scene scene = new Scene(root);

        // Set up the system tray
        sysTraySetup(stage);

        // Setting the css
        // Add a listener to the currentModeProperty
        ThemeManager.currentModeProperty().addListener((observable, oldValue, newValue) -> {
            scene.getStylesheets().clear(); // Clear existing stylesheets
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("css/" + newValue + ".css")).toExternalForm());
        });

        // Manually set the initial stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("css/" + ThemeManager.getCurrentMode() + ".css")).toExternalForm());

        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Payroll - Login");
        stage.setScene(scene);
        stage.show();
    }

    private static void sysTraySetup(Stage stage) {
        FXTrayIcon trayIcon = new FXTrayIcon(stage);

        trayIcon.setTooltip("Payroll Application");

        MenuItem item1 = new MenuItem("Exit Application");  // Exit the application
        MenuItem item2 = new MenuItem("Show Application");  // Open the application

        item1.setOnAction(e -> {
            System.exit(0);
        });

        item2.setOnAction(e -> {
            stage.show();
        });

        trayIcon.addMenuItem(item2);
        trayIcon.addMenuItem(item1);

        trayIcon.show();
    }

    public static void main(String[] args) {
        launch();
    }
}