module com.example.weatherappjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.weatherappjfx to javafx.fxml;
    exports com.example.weatherappjfx;
}
