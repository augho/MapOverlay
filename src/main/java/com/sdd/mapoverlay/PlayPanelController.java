package com.sdd.mapoverlay;

import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

import com.sdd.mapoverlay.utils.Records.Intersection;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.SegmentCollection;
import com.sdd.mapoverlay.utils.Store;


public class PlayPanelController {
    @FXML
    public Button playButton;
    @FXML
    public CheckBox displayOverlayCheckBox;


    @FXML
    protected void onPlayButtonClick() throws FileNotFoundException{
        System.out.println("Play button clicked");
        ArrayList<Intersection> pointsToPlace = computeOverlay();
        System.out.println("Overlay computed");
        
        System.out.println("Adding intersection points");
        /* for (Intersection point : pointsToPlace) {
            rootController.addInterPoint(point.p().getX(), point.p().getY());
        } */
        Store.getRootController().addIntersectionPoints(pointsToPlace);
        System.out.println("Intersection points added2");
    }

    public ArrayList<Segment> convertDataToSegments() {
        List<Segment> segmentsList = Store.getRootController().getSegmentsFromChart();
        ArrayList<Segment> segmentsArrayList = new ArrayList<>(segmentsList);
        return segmentsArrayList;
    }
    

    public ArrayList<Intersection> computeOverlay() throws FileNotFoundException {
        System.out.println("Starting computing overlay");
        ArrayList<Segment> segments = convertDataToSegments();
        SegmentCollection segmentCollection = new SegmentCollection(segments);
        return segmentCollection.findIntersections();
    }

    public void setSeriesVisibility(boolean visibility) {
        try {
            XYChart.Series<Number, Number> serie = Store.getRootController().getSerieByName("Intersection_Points");
            if (serie != null) {
                System.out.println("Setting visibility to " + visibility + " for serie " + serie.getName());
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
