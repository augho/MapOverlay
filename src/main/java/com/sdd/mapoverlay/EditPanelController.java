package com.sdd.mapoverlay;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


import com.sdd.mapoverlay.utils.Logs;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.Store;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class EditPanelController {
    public Button addSegmentButton;
    public TextField x1Field;
    public TextField y1Field;
    public TextField x2Field;
    public TextField y2Field;
    public Label segmentError;
    public Label loadFileError;
    public Label saveFileError;
    public static Stack<Logs> logsHistory = SegmentsListController.logsHistory;

    /**
     * Handles the button click event for adding a segment.
     * Retrieves the x and y values from the input fields, validates them,
     * and creates a new segment object. The segment is then added to the root controller
     * and a log entry is pushed to the logs history. Finally, the segment error visibility is set to false.
     */
    @FXML
    protected void onAddSegmentButtonClick() {
        String x1Value = this.x1Field.getText();
        String y1Value = this.y1Field.getText();
        String x2Value = this.x2Field.getText();
        String y2Value = this.y2Field.getText();

        x1Field.setText("");
        y1Field.setText("");
        x2Field.setText("");
        y2Field.setText("");

        if (Objects.equals(x1Value, "") || Objects.equals(y1Value, "") ||
                Objects.equals(x2Value, "") || Objects.equals(y2Value, "")) {
            displayAddSegmentError("Both x and y value must be declared");
            return;
        }

        double x1, y1, x2, y2;

        Store.getRootController().upperBoundX = Math.max(Store.getRootController().upperBoundX, Double.parseDouble(x1Value));
        Store.getRootController().upperBoundX = Math.max(Store.getRootController().upperBoundX, Double.parseDouble(x2Value));

        try {
            x1 = Double.parseDouble(x1Value);
            x2 = Double.parseDouble(x2Value);
            y2 = Double.parseDouble(y2Value);
            y1 = Double.parseDouble(y1Value);
        } catch (NumberFormatException e) {
            displayAddSegmentError(e.getMessage());
            return;
        }
        Segment segment = new Segment(x1, y1, x2, y2);
        Store.getRootController().addSegment(segment);
        logsHistory.push(new Logs(segment, "ADD"));
        segmentError.setVisible(false);
    }

    /**
     * Displays an error message for adding a segment.
     *
     * @param msg the error message to display
     */
    private void displayAddSegmentError(String msg) {
        segmentError.setText(msg);
        segmentError.setVisible(true);
    }

    /**
     * Handles the event when the "Load" button is clicked.
     * Opens a file chooser dialog to allow the user to select a text file.
     * Reads the selected file and processes its content to display on the UI.
     * If an error occurs during the file loading or processing, an error message is displayed.
     */
    @FXML
    protected void onLoadButtonClick() {
        String defaultDirectory = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
    	
        //Check if defaultDirectory+File.Separator+"Downloads" exists, if not, same for "Téléchargements", if not just open the defaultDirectory
        if (new File(defaultDirectory + File.separator + "Downloads").exists()) {
            fileChooser.setInitialDirectory(new File(defaultDirectory + File.separator + "Downloads"));
        } else if (new File(defaultDirectory + File.separator + "Téléchargements").exists()) {
            fileChooser.setInitialDirectory(new File(defaultDirectory + File.separator + "Téléchargements"));
        } else {
            fileChooser.setInitialDirectory(new File(defaultDirectory));
        }

        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            displayLoadFileError("You need to select a file");
            return;
        }
        if (file.isDirectory()) {
            displayLoadFileError("You can't select a directory");
        }
        final String filename = file.getName();
        if (!filename.endsWith(".txt")) {
            displayLoadFileError("You must select a text file");
        }

        ArrayList<String> fileContent = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line.strip());
            }
        } catch (IOException e) {
            displayLoadFileError(e.getMessage());
        }

        try {
            List<Segment> segments = fileContent
                    .stream()
                    .map(Segment::fromString)
                    .toList();

            Store.getRootController().displayFileContent(segments);
            loadFileError.setVisible(false);
        } catch (NumberFormatException e) {
            displayLoadFileError(e.getMessage());
        }

        //Clear the logsHistory (Stack)
        logsHistory.clear();
        PlayPanelController.pointsToPlace.clear();
    }

    private void displayLoadFileError(String msg) {
        loadFileError.setText(msg);
        loadFileError.setVisible(true);
    }

    /**
     * Handles the action when the save button is clicked.
     * Prompts the user to select a file to save the data to.
     * If a file is selected, the data from the chart's segments is written to the file.
     * If no file is selected, an error message is displayed.
     */
    @FXML
    private void onSaveButtonClick() {
        String defaultDirectory = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
    	
        //Check if defaultDirectory+File.Separator+"Downloads" exists, if not, same for "Téléchargements", if not just open the defaultDirectory
        if (new File(defaultDirectory + File.separator + "Downloads").exists()) {
            fileChooser.setInitialDirectory(new File(defaultDirectory + File.separator + "Downloads"));
        } else if (new File(defaultDirectory + File.separator + "Téléchargements").exists()) {
            fileChooser.setInitialDirectory(new File(defaultDirectory + File.separator + "Téléchargements"));
        } else {
            fileChooser.setInitialDirectory(new File(defaultDirectory));
        }

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            displaySaveFileError("You need to select a file");
            return;
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            Store.getRootController().getSegmentsFromChart()
                    .forEach(segment -> writer.println(segment.toString()));
        } catch (IOException e) {
            displaySaveFileError(e.getMessage());
        }
        saveFileError.setVisible(false);
    }

    /**
     * Displays an error message when there is an issue saving the file.
     *
     * @param msg The error message to display.
     */
    private void displaySaveFileError(String msg) {
        saveFileError.setText(msg);
        saveFileError.setVisible(true);
    }
}

