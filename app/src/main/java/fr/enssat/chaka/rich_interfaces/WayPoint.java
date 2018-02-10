package fr.enssat.chaka.rich_interfaces;

/**
 * Created by chaka on 18/01/18.
 */

public class WayPoint {
    private Float lat;
    private Float lng;
    private String label;
    private int timestamp;

    public WayPoint(Float lat, Float lng, String label, int timestamp){
        this.lat = lat;
        this.lng = lng;
        this.label = label;
        this.timestamp = timestamp;
    }

    public WayPoint(){}

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
