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

    @FXML
    protected void onDeleteButtonClick() {
        Segment segment = segmentsListView.getSelectionModel().getSelectedItem();
        if (segment != null) {
            segmentsListView.getItems().remove(segment);
            logsHistory.push(new Logs(segment, "DEL"));
            undoButton.setDisable(logsHistory.isEmpty());
            
            Store.getRootController().removeSegment(segment);
        }
    }

    @FXML
    protected void onUndoButtonClick() {

        if (!(logsHistory.isEmpty())) {
            Logs topLog = logsHistory.pop();
            if (logsHistory.isEmpty()) {
                undoButton.setDisable(true);
            }
            Store.getRootController().addSegment(topLog.getSegment());
            segmentsListView.getItems().add(topLog.getSegment());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        segmentsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        List<Segment> segments = Store.getRootController().getSegmentsFromChart();
        //System.out.println("Segments in the chart: " + segments.size() + " segments");

        segmentsListView.getItems().addAll(segments);

        segmentsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Change the strike color of the newValue (Segment) in the chart to orange
            Store.getRootController().highlightSegment(newValue);
            deleteButton.setDisable(newValue == null);
        });
        undoButton.setDisable(logsHistory.isEmpty());

    }
}
