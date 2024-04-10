package com.sdd.mapoverlay;

import com.sdd.mapoverlay.utils.Point;
import com.sdd.mapoverlay.utils.Segment;
import com.sdd.mapoverlay.utils.Records.Intersection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RootController implements Initializable{
       
    @FXML
    public LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private double lastX;
    private double lastY;

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
        System.out.println(series.getData());
        lineChart.getData().add(series);
        for (XYChart.Data<Number, Number> data : series.getData()) {
            System.out.println("Test");
            Node point = data.getNode();
            point.setStyle("-fx-background-color: #0077B6;");
            point.setScaleX(0.75);
            point.setScaleY(0.75);
            System.out.println(point);
        }
    }

    protected void addPoint(Point p){
        this.addPoint(p.getX(), p.getY());
    }

    protected void addIntersectionPoints(ArrayList<Intersection> intersectionPoints) {
        // Create a new series
        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        for (Intersection intersection : intersectionPoints) {
            serie.getData().add(new XYChart.Data<>(intersection.p().getX(), intersection.p().getY()));
            System.out.println("Adding intersection point: " + intersection.p().getX() + ", " + intersection.p().getY());
        }
        System.out.println(serie.getData());

        // Add the series to your chart
        lineChart.getData().add(serie);

        for (XYChart.Data<Number, Number> data : serie.getData()) {
            System.out.println(data);
            Node point = data.getNode();
            if (point == null) {
                System.out.println("Point is null");
            } else {
                System.out.println("Point is not null");
                point.setStyle("-fx-background-color: #0077B6;");
                point.setScaleX(0.75);
                point.setScaleY(0.75);
            }
        }
    }

    public void removeSegment(Segment segment) {
        List<XYChart.Series<Number, Number>> toRemove = new ArrayList<>();
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            if (series.getData().size() == 2) {
                if (series.getData().get(0).getXValue().equals(segment.getLowerEndpoint().getX()) &&
                        series.getData().get(0).getYValue().equals(segment.getLowerEndpoint().getY()) &&
                        series.getData().get(1).getXValue().equals(segment.getUpperEndpoint().getX()) &&
                        series.getData().get(1).getYValue().equals(segment.getUpperEndpoint().getY())) {
                    toRemove.add(series);
                }
            }
        }
        lineChart.getData().removeAll(toRemove);
    }

    private void clearLineChart() {
        this.lineChart.getData().clear();
    }

    public void highlightSegment(Segment segment) {
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            if (series.getData().get(0).getXValue().equals(segment.getLowerEndpoint().getX()) &&
                    series.getData().get(0).getYValue().equals(segment.getLowerEndpoint().getY()) &&
                    series.getData().get(1).getXValue().equals(segment.getUpperEndpoint().getX()) &&
                    series.getData().get(1).getYValue().equals(segment.getUpperEndpoint().getY())) {
                Node line = series.getNode().lookup(".chart-series-line");
                line.setStyle("-fx-stroke-width: 2px;");
                line.setStyle("-fx-stroke: #FFA500;");
                line.toFront();
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Node point = data.getNode();
                    point.setStyle("-fx-background-color: #FFA500;");
                    point.setScaleX(0.75);
                    point.setScaleY(0.75);
                    point.toFront();
                }
            } else {
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
        }
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

    private void handleScrollEvent(ScrollEvent event) {
        double zoomFactor = event.getDeltaY() > 0 ? 0.75 : 1.25;
        
        // Get the mouse cursor's position relative to the LineChart
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Zoom in or out
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        double xCenter = xAxis.getValueForDisplay(mouseX).doubleValue();
        double yCenter = yAxis.getValueForDisplay(mouseY).doubleValue();
        double newLowerX = xCenter - (xCenter - xAxis.getLowerBound()) * zoomFactor;
        double newUpperX = xCenter + (xAxis.getUpperBound() - xCenter) * zoomFactor;
        double newLowerY = yCenter - (yCenter - yAxis.getLowerBound()) * zoomFactor;
        double newUpperY = yCenter + (yAxis.getUpperBound() - yCenter) * zoomFactor;

        xAxis.setLowerBound(newLowerX);
        xAxis.setUpperBound(newUpperX);
        yAxis.setLowerBound(newLowerY);
        yAxis.setUpperBound(newUpperY);
    }

    private void handleMousePressed(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
    }

    private void handleMouseDragged(MouseEvent event) {
        double xAxisVisibleRange = xAxis.getUpperBound() - xAxis.getLowerBound();
        double yAxisVisibleRange = yAxis.getUpperBound() - yAxis.getLowerBound();

        double deltaX = (lastX - event.getX()) * (xAxisVisibleRange / lineChart.getWidth());
        double deltaY = (lastY - event.getY()) * (yAxisVisibleRange / lineChart.getHeight());

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(xAxis.getLowerBound() + deltaX);
        xAxis.setUpperBound(xAxis.getUpperBound() + deltaX);
        yAxis.setLowerBound(yAxis.getLowerBound() - deltaY);
        yAxis.setUpperBound(yAxis.getUpperBound() - deltaY);

        lastX = event.getX();
        lastY = event.getY();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lineChart.setOnScroll(this::handleScrollEvent);
        lineChart.setOnMousePressed(this::handleMousePressed);
        lineChart.setOnMouseDragged(this::handleMouseDragged);
    }
}
