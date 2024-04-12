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
        Point p = new Point(106.91368780696644,138.93940904861532);
        Segment s1 = new Segment(124.18, 98.87, 68.64, 227.76);
        Segment s2 = new Segment(104.08, 127.37, 129.56, 231.4);

        assertEquals(Position.INTERSECT, s1.whereIs(p));
        assertEquals(Position.INTERSECT, s2.whereIs(p));

        Segment s52 = new Segment(177.38, 81.75, 52.5, 184.44);
        Segment s95 = new Segment(179.55, 64.92, 95.18, 180.06);
        Segment s129 = new Segment(104.08, 127.37, 129.56, 231.4);
        Segment s120 = new Segment(64.75, 157.36, 120.14, 163.61);

        Point p111 = new Point(111.52268832789706, 157.7570826825404);
        Point p112 = new Point(112.75205970860071, 162.77637250728935);
        Point p64 = new Point(64.75, 157.36);

//        System.out.println(s129.getIntersection(s120));
//        System.out.println(s95.getIntersection(s120));
//        System.out.println(s52.getIntersection(s120));
//        System.out.println(s95.getIntersection(s129));
//
//        ULCSets ulcSets = ULCSets.getEmpty();
//        ulcSets.C().add(s120);
//        ulcSets.C().add(s129);
//        System.out.println(ulcSets.getEdgeSegmentsOfUC(p112.getY(), p112));;
//        System.out.println(s52.whereIs(p64));
    }
}
