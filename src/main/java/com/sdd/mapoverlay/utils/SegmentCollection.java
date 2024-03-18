package com.sdd.mapoverlay.utils;

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
            handleEventPoint(eventQueue.popNextEvent());
        }
        return overlay;
    }

    private void handleEventPoint(EventPoint p) {}

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
