module org.example.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.datatransfer;
    requires com.dustinredmond.fxtrayicon;
    requires java.mail;
    requires org.apache.logging.log4j;

    exports application;
    opens application to javafx.fxml;
    exports application.home;
    exports application.schedule;
    opens application.schedule to javafx.fxml;
    exports application.payroll;
    opens application.payroll to javafx.fxml;
    exports application.employees;
    opens application.employees to javafx.fxml;
    exports application.profile;
    opens application.profile to javafx.fxml;
    exports application.admin;
    exports application.help;
    opens application.help to javafx.fxml;
}