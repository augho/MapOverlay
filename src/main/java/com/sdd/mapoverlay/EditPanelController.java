package com.sdd.mapoverlay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EditPanelController {
    public Button addSegmentButton;
    public TextField x1Field;
    public TextField y1Field;
    public TextField x2Field;
    public TextField y2Field;
    public Label segmentError;
    public Label loadFileError;

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

        float x1, y1, x2, y2;
        try {
            x1 = Float.parseFloat(x1Value);
            x2 = Float.parseFloat(x2Value);
            y2 = Float.parseFloat(y2Value);
            y1 = Float.parseFloat(y1Value);
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
        FileChooser fileChooser = new FileChooser();
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
        fileContent.forEach(System.out::println);

        try {
            List<List<Float>> parsedCoordinates = fileContent
                    .stream()
                    .map(line -> Arrays.stream(line.split("\\s+"))
                            .map(Float::parseFloat)
                            .collect(Collectors.toList()))
                    .toList();

            parsedCoordinates.forEach(segment -> {
                if (segment.size() != 4) {
                    throw new NumberFormatException("File format invalid");
                }
            });
            Store.getRootController().displayFileContent(parsedCoordinates);
            loadFileError.setVisible(false);
        } catch (NumberFormatException | NullPointerException e) {
            displayLoadFileError(e.getMessage());
        }

    }

    private void displayLoadFileError(String msg) {
        loadFileError.setText(msg);
        loadFileError.setVisible(true);
    }
}

