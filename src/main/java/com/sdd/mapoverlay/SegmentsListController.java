package com.sdd.mapoverlay;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import com.sdd.mapoverlay.utils.Logs;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.Store;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;

public class SegmentsListController implements Initializable {
    public ScrollPane segmentsList;
    public Button deleteButton;
    public Button undoButton;
    public ListView<Segment> segmentsListView;
    public static Stack<Logs> logsHistory = SidePanelController.logsHistory;

    public void setSegmentsList(List<Segment> segments) {
        segmentsListView.getItems().clear();
        for (Segment segment : segments) {
            segmentsListView.getItems().add(segment);
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        Segment segment = segmentsListView.getSelectionModel().getSelectedItem();
        if (segment != null) {
            segmentsListView.getItems().remove(segment);
            logsHistory.push(new Logs(segment, "DEL"));

            System.out.println("Showing logs");
            for (Logs log : logsHistory) {
                System.out.println(log.toString());
            }

            Store.getRootController().removeSegment(segment);
        }
        System.out.println("Deleting segment: " + segment.toString());
    }

    @FXML
    protected void onUndoButtonClick() {

        if (!(logsHistory.isEmpty())) {
            Logs topLog = logsHistory.pop();
            switch (topLog.getType()) {
                case "ADD":
                    Store.getRootController().addSegment(topLog.getSegment());
                    break;
                case "DEL":
                    Store.getRootController().removeSegment(topLog.getSegment());
                    break;
                default:
                    System.out.println("Unknown type (to treat)");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        segmentsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        List<Segment> segments = Store.getRootController().getChartContent();

        segmentsListView.getItems().addAll(segments);

        /* System.out.println("Segments: " + segments); */

        segmentsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Change the strike color of the newValue (Segment) in the chart to orange
            Store.getRootController().highlightSegment(newValue);
            /*
             * if (newValue != null)
             * System.out.println("Selected segment: " + newValue.toString());
             */
            deleteButton.setDisable(newValue == null);
        });
        undoButton.setDisable(logsHistory.isEmpty());

    }
}
