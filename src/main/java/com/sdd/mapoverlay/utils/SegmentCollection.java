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
        System.out.println("[LOG] Starting handling of event points");
//        eventQueue.printTree();
        while (!eventQueue.isEmpty()) {
            handleEventPoint(eventQueue.popNextEvent(), statusStructure, eventQueue, newOverlay);
        }
        overlay = newOverlay;
        return overlay;
    }

    private void handleEventPoint(EventPoint p, T statusStructure, Q eventQueue, ArrayList<Intersection> overlay) {
        Point bp4 = new Point(112.75205970860071, 162.77637250728935);
        Point bp3 = new Point(111.52268832789706, 157.7570826825404);
        Point bp2 = new Point(64.75, 157.36); // overlay_test
        Point breakPoint = new Point(106.91368780696644,138.93940904861532); // fichier1
        System.out.println("\n[HANDLING] event point: " + p);
        System.out.println(statusStructure.getStatus());
        // Why here ? p: 106.91368780696644 138.93940904861532 : 2 / s: 104.08 127.37 129.56 231.4
        // 104.08 127.37 129.56 231.4
        // 124.18 98.87 68.64 227.76
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
        ulcSets.C().forEach(segment -> statusStructure.delete(segment, p));
        ulcSets.L().forEach(segment -> statusStructure.delete(segment, p));

        // Line 6 & 7
        ulcSets.U().forEach(segment -> statusStructure.insert(segment, p));
        ulcSets.C().forEach(segment -> statusStructure.insert(segment, p));
        // Line 8 to 16
        if (ulcSets.U().isEmpty() && ulcSets.C().isEmpty()) {
            SegmentPair neighbours = statusStructure.findLeftAndRightNeighbour(p);
            if (neighbours.bothPresent()) {
                findNewEvent(neighbours.left(), neighbours.right(), p, eventQueue);
            }
        } else {
            SegmentPair extremesOfUC =  ulcSets.getEdgeSegmentsOfUC(p.getY(), p);
            System.out.println(statusStructure.getStatus());
            extremesOfUC.getLeft().ifPresent(segment -> {
                statusStructure.findLeftNeighbour(segment, p.getY()).ifPresent(leftNeighbour -> {
                    findNewEvent(leftNeighbour, segment, p, eventQueue);
                });
            });

            extremesOfUC.getRight().ifPresent(segment -> {
                statusStructure.findRightNeighbour(segment, p.getY()).ifPresent(rightNeighbour -> {
                    findNewEvent(segment, rightNeighbour, p, eventQueue);
                });
            });
        }
        System.out.println("[POST HANDLING]");
        System.out.println(statusStructure.getStatus());
    }

    private void findNewEvent(
            Segment leftSegment,
            Segment rightSegment,
            Point eventPoint,
            Q eventQueue
    ) {
        // TODO remake & approximation
        double sweepLineY = eventPoint.getY();
        leftSegment.getIntersection(rightSegment).ifPresent(intersectionPoint -> {
                if(intersectionPoint.getY() < sweepLineY ||
                        intersectionPoint.getY() == sweepLineY && intersectionPoint.getX() > eventPoint.getX()) {
                    ArrayList<Segment> intersectionPair = new ArrayList<>();
                    intersectionPair.add(leftSegment);
                    intersectionPair.add(rightSegment);

                    eventQueue.insert(new EventPoint(intersectionPair, intersectionPoint));
                }
        });
    }
}
