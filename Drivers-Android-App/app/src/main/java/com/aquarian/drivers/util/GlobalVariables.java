package com.aquarian.drivers.util;

import android.app.Application;

public class GlobalVariables extends Application {

    private String driverID;
    private String driverFirstname;
    private String driverLastConnection;
    private String vehicleID;
    private String weather;
    private int numberOfReceipts;
    private double Latitude;
    private double Longitude;

    public double getLatitude() { return Latitude; }

    public void setLatitude(double latitude) { Latitude = latitude; }

    public double getLongitude() { return Longitude; }

    public void setLongitude(double longitude) { Longitude = longitude; }

    public int getNumberOfReceipts() { return numberOfReceipts; }

    public void setNumberOfReceipts(int numberOfReceipts) { this.numberOfReceipts = numberOfReceipts; }

    public String getWeather() { return weather; }

    public void setWeather(String weather) { this.weather = weather; }

    public String getVehicleID() { return vehicleID; }

    public void setVehicleID(String vehicleID) { this.vehicleID = vehicleID; }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getDriverFirstname() {
        return driverFirstname;
    }

    public void setDriverFirstname(String driverFirstname) { this.driverFirstname = driverFirstname; }

    public String getDriverLastConnection() { return driverLastConnection; }

    public void setDriverLastConnection(String driverLastConnection) { this.driverLastConnection = driverLastConnection; }
}
