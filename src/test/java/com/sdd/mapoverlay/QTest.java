package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.EventPoint;
import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Q;
import com.sdd.mapoverlay.utils.Segment;
import org.junit.jupiter.api.Test;

public class QTest {
    @Test
    public void insertTest() {
        Q eventQueue = Q.getEmptyQueue();

        Point p1 = new Point(15.0, 5.0);
        Point p2 = new Point(5.0, 15.0);
        Point p3 = new Point(5.0, 5.0);
        Point p4 = new Point(15.0, 15.0);

        Segment s1 = Segment.getSegment(p1, p2);
        Segment s2 = Segment.getSegment(p3, p4);
        eventQueue.insert(new EventPoint(s1, s1.getUpperEndpoint()));
        eventQueue.insert(new EventPoint(s1, s1.getUpperEndpoint()));

        eventQueue.insert(new EventPoint(s2, s2.getUpperEndpoint()));

        eventQueue.insert(new EventPoint(s1, s1.getLowerEndpoint()));

        eventQueue.insert(new EventPoint(s2, s2.getLowerEndpoint()));
        eventQueue.printTree();
        eventQueue.popNextEvent();
        eventQueue.printTree();
    }
}
