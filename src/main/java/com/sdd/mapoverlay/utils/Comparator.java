package com.sdd.mapoverlay.utils;

public class Comparator {
    final static double errorMargin = 0.00001;

    public static boolean closeEnough(double a, double b) {
        return Math.abs(a - b) < errorMargin;
    }
    public static boolean closeEnough(Point a, Point b) {
        return closeEnough(a.getX(), b.getX()) && closeEnough(a.getY(), b.getY());
    }
    public static boolean sandwiched(double a, double b, double c) {
        if (a < c) {
            return a - errorMargin < b && b < c + errorMargin;
        } else {
            return c - errorMargin < b && b < a + errorMargin;
        }
    }

    public static double getErrorMargin() {
        return errorMargin;
    }
}
