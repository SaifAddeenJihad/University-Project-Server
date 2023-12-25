module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires snappy.java;
    requires org.apache.logging.log4j;


    opens com.LabManagementAppUI to javafx.fxml;
    exports com.LabManagementAppUI;
}