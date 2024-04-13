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


    /**
     * Handles the event when the play button is clicked.
     * This method is responsible for initializing the necessary variables,
     * computing the overlay, updating the chart, and displaying the overlay.
     * It also adds the sweep line, sets the label text, and highlights the intersection point.
     *
     * @throws FileNotFoundException if the file is not found
     */
    @FXML
    protected void onPlayButtonClick() throws FileNotFoundException{
        currentStep = 0;

        pointsToPlace = computeOverlay();
        
        // We reset the chart to remove the previous intersection points
        Store.getRootController().lineChart.getData().remove(Store.getRootController().getSerieByName("Intersection_Points"));
        Store.getRootController().addIntersectionPoints(pointsToPlace);
        nextStepButton.setDisable(pointsToPlace.isEmpty());
        displayOverlayCheckBox.setSelected(true);

        try {
            Store.getRootController().addSweepLine();
            labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(0).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(0).p().getY())+")");
            Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    /**
     * Decreases the current step by one and updates the UI accordingly.
     * If the current step becomes less than or equal to zero, it is set to zero.
     * Highlights the intersection point of the current step on the map.
     * If the current step is equal to the second-to-last step, enables the next step button.
     * Updates the label to display the coordinates of the intersection point.
     */
    @FXML
    protected void onPrevStepButtonClick() {
        currentStep--;
        if (currentStep <= 0){
            currentStep = 0;
        }
        Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());   
        if (currentStep == pointsToPlace.size() - 2)
            nextStepButton.setDisable(false);
        
        labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(currentStep).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(currentStep).p().getY())+")");
    }

    /**
     * Increases the current step by one and updates the UI accordingly.
     * If the current step becomes greater than or equal to the size of the ArrayList of Intersection poins, it is set to its size minus one.
     * Highlights the intersection point of the current step on the map.
     * If the current step is different from 0, enables the previous step button.
     * Updates the label to display the coordinates of the intersection point.
     */
    @FXML
    protected void onNextStepButtonClick() {
        currentStep++;
        if (currentStep >= pointsToPlace.size()){
            currentStep = pointsToPlace.size()-1;
        }
        Store.getRootController().highlightIntersectionPoint(pointsToPlace.get(currentStep).p());
        if (currentStep != 0)
            prevStepButton.setDisable(false);

        labelIntersection.setText("Showing intersection ("+String.format("%,.2f",pointsToPlace.get(currentStep).p().getX())+", "+String.format("%,.2f", pointsToPlace.get(currentStep).p().getY())+")");
    }


    /**
     * Converts data to segments.
     * 
     * @return An ArrayList of Segment objects.
     */
    public ArrayList<Segment> convertDataToSegments() {
        List<Segment> segmentsList = Store.getRootController().getSegmentsFromChart();
        ArrayList<Segment> segmentsArrayList = new ArrayList<>(segmentsList);
        return segmentsArrayList;
    }
    

    /**
     * Computes the overlay of segments and returns a list of intersections.
     *
     * @return An ArrayList of Intersection objects representing the intersections of segments.
     * @throws FileNotFoundException if the data for segments is not found.
     */
    public ArrayList<Intersection> computeOverlay() throws FileNotFoundException {
        ArrayList<Segment> segments = convertDataToSegments();
        SegmentCollection segmentCollection = new SegmentCollection(segments);
        return segmentCollection.findIntersections();
    }

    /**
     * Sets the visibility of a series and its data points.
     *
     * @param visibility true to make the series and its data points visible, false to hide them
     */
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

    /**
     * Handles the event when the "Display Overlay" checkbox is clicked.
     * Sets the visibility of the series based on the state of the checkbox.
     */
    @FXML
    protected void onDisplayOverlayClick() {
        setSeriesVisibility(displayOverlayCheckBox.isSelected());
    }

    /**
     * Handles the event when the "Next Step" button is pressed.
     * Calls the {@link #onNextStepButtonClick()} method.
     */
    @FXML
    protected void onNextStepButtonPressed() {
        onNextStepButtonClick();
    }

    /**
     * Handles the event when the previous step button is pressed.
     * Calls the {@link #onPrevStepButtonClick()} method.
     */
    @FXML
    protected void onPrevStepButtonPressed() {
        onPrevStepButtonClick();
    }

    /**
     * Initializes the PlayPanelController.
     * This method is automatically called after the FXML file has been loaded and the controller has been instantiated.
     * It sets up the initial state of the PlayPanelController, including button disable states and event handlers.
     */
    @FXML
    public void initialize() {
        // Initialize the timeline for the next and previous step buttons
        nextStepButton.setDisable(pointsToPlace.isEmpty() || currentStep == pointsToPlace.size()-1);
        prevStepButton.setDisable((pointsToPlace.isEmpty() || currentStep == 0));
        timelineFwd = new Timeline(new KeyFrame(Duration.millis(100), event -> onNextStepButtonPressed()));
        timelineBwd = new Timeline(new KeyFrame(Duration.millis(100), event -> onPrevStepButtonPressed()));
        timelineFwd.setCycleCount(Animation.INDEFINITE);
        timelineBwd.setCycleCount(Animation.INDEFINITE);

        // Event handlers for the next and previous step buttons
        nextStepButton.setOnMousePressed(event -> {
            timelineFwd.play();
        });

        nextStepButton.setOnMouseReleased(event -> {
            timelineFwd.stop();
            if (currentStep==pointsToPlace.size()-1)
                nextStepButton.setDisable(true);
        });

        prevStepButton.setOnMousePressed(event -> {
            timelineBwd.play();
        });

        prevStepButton.setOnMouseReleased(event -> {
            timelineBwd.stop();
            if (currentStep == 0)
                prevStepButton.setDisable(true);
        });
    }
    
}
