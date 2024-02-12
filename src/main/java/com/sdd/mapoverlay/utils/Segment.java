package com.sdd.mapoverlay.utils;

import javafx.scene.chart.XYChart;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Segment {
    private Point lowerEndpoint, upperEndpoint;

    public Segment(Double x1, Double y1, Double x2, Double y2) {

        if (y1 < y2) {
            this.lowerEndpoint = new Point(x1, y1);
            this.upperEndpoint = new Point(x2, y2);
        } else if (y1 > y2) {
            this.lowerEndpoint = new Point(x2, y2);
            this.upperEndpoint = new Point(x1, y1);
        } else {
            // When horizontal left endpoint is upperEndpoint
            if(x1 < x2) {
                this.lowerEndpoint = new Point(x2, y2);
                this.upperEndpoint = new Point(x1, y1);
            } else {
                this.lowerEndpoint = new Point(x1, y1);
                this.upperEndpoint = new Point(x2, y2);
            }
        }
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

    public Point getLowerEndpoint() {
        return lowerEndpoint;
    }

    public Point getUpperEndpoint() {
        return upperEndpoint;
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
        return lowerEndpoint.toString() + " " + upperEndpoint.toString();
    }

    public boolean sameAs(Segment s) {
        return this.getUpperEndpoint().sameAs(s.getUpperEndpoint()) && this.lowerEndpoint.sameAs(s.getLowerEndpoint());
    }
}
