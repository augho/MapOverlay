package com.sdd.mapoverlay;


import com.sdd.mapoverlay.utils.Records.Intersection;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.SegmentCollection;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void testOverlay() throws FileNotFoundException {
        ArrayList<Segment> segments = readSegmentFile("base_case.txt");
        SegmentCollection segmentCollection = new SegmentCollection(segments);
        ArrayList<Intersection> res = segmentCollection.findIntersections();
        System.out.println("RESULTS:");
        System.out.println("Count = " + res.size());
        res.forEach(System.out::println);
    }


}
