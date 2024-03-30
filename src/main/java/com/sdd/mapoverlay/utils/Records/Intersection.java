package com.sdd.mapoverlay.utils.Records;

import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Segment;

import java.util.ArrayList;

public record Intersection(Point p, ArrayList<Segment> segments) {

}
