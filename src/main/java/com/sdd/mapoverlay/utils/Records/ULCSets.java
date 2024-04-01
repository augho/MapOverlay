package com.sdd.mapoverlay.utils.Records;

import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Position;
import com.sdd.mapoverlay.utils.Segment;

import java.util.ArrayList;

public record ULCSets(ArrayList<Segment> U, ArrayList<Segment> L, ArrayList<Segment> C) {
    public static ULCSets getEmpty() {
        return new ULCSets(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public int getULCSize() {
        return U.size() + L.size() + C.size();
    }

    public SegmentPair getEdgeSegmentsOfUC(Double sweepLineY, Point eventPoint) {
        ArrayList<Segment> ucSets = new ArrayList<>(U);
        ucSets.addAll(C);
        Segment leftmost = ucSets.get(0);
        Segment rightmost = ucSets.get(0);
        for (Segment segment : ucSets) {
            if (segment.xAt(sweepLineY) < leftmost.xAt(sweepLineY)) {
                leftmost = segment;
            } else if (segment.xAt(sweepLineY) > rightmost.xAt(sweepLineY)) {
                rightmost = segment;
            }
        }
        // TODO Make sure those conditions are necessary
        if (leftmost.whereIs(eventPoint) != Position.RIGHT) {
            leftmost = null;
        }
        if (rightmost.whereIs(eventPoint) != Position.LEFT) {
            rightmost = null;
        }

        return new SegmentPair(
                leftmost,
                rightmost
        );
    }

    public ArrayList<Segment> getCombinedSets() {
        ArrayList<Segment> combinedSets = new ArrayList<>(U);
        combinedSets.addAll(L);
        combinedSets.addAll(C);

        return combinedSets;
    }
}
