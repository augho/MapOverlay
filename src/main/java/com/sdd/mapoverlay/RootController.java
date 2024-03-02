package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.Segment;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RootController {
    public LineChart<Number, Number> lineChart;

    public void addSegment(Double x1, Double y1, Double x2, Double y2) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        System.out.printf(
                "Adding segment:(%.2f, %.2f) (%.2f, %.2f)%n",
                x1, y1, x2, y2
        );
        series.getData().add(new XYChart.Data<>(x1, y1));
        series.getData().add(new XYChart.Data<>(x2, y2));
        lineChart.getData().add(series);

        // Change the color of the 2 points and segment added to black
        for (XYChart.Data<Number, Number> data : series.getData()) {
            Node point = data.getNode();
            point.setStyle("-fx-background-color: #0077B6;");
            point.setScaleX(0.75);
            point.setScaleY(0.75);
        }
        Node line = series.getNode().lookup(".chart-series-line");
        line.setStyle("-fx-stroke-width: 2px;");
        line.setStyle("-fx-stroke: #90E0EF;");
    }
    public void addSegment(Segment segment) {
        this.addSegment(
                segment.getLowerEndpoint().getX(),
                segment.getLowerEndpoint().getY(),
                segment.getUpperEndpoint().getX(),
                segment.getUpperEndpoint().getY());
    }

    protected void addPoint(Double x, Double y) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(x, y));
        lineChart.getData().add(series);
    }

    private void clearLineChart() {
        this.lineChart.getData().clear();
    }

    public void displayFileContent(List<Segment> fileContent) {
        this.clearLineChart();
        fileContent.forEach(this::addSegment);
    }

    public List<Segment> getChartContent() {
        return this.lineChart.getData()
                .stream()
                .map(Segment::fromSeries)
                .toList();
    }
}
