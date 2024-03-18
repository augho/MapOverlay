package com.sdd.mapoverlay.utils;

import java.util.ArrayList;

public class EventPoint extends Point {
    private ArrayList<Segment> segments = new ArrayList<>();

    public EventPoint(Segment segment, Point point) {
        super(point.getX(), point.getY());
        this.segments.add(segment);
    }

    public EventPoint(ArrayList<Segment> segments, Point point) {
        super(point.getX(), point.getY());
        this.segments.addAll(segments);
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public boolean contains(Segment segment) {
        return this.segments.stream().anyMatch(s -> s.sameAs(segment));
    }

    public void addSegment(Segment segment) {
        this.segments.add(segment);
    }

    public void addSegments(ArrayList<Segment> segments) {
        this.segments.addAll(segments);
    }

    public Position compare(EventPoint eventPoint) {
        if (eventPoint.sameAs(this)) {
            return Position.INTERSECT;
        } else if (this.getY() > eventPoint.getY()
                || this.getY() == eventPoint.getY() && this.getX() < eventPoint.getX()) {
            return Position.LEFT;
        } else {
            return Position.RIGHT;
        }
    }
}
