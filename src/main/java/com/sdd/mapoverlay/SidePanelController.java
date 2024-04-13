package com.sdd.mapoverlay;

import java.io.IOException;
import java.util.Stack;

import com.sdd.mapoverlay.utils.Logs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidePanelController {
    public VBox swapContainer;
    public Button playButton;
    public Button editButton;
    public Button segmentsListButton;
    static Stack<Logs> logsHistory = new Stack<Logs>();

    /**
     * Event handler for the play button click.
     * Loads the "play-panel.fxml" file and swaps it with the current container.
     * Disables the play button and enables the edit button and segments list button.
     * If an error occurs while loading the file, an error message is printed to the console.
     */
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
            System.out.println("[ERR]Couldn't load play-panel");
        }

    }

    /**
     * Event handler for the edit button click.
     * Loads the "edit-panel.fxml" file using FXMLLoader and sets it as the first child of the swapContainer.
     * Enables the play button and segments list button, and disables the edit button.
     * If an IOException occurs while loading the "edit-panel.fxml" file, an error message is printed to the console.
     */
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

    /**
     * Handles the button click event for the "Segments List" button.
     * Loads the "segments-list.fxml" file and swaps it with the current container.
     * Enables the play and edit buttons, and disables the segments list button.
     */
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
            System.out.println("[ERR]Couldn't load segmentList-panel");
        }
    }
    
}
