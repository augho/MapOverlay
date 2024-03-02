package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.Store;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditPanelController {
    public Button addSegmentButton;
    public TextField x1Field;
    public TextField y1Field;
    public TextField x2Field;
    public TextField y2Field;
    public Label segmentError;
    public Label loadFileError;
    public Label saveFileError;

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
        try {
            x1 = Double.parseDouble(x1Value);
            x2 = Double.parseDouble(x2Value);
            y2 = Double.parseDouble(y2Value);
            y1 = Double.parseDouble(y1Value);
        } catch (NumberFormatException e) {
            displayAddSegmentError(e.getMessage());
            return;
        }
        Store.getRootController().addSegment(x1, y1, x2, y2);
        segmentError.setVisible(false);
    }

    private void displayAddSegmentError(String msg) {
        segmentError.setText(msg);
        segmentError.setVisible(true);
    }

    @FXML
    protected void onLoadButtonClick() {
        String defaultDirectory = System.getProperty("user.home") + File.separator + "Downloads";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(defaultDirectory));
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

    }

    private void displayLoadFileError(String msg) {
        loadFileError.setText(msg);
        loadFileError.setVisible(true);
    }

    @FXML
    private void onSaveButtonClick() {
        String defaultDirectory = System.getProperty("user.home") + File.separator + "Downloads";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(defaultDirectory));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            displaySaveFileError("You need to select a file");
            return;
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            Store.getRootController().getChartContent()
                    .forEach(segment -> writer.println(segment.toString()));
        } catch (IOException e) {
            displaySaveFileError(e.getMessage());
        }
        saveFileError.setVisible(false);
    }

    private void displaySaveFileError(String msg) {
        saveFileError.setText(msg);
        saveFileError.setVisible(true);
    }
}

