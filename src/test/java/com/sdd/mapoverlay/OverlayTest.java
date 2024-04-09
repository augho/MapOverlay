package com.sdd.mapoverlay;


import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Records.Intersection;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.SegmentCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OverlayTest {
    public ArrayList<Segment> readSegmentFile(String filepath) throws FileNotFoundException {
        ArrayList<String> fileContent = new ArrayList<>();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.add(line.strip());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new FileNotFoundException("File not found");
        }
        List<Segment> segments;
        try {
            segments = fileContent
                    .stream()
                    .map(Segment::fromString)
                    .toList();

        } catch (NumberFormatException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new ArrayList<>(segments);
    }

    private ArrayList<Intersection> computeOverlay(String filename) throws FileNotFoundException {
        ArrayList<Segment> segments = readSegmentFile(filename);
        SegmentCollection segmentCollection = new SegmentCollection(segments);
        return segmentCollection.findIntersections();
    }

    @Test
    public void testOverlay() throws FileNotFoundException {
        ArrayList<Intersection> res = computeOverlay("edgecaseParall.txt");

        System.out.println("RESULTS:");
        System.out.println("Count = " + res.size());
        res.forEach(System.out::println);
    }

    @Test
    public void testBaseCase() throws FileNotFoundException {
        ArrayList<Intersection> baseCaseRes = computeOverlay("base_case.txt");
        assertEquals(1, baseCaseRes.size());
        Intersection intersection = baseCaseRes.get(0);
        assertEquals(3, intersection.segments().size());
        assertTrue(intersection.p().sameAs(new Point(10.0, 10.0)));
    }
    @Test
    public void testEdge1() throws FileNotFoundException {
        ArrayList<Intersection> res = computeOverlay("edgecase1.txt");
        assertTrue(res.isEmpty());
    }

    @Test
    public void testEdge11() throws FileNotFoundException {
        ArrayList<Intersection> res = computeOverlay("edgecase11.txt");
        assertEquals(1, res.size());
        assertEquals(2, res.get(0).segments().size());
        assertTrue(res.get(0).p().sameAs(new Point(2.0, 4.0)));
    }

    @Test
    public void testEdge12() throws FileNotFoundException {
        ArrayList<Intersection> res = computeOverlay("edgecase12.txt");
        assertEquals(1, res.size());
        assertEquals(2, res.get(0).segments().size());
        assertTrue(res.get(0).p().sameAs(new Point(5.0, 4.0)));
    }
    @Test
    public void testEdge3inter() throws FileNotFoundException {
        ArrayList<Intersection> res = computeOverlay("edgecase3inter.txt");
        assertEquals(1, res.size());
        assertEquals(3, res.get(0).segments().size());
        assertTrue(res.get(0).p().sameAs(new Point(3.0, 5.0)));
    }
    @Test
    public void testEdge2() throws FileNotFoundException {
        ArrayList<Intersection> res = computeOverlay("edgecase2.txt");
        assertEquals(2, res.size());
        assertEquals(2, res.get(0).segments().size());
        assertEquals(2, res.get(1).segments().size());
    }
}
