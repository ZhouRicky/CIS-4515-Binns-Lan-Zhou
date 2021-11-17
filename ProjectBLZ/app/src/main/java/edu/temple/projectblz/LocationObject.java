package edu.temple.projectblz;

public class LocationObject {
    private double park_lat;
    private double park_lon;
    private int park_id;
    private int driver_id;
    private String createdAt;

    public LocationObject(double park_lat, double park_lon, int park_id, int driver_id, String createdAt){
        this.park_lat = park_lat;
        this.park_lon = park_lon;
        this.park_id = park_id;
        this.driver_id = driver_id;
        this.createdAt = createdAt;
    }

    public double getPark_lat() {
        return park_lat;
    }

    public double getPark_lon() {
        return park_lon;
    }

    public int getPark_id() {
        return park_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
