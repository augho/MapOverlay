package com.sdd.mapoverlay.utils;

public class Comparator {

    public static boolean closeEnough(double a, double b) {
        final double errorMargin = 0.0001;
        return Math.abs(a - b) < errorMargin;
    }
    public static boolean closeEnough(Point a, Point b) {
        return closeEnough(a.getX(), b.getX()) && closeEnough(a.getY(), b.getY());
    }
}
