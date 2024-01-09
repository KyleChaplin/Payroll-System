module org.example.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    exports application;
    opens application to javafx.fxml;
    exports application.home;
    exports application.schedule;
    exports application.payroll;
    exports application.employees;
    opens application.employees to javafx.fxml;
    exports application.timeoff;
    exports application.help;
}