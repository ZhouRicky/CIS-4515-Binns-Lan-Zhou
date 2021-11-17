package edu.temple.projectblz;

//An enum of Position could be use for saving parking location and current location.

public class Position {

    String id;
    double latitude, longitude;

    public Position(String id, double latitude, double longitude){
        setId(id);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }
}
