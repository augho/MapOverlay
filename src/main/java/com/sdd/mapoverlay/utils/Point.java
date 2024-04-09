package com.sdd.mapoverlay.utils;

public class Point {
    private final double x, y;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return x +  " " + y;
    }

    private boolean exactSameAs(Point point) {
        return point.getX() == this.getX() && point.getY() == this.getY();
    }
    public boolean sameAs(Point point) {
        return Comparator.closeEnough(this, point);
    }
}
