package com.sdd.mapoverlay;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

import com.sdd.mapoverlay.utils.Records.Intersection;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.SegmentCollection;
import com.sdd.mapoverlay.utils.Store;


public class PlayPanelController {

    public static int currentStep;
    public static ArrayList<Intersection> pointsToPlace = new ArrayList<>();
    private Timeline timelineFwd, timelineBwd;

    @FXML
    public CheckBox displayOverlayCheckBox;
    @FXML
    public Button prevStepButton;
    @FXML
    public Button nextStepButton;
    @FXML
    public Label labelIntersection = new Label("No intersection found");


    @FXML
    protected void onPlayButtonClick() throws FileNotFoundException{
        
        /* System.out.println(pointsToPlace.size());
        System.out.println(currentStep); */

        // Reset the current step and disable the prevStepButton since you can't go before step 0
        currentStep = 0;

        pointsToPlace = computeOverlay();
        
        // We reset the chart to remove the previous intersection points
        System.out.println(Store.getRootController().lineChart.getData().size());
        Store.getRootController().lineChart.getData().remove(Store.getRootController().getSerieByName("Intersection_Points"));
        System.out.println("Intersection points series removed");
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
        if (currentStep <= 0) {
            prevStepButton.setDisable(true);
            currentStep = 0;
        } else {
            prevStepButton.setDisable(false);
        }
        if (currentStep == pointsToPlace.size() - 2){
            nextStepButton.setDisable(false);
        }
        labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(currentStep).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(currentStep).p().getY())+")");
    }

    @FXML
    protected void onNextStepButtonClick() {
        currentStep++;
        if (currentStep == pointsToPlace.size())
            currentStep--;
        Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());
        if (currentStep != 0)
            prevStepButton.setDisable(false);

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
                for (XYChart.Data<Number, Number> data : serie.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().toFront();
                        data.getNode().setVisible(visibility);
                    }
                }
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

    @FXML
    protected void onNextStepButtonPressed() {
        onNextStepButtonClick();
    }

    @FXML
    protected void onPrevStepButtonPressed() {
        onPrevStepButtonClick();
    }

    @FXML
    public void initialize() {
        timelineFwd = new Timeline(new KeyFrame(Duration.millis(80), event -> onNextStepButtonPressed()));
        timelineBwd = new Timeline(new KeyFrame(Duration.millis(80), event -> onPrevStepButtonPressed()));
        timelineFwd.setCycleCount(Animation.INDEFINITE);
        timelineBwd.setCycleCount(Animation.INDEFINITE);

        nextStepButton.setOnMousePressed(event -> {
            timelineFwd.play();
        });

        nextStepButton.setOnMouseReleased(event -> {
            timelineFwd.stop();
        });

        prevStepButton.setOnMousePressed(event -> {
            timelineBwd.play();
        });

        prevStepButton.setOnMouseReleased(event -> {
            timelineBwd.stop();
        });
    }
    
}
