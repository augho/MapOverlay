package com.sdd.mapoverlay;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidePanelController {
    public VBox swapContainer;
    public Button playButton;
    public Button editButton;
    public Button segmentsListButton;

    @FXML
    protected void onPlayButtonCLick() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SidePanelController.class.getResource("play-panel.fxml")
        );
        playButton.setDisable(true);
        editButton.setDisable(false);
        segmentsListButton.setDisable(false);
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
        segmentsListButton.setDisable(false);
        try {
            swapContainer.getChildren().set(0, fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("[ERR]Couldn't load edit-panel");
        }
    }

    @FXML
    protected void onSegmentsListButtonCLick() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SidePanelController.class.getResource("segments-list.fxml")
        );
        playButton.setDisable(false);
        editButton.setDisable(false);
        segmentsListButton.setDisable(true);
        try {
            swapContainer.getChildren().set(0, fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("[ERR]Couldn't load edit-panel");
        }
    }
    
}
