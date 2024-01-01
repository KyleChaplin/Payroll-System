module org.example.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    exports application;
    opens application to javafx.fxml;
    exports application.home;
    opens application.home to javafx.fxml;
    exports application.timeoff;
    opens application.timeoff to javafx.fxml;
}