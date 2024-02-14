package com.sdd.mapoverlay.utils;

import javafx.scene.chart.XYChart;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private double getLineCoefficient() {
        return (upperEndpoint.getX() - lowerEndpoint.getX()) / (upperEndpoint.getY() - lowerEndpoint.getY());
    }

    private double getLineOrigin() {
        return upperEndpoint.getY() - this.getLineCoefficient() * upperEndpoint.getX();
    }
    public Optional<Point> getIntersection(Segment segment) {
        /*
            For a segment given by 2 points {(x1, y1), (x2, y2)}
            coefficient a = (x2 - x1) / (y2 - y1)
            line of s1 is y = ax + b with b = y1 - a * x1

            line of s1 := y = ax + b
            line of s2 := y = cx + d
            Intersection at ax - cx = d - b => x = (d - b) / (a - c)
            Input x in one of the equations to get y

            Now need to check if the intersection point (u, v) belongs to the segments
            That is if  lowerEndpoint.x <= u <= upperEndpoint.x and lowerEndpoint.y <= v upperEndpoint.y for
            both segments

            Maybe some issue, parallels or overlapping segments
            - Parallel if a = c, that edge case is not supposed to happen I think
        */
        // TODO Round the result to 2 digits
        final double a = this.getLineCoefficient();
        final double b = this.getLineOrigin();

        final double c = segment.getLineCoefficient();
        final double d = segment.getLineOrigin();

        final double u = (d - b) / (a - c);
        final double v = a * u + b;
        System.out.println("s2 = " + segment);
        System.out.println("s1 = " + this);
        System.out.println("(u, v) = " + "("+ u + ", " + v + ")");
        return Optional.empty();
    }
}
