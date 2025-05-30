package se.su.inlupp;

public class Location {
    private final String name;
    private final double x, y;

    public Location(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }


    public String getName() {
        return name;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public String toString() {
        return name + " (" + x + ", " + y + ")";
    }
}
