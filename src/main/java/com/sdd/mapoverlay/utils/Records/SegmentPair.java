package com.sdd.mapoverlay.utils.Records;

import com.sdd.mapoverlay.utils.Segment;

import java.util.Optional;

public record SegmentPair(Segment left, Segment right) {
    public boolean bothPresent() {
        return left != null && right != null;
    }

    public Optional<Segment> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<Segment> getRight() {
        return Optional.ofNullable(right);
    }
}
