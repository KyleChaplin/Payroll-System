module org.example.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.project.home to javafx.fxml;
    exports org.project.home;
}