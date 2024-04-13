package com.sdd.mapoverlay.utils;

import javafx.scene.chart.XYChart;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Segment {
    private final Point lowerEndpoint, upperEndpoint;

    private final SegmentType segmentType;

    private final Double slope;
    private final Double lineOrigin;

    public Segment(double x1, double y1, double x2, double y2) {
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
        if(y1 == y2) {
            segmentType = SegmentType.HORIZONTAL;
            slope = 0.0;
            lineOrigin = y1;
        } else if(x1 == x2) {
            segmentType = SegmentType.VERTICAL;
            slope = Double.NaN;
            lineOrigin = Double.NaN;
        } else {
            segmentType = SegmentType.SLOPED;
            slope = (y2 - y1) / (x2 - x1);
            lineOrigin = y1 - slope * x1;
        }
    }
    public static Segment getSegment(Point p1, Point p2) {
        return new Segment(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Parse a segment from a line of a txt file containing 4 decimal numbers
     * @param line Line of the txt file
     * @return Parsed segment
     * @throws NumberFormatException When the file isn't formatted correctly
     */
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
        return lowerEndpoint + " " + upperEndpoint;
    }

    public String readableToString() {
        return Integer.toString((int) upperEndpoint.getX());
    }

    /**
     * Checks if the segments endpoints match
     * @param s Other segment
     * @return true if equals
     */
    public boolean sameAs(Segment s) {
        return upperEndpoint.sameAs(s.getUpperEndpoint()) && lowerEndpoint.sameAs(s.getLowerEndpoint());
    }

    /**
     * @return The coefficient of the line containing this segment
     */
    public double getSlope() {
        return slope;
    }

    /**
     * @return The origin of the line containing this segments (y when x = 0)
     */
    public double getLineOrigin() {
        return lineOrigin;
    }

    private SegmentType getSegmentType () { return segmentType; }

    public Double xAt(Double y) {
        // y = ax + b
        // x = (y - b) / a
        switch (segmentType) {
            case SLOPED -> {
                return (y - lineOrigin) / slope;
            }
            case VERTICAL -> {
                if (lowerEndpoint.getY() <= y && y <= upperEndpoint.getY()) {
                    return upperEndpoint.getX();
                } else {
                    throw new RuntimeException(
                            this +" | " + lowerEndpoint.getY() + "<=" + y + "<=" + upperEndpoint.getY()
                    );
                }
            }
            case HORIZONTAL -> {
                if (Comparator.closeEnough(y, upperEndpoint.getY())) {
                    return upperEndpoint.getY();
                } else {
                    throw new RuntimeException(this +" | " + y + "!=" + upperEndpoint.getY());
                }
            }
        }
        throw new RuntimeException("Why here ?");
    }

    public boolean contains(Point p) {
        switch (segmentType) {
            case VERTICAL -> {
                if (!Comparator.closeEnough(p.getX(), upperEndpoint.getX())) {
                    return false;
                }
                return Comparator.sandwiched(lowerEndpoint.getY(), p.getY(), upperEndpoint.getY());
            }
            case HORIZONTAL -> {
                if (!Comparator.closeEnough(p.getY(), upperEndpoint.getY())) {
                    return false;
                }
                return Comparator.sandwiched(lowerEndpoint.getX(), p.getX(), upperEndpoint.getX());
            }
            case SLOPED -> {
                return Comparator.sandwiched(lowerEndpoint.getX(), p.getX(), upperEndpoint.getX()) &&
                        Comparator.sandwiched(lowerEndpoint.getY(), p.getY(), upperEndpoint.getY());
            }
        }
        throw new RuntimeException("Why here ?");
    }

    /**
     * Checks the position of a point relative to this segment.
     * @param point Point to be located
     * @return LEFT if the point is in the half-plane left of this segment's line, RIGHT if it's in the right one
     *         and INTERSECT if it's on the line
     */
    public Position whereIs(Point point) {
        /*
            y < ax + b -> right
            y > ax + b -> left
            y = ax + b -> intersection
        */
        if (segmentType == SegmentType.HORIZONTAL) {
            if (!Comparator.closeEnough(point.getY(), upperEndpoint.getY())) {
                if (point.getY() < upperEndpoint.getY()) {
                    return Position.RIGHT;
                } else {
                    throw new RuntimeException("Think this through: " + this + " | " + point);
                }
            }
            if (point.getX() < upperEndpoint.getX()) {
                return Position.LEFT;
            } else if (lowerEndpoint.getX() < point.getX()){
                return Position.RIGHT;
            } else {
                return Position.INTERSECT;
            }
        }
        if (segmentType == SegmentType.VERTICAL) {
            if (Comparator.closeEnough(point.getX(), upperEndpoint.getX())) {
                return Position.INTERSECT;
            } else if (point.getX() < upperEndpoint.getX()) {
                return Position.LEFT;
            } else {
                return Position.RIGHT;
            }
        }

         final double axPlusB = getSlope() * point.getX() + getLineOrigin();

         if (Comparator.closeEnough(axPlusB, point.getY())) {
             return Position.INTERSECT;
         }
         if (point.getY() > axPlusB) {
             return getSlope() > 0 ? Position.LEFT : Position.RIGHT;
         } else if (point.getY() < axPlusB) {
             return getSlope() > 0 ? Position.RIGHT : Position.LEFT;
         } else {
             throw new RuntimeException(this + " / " + point);
         }
    }

    /**
     * Computes the intersection between two segments
     * @param segment Compared segment
     * @return The intersection point if it exists
     */
    public Optional<Point> getIntersection(Segment segment) {
        /*
            For a segment given by 2 points {(x1, y1), (x2, y2)}
            coefficient a = (y2 - y1) / (x2 - x1)
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

        final double a = slope;
        final double b = lineOrigin;

        final double c = segment.getSlope();
        final double d = segment.getLineOrigin();

        // segments are parallel
        if (a == c || Double.isNaN(a) && Double.isNaN(c)) {
            if(upperEndpoint.sameAs(segment.lowerEndpoint)) {
                return Optional.of(upperEndpoint);
            } else if (lowerEndpoint.sameAs(segment.upperEndpoint)) {
                return Optional.of(lowerEndpoint);
            } else {
                return Optional.empty();
            }
        }

        Point candidate;

        if (segment.getSegmentType() == SegmentType.VERTICAL) { // the other is vertical
            final double xVertical = segment.getUpperEndpoint().getX();
            final double y = a * xVertical + b;

            candidate = new Point(xVertical, y);
        } else if (segmentType == SegmentType.VERTICAL) { // this one is vertical
            final double xVertical = upperEndpoint.getX();
            final double y = c * xVertical + d;

            candidate = new Point(xVertical, y);
        } else {  // General case
            final double u = (d - b) / (a - c);
            final double v = a * u + b;

            candidate = new Point(u, v);
        }

        return this.contains(candidate) && segment.contains(candidate) ? Optional.of(candidate) : Optional.empty();

    }

}

enum SegmentType {
    HORIZONTAL,
    VERTICAL,
    SLOPED
}
