package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

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
        Segment s3 = Segment.getSegment(p2, p4); // Horizontal
        Segment s4 = Segment.getSegment(p3, p1); // Horizontal

        eventQueue.insert(new EventPoint(s1, s1.getUpperEndpoint()));
        eventQueue.insert(new EventPoint(s1, s1.getUpperEndpoint()));

        eventQueue.insert(new EventPoint(s2, s2.getUpperEndpoint()));

        eventQueue.insert(new EventPoint(s1, s1.getLowerEndpoint()));

        eventQueue.printTree();
        eventQueue.insert(new EventPoint(s2, s2.getLowerEndpoint()));
        eventQueue.printTree();


//        eventQueue.insert(new EventPoint(s3, s3.getUpperEndpoint()));
//        eventQueue.insert(new EventPoint(s3, s3.getLowerEndpoint()));
//
//        eventQueue.insert(new EventPoint(s4, s4.getLowerEndpoint()));
//        eventQueue.insert(new EventPoint(s4, s4.getUpperEndpoint()));

        ArrayList<EventPoint> events = new ArrayList<>();
        do {
//            eventQueue.printTree();
            events.add(eventQueue.popNextEvent());
        } while (!eventQueue.isEmpty());

        events.forEach(System.out::println);

        // TODO find the source of null elements
        events = (ArrayList<EventPoint>) events.stream().filter(Objects::nonNull).collect(Collectors.toList());

//        Assertions.assertEquals(4, events.size());

        for (int i = 1; i < events.size(); i++) {
            Assertions.assertEquals(Position.LEFT, events.get(i - 1).compare(events.get(i)));
        }
    }
}
