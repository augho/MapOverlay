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
    private XYChart.Series<Number, Number> sweepLine = new XYChart.Series<>();
    protected double lowerBoundX = 0.00;
    protected double upperBoundX = 100.00;


    public void addSegment(Double x1, Double y1, Double x2, Double y2) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        System.out.printf(
                "Adding segment:(%.2f, %.2f) (%.2f, %.2f)%n",
                x1, y1, x2, y2
        );
        series.getData().add(new XYChart.Data<>(x1, y1));
        series.getData().add(new XYChart.Data<>(x2, y2));
        lineChart.getData().add(series);

        upperBoundX = Math.max(upperBoundX, x1);
        upperBoundX = Math.max(upperBoundX, x2);

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
            Node point = data.getNode();
            point.setStyle("-fx-background-color: #0077B6;");
            point.setScaleX(0.75);
            point.setScaleY(0.75);
        }
    }

    protected void addPoint(Point p){
        this.addPoint(p.getX(), p.getY());
    }

    protected void addIntersectionPoints(ArrayList<Intersection> intersectionPoints) {
        // Create a new series
        XYChart.Series<Number, Number> serie = new XYChart.Series<>();

        // Delete the previous series if it exists
        XYChart.Series<Number, Number> previousSerie = this.getSerieByName("Intersection_Points");
        if (previousSerie != null) {
            lineChart.getData().remove(previousSerie);
        }

        // Set the name of the serie
        serie.setName("Intersection_Points");

        // Add the serie to your chart
        lineChart.getData().add(serie);

        for (Intersection intersection : intersectionPoints) {
            serie.getData().add(new XYChart.Data<>(intersection.p().getX(), intersection.p().getY()));
            System.out.println("Adding intersection point: " + intersection.p().getX() + ", " + intersection.p().getY());

        }

        // Change the color of the points to be more visible to the user
        for (XYChart.Data<Number, Number> data : serie.getData()) {
            Node point = data.getNode();
            if (point == null) {
            } else {
                point.setStyle("-fx-background-color: #B63F00;");
                point.setScaleX(1.15);
                point.setScaleY(1.15);
            }
            Node line = serie.getNode().lookup(".chart-series-line");
            line.setStyle("-fx-stroke: #FF000000;");
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

    public void highlightIntersectionPoint(Point intersectionPoint) {
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            if (series.getName() != null && series.getName().equals("Intersection_Points")) {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    if (data.getXValue().equals(intersectionPoint.getX()) && data.getYValue().equals(intersectionPoint.getY())) {
                        // we clear the sweep line
                        sweepLine.getData().clear();
                        // we add two points to form a sweep line. One point is at the lower bound of the x axis and the other is at the upper bound of the x axis
                        sweepLine.getData().add(new XYChart.Data<>(lowerBoundX, intersectionPoint.getY()));
                        sweepLine.getData().add(new XYChart.Data<>(upperBoundX*1.25, intersectionPoint.getY()));
                        sweepLine.getData().add(new XYChart.Data<>(intersectionPoint.getX(), intersectionPoint.getY()));
                        // we change the color of the sweep line line to be more visible
                        Node line = sweepLine.getNode().lookup(".chart-series-line");
                        line.setStyle("-fx-stroke: #FF0000;");
                        line.toFront();
                        for (XYChart.Data<Number, Number> sweepData : sweepLine.getData()) {
                            Node sweepPoint = sweepData.getNode();
                            sweepPoint.setStyle("-fx-background-color: #FF000000;");
                            sweepPoint.setScaleX(0.75);
                            sweepPoint.setScaleY(0.75);
                        }

                        Node point = data.getNode();
                        point.setStyle("-fx-background-color: #FFA500;");
                        point.setScaleX(1.5);
                        point.setScaleY(1.5);
                        point.toFront();
                    } else {
                        Node point = data.getNode();
                        point.setStyle("-fx-background-color: #B63F00;");
                        point.setScaleX(1.15);
                        point.setScaleY(1.15);
                    }
                }
            }
        }
    }

    public void addSweepLine(){
        lineChart.getData().add(sweepLine);
    }

    public void displayFileContent(List<Segment> fileContent) {
        this.clearLineChart();
        fileContent.forEach(this::addSegment);
    }

    public List<Object> getChartContent() {
        List<Segment> segments = new ArrayList<>();
        List<Point> intersections = new ArrayList<>();
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            if (series.getData().size() == 2) {
                segments.add(new Segment(
                        (Double) series.getData().get(0).getXValue(),
                        (Double) series.getData().get(0).getYValue(),
                        (Double) series.getData().get(1).getXValue(),
                        (Double) series.getData().get(1).getYValue()
                ));
            } else {
                for (int i=0; i<series.getData().size(); i++) {
                    intersections.add(new Point(
                            (Double) series.getData().get(i).getXValue(),
                            (Double) series.getData().get(i).getYValue()
                    ));
                }
            }
        }
        List<Object> result = new ArrayList<>();
        result.add(segments);
        result.add(intersections);
        return result;
    }

    public List<Segment> getSegmentsFromChart() {
        return lineChart.getData().stream()
                .filter(series -> series.getData().size() == 2)
                .map(series -> new Segment(
                        series.getData().get(0).getXValue().doubleValue(),
                        series.getData().get(0).getYValue().doubleValue(),
                        series.getData().get(1).getXValue().doubleValue(),
                        series.getData().get(1).getYValue().doubleValue()
                ))
                .collect(Collectors.toList());
    }

    public XYChart.Series<Number, Number> getSerieByName(String serieName) {
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            if (series.getName() != null && series.getName().equals(serieName)) {
                return series;
            }
        }
        return null; // Si la série n'est pas trouvée
    }

    public List<Point> getIntersectionPointsFromChart() {
        List<Point> intersectionPoints = new ArrayList<>();
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            // Bad practice to use magic numbers but we assume that the intersection points are the only series with more than 2 points
            if (series.getData().size() !=2) {
                for (int i=0; i<series.getData().size(); i++) {
                    intersectionPoints.add(new Point(
                            series.getData().get(i).getXValue().doubleValue(),
                            series.getData().get(i).getYValue().doubleValue()
                    ));
                }
            }
        }
        System.out.println("Intersection points: " + intersectionPoints);
        return intersectionPoints;
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
