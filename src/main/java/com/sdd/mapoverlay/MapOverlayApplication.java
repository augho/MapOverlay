package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.Store;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// https://coolors.co/palette/03045e-0077b6-00b4d8-90e0ef-caf0f8
public class MapOverlayApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MapOverlayApplication.class.getResource("root.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        Store.setRootController(fxmlLoader);
        stage.setTitle("Map Overlay");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}