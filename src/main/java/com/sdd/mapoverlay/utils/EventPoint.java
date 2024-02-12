package com.sdd.mapoverlay.utils;

import java.util.ArrayList;

public class EventPoint {
    private ArrayList<Segment> segments = new ArrayList<>();

    private Point point;

    public EventPoint(Segment segment, Point point) {
        this.segments.add(segment);
        this.point = point;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public Point getPoint() {
        return point;
    }

    public double y() {
        return this.getPoint().getY();
    }

    public double x() {
        return this.getPoint().getX();
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public boolean contains(Segment segment) {
        return this.segments.stream().anyMatch(s -> s.sameAs(segment));
    }

    public void addSegment(Segment segment) {
        this.segments.add(segment);
    }

    public boolean isRightOf(EventPoint eventPoint) {
        return !this.isLeftOf(eventPoint);
    }

    public boolean isLeftOf(EventPoint eventPoint) {
        // Higher y are treated first and left ones if same height
        // p < q => p.y > q.y OR (p.y == q.y AND p.x < q.x) if they're on the same horizontal line
        return this.y() > eventPoint.y() || this.y() == eventPoint.y() && this.x() < eventPoint.x();
    }
}
