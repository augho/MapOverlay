package com.sdd.mapoverlay.utils;

import com.sdd.mapoverlay.utils.Records.Intersection;
import com.sdd.mapoverlay.utils.Records.SegmentPair;
import com.sdd.mapoverlay.utils.Records.ULCSets;

import java.util.ArrayList;

public class SegmentCollection {
    private ArrayList<Intersection> overlay;
    private ArrayList<Segment> collection;

    public SegmentCollection(ArrayList<Segment> collection) {
        this.collection = collection;
    }

    public void setCollection(ArrayList<Segment> collection) {
        this.collection = collection;
        this.overlay = null;
    }
    public ArrayList<Intersection> getOverlay() {
        if (overlay == null) {
            overlay = findIntersections();
        }
        return overlay;
    }

    public ArrayList<Intersection> findIntersections() {
        if (collection == null) {
            throw new NullPointerException("No collection to run the algorithm on");
        }
        ArrayList<Intersection> newOverlay = new ArrayList<>();
        Q eventQueue = Q.getEmptyQueue();
        collection.forEach(s -> {
            eventQueue.insert(new EventPoint(s, s.getUpperEndpoint()));
            eventQueue.insert(new EventPoint(s, s.getLowerEndpoint()));
        });
        T statusStructure = T.getEmpty();
        System.out.println("Starting handling of event point");
        eventQueue.printTree();
        while (!eventQueue.isEmpty()) {
            handleEventPoint(eventQueue.popNextEvent(), statusStructure, eventQueue, newOverlay);
        }
        return overlay;
    }

    private void handleEventPoint(EventPoint p, T statusStructure, Q eventQueue, ArrayList<Intersection> overlay) {
        ULCSets ulcSets;
        // Line 2 in algo
        ulcSets = statusStructure.isEmpty() ? ULCSets.getEmpty() : statusStructure.findAllContaining(p);

        // Line 1 in algo
        ulcSets.U().addAll(p.getSegments()
                    .stream()
                    .filter(segment -> segment.getUpperEndpoint().sameAs(p))
                    .toList()
        );
        // Line 3 & 4
        if (ulcSets.getULCSize() > 1) {
            overlay.add(new Intersection(p, ulcSets.getCombinedSets()));
        }
        // Line 5
        ulcSets.C().forEach(statusStructure::delete);
        ulcSets.L().forEach(statusStructure::delete);
        // Line 6 & 7
        ulcSets.U().forEach(statusStructure::insert);
        statusStructure.printTree();
        ulcSets.C().forEach(statusStructure::insert);
        // Line 8 to 16
        if (ulcSets.U().isEmpty() && ulcSets.C().isEmpty()) {
            SegmentPair neighbours = statusStructure.findLeftAndRightNeighbour(p);
            if (neighbours.bothPresent()) {
                findNewEvent(neighbours.left(), neighbours.right(), p, p.getY(), eventQueue);
            }
        } else {
            SegmentPair extremesOfUC =  ulcSets.getEdgeSegmentsOfUC(p.getY(), p);
            extremesOfUC.getLeft().ifPresent(segment -> {
                statusStructure.findLeftNeighbour(segment, p.getY()).ifPresent(leftNeighbour -> {
                    findNewEvent(leftNeighbour, segment, p, p.getY(), eventQueue);
                });
            });
            extremesOfUC.getRight().ifPresent(segment -> {
                statusStructure.findRightNeighbour(segment, p.getY()).ifPresent(rightNeighbour -> {
                    findNewEvent(segment, rightNeighbour, p, p.getY(), eventQueue);
                });
            });
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
