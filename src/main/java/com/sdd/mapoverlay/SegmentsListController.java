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

    /**
     * Handles the delete button click event.
     * Removes the selected segment from the segments list view,
     * pushes a log entry for the deletion, and updates the undo button state.
     * Also removes the segment from the root controller.
     */
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

    /**
     * Handles the action when the "Undo" button is clicked.
     * If the logs history is not empty, it pops the top log from the history,
     * adds the segment associated with the log to the root controller,
     * and adds the segment to the segments list view.
     * If the logs history becomes empty after popping the top log,
     * the "Undo" button is disabled.
     */
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

    /**
     * Initializes the SegmentsListController.
     * This method is called after the FXML file has been loaded and the controller object has been created.
     * It sets up the initial state of the controller and registers event listeners.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
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
