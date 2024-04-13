package com.sdd.mapoverlay;

import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

import com.sdd.mapoverlay.utils.Records.Intersection;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.SegmentCollection;
import com.sdd.mapoverlay.utils.Store;


public class PlayPanelController {

    public int currentStep = 0;
    public ArrayList<Intersection> pointsToPlace = new ArrayList<>();
    @FXML
    public Button playButton;
    @FXML
    public CheckBox displayOverlayCheckBox;
    @FXML
    public Button prevStepButton;
    @FXML
    public Button nextStepButton;
    @FXML
    public Label labelIntersection = new Label();


    @FXML
    protected void onPlayButtonClick() throws FileNotFoundException{
        // Reset the current step and disable the prevStepButton since you can't go before step 0
        currentStep = 0;
        prevStepButton.setDisable(true);

        pointsToPlace = computeOverlay();
        
        Store.getRootController().addIntersectionPoints(pointsToPlace);

        Store.getRootController().addSweepLine();

        displayOverlayCheckBox.setSelected(true);
        try {
            labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(0).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(0).p().getY())+")");
            Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    @FXML
    protected void onPrevStepButtonClick() {
        currentStep--;
        Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());
        if (currentStep == 0) {
            prevStepButton.setDisable(true);
        }
        if (currentStep == pointsToPlace.size() - 2){
            nextStepButton.setDisable(false);
        }
        labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(currentStep).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(currentStep).p().getY())+")");
    }

    @FXML
    protected void onNextStepButtonClick() {
        currentStep++;
        Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());
        if (currentStep == 1) {
            prevStepButton.setDisable(false);
        }
        if (currentStep == pointsToPlace.size() - 1){
            nextStepButton.setDisable(true);
        }
        labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(currentStep).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(currentStep).p().getY())+")");
    }


    public ArrayList<Segment> convertDataToSegments() {
        List<Segment> segmentsList = Store.getRootController().getSegmentsFromChart();
        ArrayList<Segment> segmentsArrayList = new ArrayList<>(segmentsList);
        return segmentsArrayList;
    }
    

    public ArrayList<Intersection> computeOverlay() throws FileNotFoundException {
        ArrayList<Segment> segments = convertDataToSegments();
        SegmentCollection segmentCollection = new SegmentCollection(segments);
        return segmentCollection.findIntersections();
    }

    public void setSeriesVisibility(boolean visibility) {
        try {
            XYChart.Series<Number, Number> serie = Store.getRootController().getSerieByName("Intersection_Points");
            if (serie != null) {
                serie.getNode().setVisible(visibility);
            }
            // Hide or show all the data points
            for (XYChart.Data<Number, Number> data : serie.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setVisible(visibility);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    @FXML
    protected void onDisplayOverlayClick() {
        setSeriesVisibility(displayOverlayCheckBox.isSelected());
    }
}
