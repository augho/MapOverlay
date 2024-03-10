package com.sdd.mapoverlay;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.Store;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;


public class SegmentsListController implements Initializable{
    public ScrollPane segmentsList;
    public Button deleteButton;
    public ListView<Segment> segmentsListView;

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
            Store.getRootController().removeSegment(segment);
        }
        System.out.println("Deleting segment: " + segment.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        segmentsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        List<Segment> segments = Store.getRootController().getChartContent();

        segmentsListView.getItems().addAll(segments);

        /* System.out.println("Segments: " + segments); */

        
        segmentsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            /* if (newValue != null)
                System.out.println("Selected segment: " + newValue.toString()); */
            deleteButton.setDisable(newValue == null);
        });
        
    }
}