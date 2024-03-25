package com.sdd.mapoverlay.utils;

import com.sdd.mapoverlay.utils.Records.ULCSets;

import java.util.ArrayList;

public class SegmentCollection {
    private ArrayList<Point> overlay;
    private ArrayList<Segment> collection;

    public void setCollection(ArrayList<Segment> collection) {
        this.collection = collection;
        this.overlay = null;
    }
    public ArrayList<Point> getOverlay() {
        if (overlay == null) {
            overlay = findIntersections();
        }
        return overlay;
    }

    private ArrayList<Point> findIntersections() {
        if (collection == null) {
            throw new NullPointerException("No collection to run the algorithm on");
        }
        ArrayList<Point> overlay = new ArrayList<>();
        Q eventQueue = Q.getEmptyQueue();
        collection.forEach(s -> {
            eventQueue.insert(new EventPoint(s, s.getUpperEndpoint()));
            eventQueue.insert(new EventPoint(s, s.getLowerEndpoint()));
        });
        T statusStructure = T.getEmpty(overlay);
        while (!eventQueue.isEmpty()) {
            handleEventPoint(eventQueue.popNextEvent(), statusStructure);
        }
        return overlay;
    }

    private void handleEventPoint(EventPoint p, T statusStructure) {
        ULCSets ulcSets;
        // Line 2 in algo
        if(p.getSegments().size() > 1) {
            ulcSets = statusStructure.findAllContaining(p);
        } else {
            ulcSets = ULCSets.getEmpty();
        }
        // Line 1 in algo
        ulcSets.U().addAll(p.getSegments()
                    .stream()
                    .filter(segment -> segment.getUpperEndpoint().sameAs(p))
                    .toList()
        );
        // Line 3 & 4
        if (ulcSets.getULCSize() > 1) {
            // TODO report as intersection
        }
        // Line 5
        ulcSets.C().forEach(statusStructure::delete);
        ulcSets.L().forEach(statusStructure::delete);
        // Line 6
        ulcSets.U().forEach(statusStructure::insert);
        ulcSets.C().forEach(statusStructure::insert);
        if (ulcSets.U().isEmpty() && ulcSets.C().isEmpty()) {

        }
    }

    private void findNewEvent(
            Segment leftSegment,
            Segment rightSegment,
            EventPoint p,
            Double sweepLineY,
            Q eventQueue
    ) {
        leftSegment.getIntersection(rightSegment).ifPresent(
                intersectionPoint -> {
                    if(intersectionPoint.getY() < sweepLineY) {
                        eventQueue.insert(new EventPoint(leftSegment, intersectionPoint));
                    }
                }
        );
    }
}
