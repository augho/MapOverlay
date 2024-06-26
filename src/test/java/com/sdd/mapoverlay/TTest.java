package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.T;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TTest {
    @Test
    public void insertionTest() {
        T statusStructure = T.getEmpty();

        Point p1 = new Point(15.0, 5.0);
        Point p2 = new Point(5.0, 15.0);
        Point p3 = new Point(5.0, 5.0);
        Point p4 = new Point(15.0, 15.0);

        Segment s1 = Segment.getSegment(p1, p2);
        Segment s2 = Segment.getSegment(p3, p4);
        Segment s3 = Segment.getSegment(p2, p4); // Horizontal
        Segment s4 = Segment.getSegment(p3, p1); // Horizontal

        statusStructure.insert(s1, s1.getUpperEndpoint());
        statusStructure.insert(s2, s2.getUpperEndpoint());

        statusStructure.findRightNeighbour(s2, s2.xAt(12.0)).ifPresent(
                segment -> Assertions.assertTrue(segment.sameAs(s1))
        );
    }

    @Test
    public void otherInsertTest() {
        Segment s2 = new Segment(4.0, 3.0, 2.0, 7.0);
        Segment s4 = new Segment(2.0, 3.0, 4.0, 7.0);
        Segment s1 = new Segment(6.0, 5.0, 1.0, 5.0);

        Point p = new Point(3.0, 5.0);

        T statusStructure = T.getEmpty();

        statusStructure.insert(s1, p);
        statusStructure.insert(s2, p);
        statusStructure.insert(s4, p);
    }
}
