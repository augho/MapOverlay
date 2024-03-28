package com.sdd.mapoverlay.utils.Records;

import com.sdd.mapoverlay.utils.Segment;

import java.util.ArrayList;

public record ULCSets(ArrayList<Segment> U, ArrayList<Segment> L, ArrayList<Segment> C) {
    public static ULCSets getEmpty() {
        return new ULCSets(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public int getULCSize() {
        return U.size() + L.size() + C.size();
    }

    public SegmentPair getEdgeSegmentsOfUC() {
        return null;
    }
}
