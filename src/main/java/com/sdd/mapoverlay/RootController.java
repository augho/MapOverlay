package com.sdd.mapoverlay;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class RootController {
    public LineChart<Number, Number> lineChart;

    public void addSegment(Float x1, Float y1, Float x2, Float y2) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        System.out.printf(
                "Adding segment:(%.2f, %.2f) (%.2f, %.2f)%n",
                x1, y1, x2, y2
        );
        series.getData().add(new XYChart.Data<>(x1, y1));
        series.getData().add(new XYChart.Data<>(x2, y2));
        lineChart.getData().add(series);
    }

    private void clearLineChart() {
        this.lineChart.getData().clear();
    }

    public void displayFileContent(List<List<Float>> fileContent) {
        this.clearLineChart();
        fileContent.forEach(segment -> {
            this.addSegment(
                    segment.get(0), segment.get(1), segment.get(2), segment.get(3)
            );
        });
    }
}
