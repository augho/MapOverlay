package com.sdd.mapoverlay.utils;

public class Point {
    Double x, y;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return x.toString() +  " " + y.toString();
    }

    public boolean sameAs(Point point) {
        return point.getX().equals(this.getX()) && point.getY().equals(this.getY());
    }
}
