package com.sdd.mapoverlay.utils.Records;

import com.sdd.mapoverlay.utils.Comparator;
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
            // x coordinate of the segments on the sweep line
            final double sOnLine = segment.xAt(sweepLineY);
            final double lOnLine = leftmost.xAt(sweepLineY);
            final double rOnLine = rightmost.xAt(sweepLineY);
            if (Comparator.closeEnough(sOnLine, lOnLine)) {
                if (segment.getLowerEndpoint().getX() < leftmost.getLowerEndpoint().getX()) {
                    leftmost = segment;
                }
                if (rightmost.getLowerEndpoint().getX() < segment.getLowerEndpoint().getX()) {
                    rightmost = segment;
                }

            } else if (sOnLine < lOnLine) {
                leftmost = segment;

            } else if (rOnLine < sOnLine) {
                rightmost = segment;
            }
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
