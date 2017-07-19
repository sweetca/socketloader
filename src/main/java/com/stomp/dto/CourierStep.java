package com.stomp.dto;

import com.stomp.model.TravelMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourierStep implements Serializable {
    private long id;
    private double latitude;
    private double longitude;
    private long courier;
    private long company;
    private TravelMode travelMode = TravelMode.DRIVING;
    private double speed = 0.0;
    private int distance = 0;
    private long time = new Date().getTime();


    public CourierStep() {
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public long getCourier() {
        return courier;
    }
    public void setCourier(long courier) {
        this.courier = courier;
    }
    public long getCompany() {
        return company;
    }
    public void setCompany(long company) {
        this.company = company;
    }
    public TravelMode getTravelMode() {
        return travelMode;
    }
    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }
    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"id\":null," )
                .append("\"latitude\":" + this.latitude)
                .append(",\"longitude\":" + this.longitude)
                .append(",\"created\":null,\"courier\":null,\"companyId\":null,")
                .append("\"travelMode\":\"DRIVING\",")
                .append("\"speed\":0,\"distance\":0,\"time\":" + new Date().getTime())
                .append("}");

        return builder.toString();
    }
}
