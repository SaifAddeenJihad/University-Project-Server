module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires snappy.java;


    opens com.LabManagementAppUI to javafx.fxml;
    exports com.LabManagementAppUI;
}