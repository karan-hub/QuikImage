module org.example.aiservice {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.aiservice to javafx.fxml;
    exports org.example.aiservice;
}