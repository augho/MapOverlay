package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Position;
import com.sdd.mapoverlay.utils.Records.ULCSets;
import com.sdd.mapoverlay.utils.Segment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SegmentTest {
    @Test
    public void horizontalSegmentTest() {
        Point p1 = new Point(5.0, 4.0);
        Point p2 = new Point(1.0, 4.0);

        Segment hSegment = Segment.getSegment(p1, p2);

        Segment intersectL = new Segment(0.0, 4.0, 1.0, 4.0);
        Segment intersectR = new Segment(5.0, 4.0, 6.0, 5.0);
        Segment left = new Segment(0.0, 4.0, 5.0, 10.0);
        Segment right = new Segment(10.0, 0.0, 10.0, 10.0);


        assertTrue(hSegment.getIntersection(intersectL).get().sameAs(intersectL.getLowerEndpoint()));
        assertTrue(hSegment.getIntersection(intersectR).get().sameAs(intersectR.getLowerEndpoint()));

        assertTrue(hSegment.getIntersection(left).isEmpty());
        assertTrue(hSegment.getIntersection(right).isEmpty());
    }
    @Test
    public void verticalSegmentTest() {
        Segment vSegment = new Segment(0.0, 0.0, 0.0, 5.0);

        Segment intersectH = new Segment(-1.0, 3.0, 3.0, 3.0);
        Segment intersectUp = new Segment(0.0, 5.0, 0.0, 7.0);
        Segment noContact = new Segment(1.0, 0.0, 2.0, 1.0);
        Segment noContactBis = new Segment(0.5, 2.0, 3.0, 2.0);
        Segment parallel = new Segment(1.0, 0.0, 1.0, 5.0);

        assertTrue(vSegment.getIntersection(intersectH).get().sameAs(new Point(0.0, 3.0)));
        assertTrue(vSegment.getIntersection(intersectUp).get().sameAs(vSegment.getUpperEndpoint()));

        assertTrue(vSegment.getIntersection(noContact).isEmpty());
        assertTrue(vSegment.getIntersection(noContactBis).isEmpty());
        assertTrue(vSegment.getIntersection(parallel).isEmpty());
    }

    @Test
    public void otherTest() {
        Segment s52 = new Segment(177.38, 81.75, 52.5, 184.44);
        Segment s95 = new Segment(179.55, 64.92, 95.18, 180.06);
        Segment s129 = new Segment(104.08, 127.37, 129.56, 231.4);
        Segment s120 = new Segment(64.75, 157.36, 120.14, 163.61);
        Segment s95Big = new Segment(149.19, 95.63, 95.91, 226.48);
        Segment s164 = new Segment(98.62, 173.4, 164.53, 225.36);

        Segment s171 = new Segment(63.41, 219.94, 171.39, 225.31);
        Segment s134 = new Segment(40.29, 168.95, 134.05, 223.33);
        Segment s68 = new Segment(124.18, 98.87, 68.64, 227.76);

        Segment s142 = new Segment(67.74, 87.24, 142.76, 200.0);
        Segment s75 = new Segment(122.97, 95.32, 75.91, 199.52);

        Point p111 = new Point(111.52268832789706, 157.7570826825404);
        Point p112 = new Point(112.75205970860071, 162.77637250728935);
        Point p64 = new Point(64.75, 157.36);

        System.out.println(s142.getIntersection(s68));
    }
}
