module com.sdd.mapoverlay {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sdd.mapoverlay to javafx.fxml;
    exports com.sdd.mapoverlay;
    exports com.sdd.mapoverlay.utils;
    opens com.sdd.mapoverlay.utils to javafx.fxml;
    exports com.sdd.mapoverlay.utils.Records;
    opens com.sdd.mapoverlay.utils.Records to javafx.fxml;
}