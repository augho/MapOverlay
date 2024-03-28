package com.sdd.mapoverlay.utils.Records;

import com.sdd.mapoverlay.utils.Segment;

public record SegmentPair(Segment left, Segment right) {
    public boolean bothPresent() {
        return left != null && right != null;
    }
}
