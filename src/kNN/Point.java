package kNN;

/**
 * Created by Zell on 12/13/15.
 */
public class Point {
    double distance;
    double value;

    public Point() {
        this.distance = 0;
        this.value = 0;
    }

    public Point(double distance, double value) {
        this.distance = distance;
        this.value = value;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public double getDistance() {
        return distance;
    }
}
