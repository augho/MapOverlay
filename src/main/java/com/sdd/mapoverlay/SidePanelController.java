package com.sdd.mapoverlay;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SidePanelController {
    public VBox swapContainer;
    public Button playButton;
    public Button editButton;

    @FXML
    protected void onPlayButtonCLick() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SidePanelController.class.getResource("play-panel.fxml")
        );
        playButton.setDisable(true);
        editButton.setDisable(false);
        try {
            swapContainer.getChildren().set(0, fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("[ERR]Couldn't load edit-panel");
        }

    }
    @FXML
    protected void onEditButtonCLick() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SidePanelController.class.getResource("edit-panel.fxml")
        );
        playButton.setDisable(false);
        editButton.setDisable(true);
        try {
            swapContainer.getChildren().set(0, fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("[ERR]Couldn't load edit-panel");
        }
    }
}
