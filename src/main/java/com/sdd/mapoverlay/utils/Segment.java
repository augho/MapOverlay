package com.sdd.mapoverlay.utils;

import javafx.scene.chart.XYChart;

import java.util.Arrays;
import java.util.List;

public class Segment {
    private Point a, b;

    public Segment(Double x1, Double y1, Double x2, Double y2) {
        this.a = new Point(x1, y1);
        this.b = new Point(x2, y2);
    }

    public static Segment fromString(String line) throws NumberFormatException {
        try {
            List<Double> parsed = Arrays.stream(line.split("\\s+"))
                    .map(Double::parseDouble)
                    .toList();
            if (parsed.size() != 4) {
                throw new NumberFormatException("File format incorrect");
            }
            return new Segment(parsed.get(0), parsed.get(1), parsed.get(2), parsed.get(3));
        } catch (NullPointerException e) {
            throw new NumberFormatException("File format incorrect");
        }
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public static Segment fromSeries(XYChart.Series<Number, Number> series) {
        return new Segment(
                (Double) series.getData().get(0).getXValue(),
                (Double) series.getData().get(0).getYValue(),
                (Double) series.getData().get(1).getXValue(),
                (Double) series.getData().get(1).getYValue()
        );
    }

    @Override
    public String toString() {
        return a.toString() + " " + b.toString();
    }
}
