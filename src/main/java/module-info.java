module com.sdd.mapoverlay {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sdd.mapoverlay to javafx.fxml;
    exports com.sdd.mapoverlay;
}