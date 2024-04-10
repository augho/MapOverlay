package com.sdd.mapoverlay;

import javafx.fxml.FXML;
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
        RootController rootController = new RootController();
        System.out.println("Root controller created");
        System.out.println("Adding intersection points");
        /* for (Intersection point : pointsToPlace) {
            rootController.addInterPoint(point.p().getX(), point.p().getY());
        } */
        rootController.addIntersectionPoints(pointsToPlace);
        System.out.println("Intersection points added2");
    }

    public ArrayList<Segment> convertDataToSegments() {
        List<Segment> segmentsList = Store.getRootController().getChartContent();
        ArrayList<Segment> segmentsArrayList = new ArrayList<>(segmentsList);
        return segmentsArrayList;
    }
    

    public ArrayList<Intersection> computeOverlay() throws FileNotFoundException {
        System.out.println("Starting computing overlay");
        ArrayList<Segment> segments = convertDataToSegments();
        SegmentCollection segmentCollection = new SegmentCollection(segments);
        return segmentCollection.findIntersections();
    }

    @FXML
    protected void onDisplayOverlayClick() {
        boolean isChecked = displayOverlayCheckBox.isSelected();
        if (isChecked) {
            System.out.println("Display overlay is checked");
        } else {
            System.out.println("Display overlay is unchecked");
        }
    }
}
