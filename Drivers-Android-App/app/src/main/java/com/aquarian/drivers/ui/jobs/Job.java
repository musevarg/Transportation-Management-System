package com.aquarian.drivers.ui.jobs;

public class Job
{
    private String id;
    private String status;
    private int customerId;
    private int pickupId;
    private int dropoffId;
    private int vehicleId;
    private String parcelType;
    private String parcelSize;
    private String parcelWeight;
    private String dateCreated;
    private String dateDue;
    private String dateDelivered;
    private String trackingId;
    private String distanceTravelled;
    private String pic1;
    private String pic2;
    private String comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPickupId() {
        return pickupId;
    }

    public void setPickupId(int pickupId) {
        this.pickupId = pickupId;
    }

    public int getDropoffId() {
        return dropoffId;
    }

    public void setDropoffId(int dropoffId) {
        this.dropoffId = dropoffId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getParcelType() {
        return parcelType;
    }

    public void setParcelType(String parcelType) {
        this.parcelType = parcelType;
    }

    public String getParcelSize() {
        return parcelSize;
    }

    public void setParcelSize(String parcelSize) {
        this.parcelSize = parcelSize;
    }

    public String getParcelWeight() {
        return parcelWeight;
    }

    public void setParcelWeight(String parcelWeight) {
        this.parcelWeight = parcelWeight;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
    }

    public String getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(String dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public String geTtrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(String distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public Job(String id, String status, int customerId, int pickupId, int dropoffId, int vehicleId, String parcelType, String parcelSize, String parcelWeight, String dateCreated, String dateDue, String dateDelivered, String trackingId, String distanceTravelled, String pic1, String pic2, String comments) {
        this.id = id;
        this.status = status;
        this.customerId = customerId;
        this.pickupId = pickupId;
        this.dropoffId = dropoffId;
        this.vehicleId = vehicleId;
        this.parcelType = parcelType;
        this.parcelSize = parcelSize;
        this.parcelWeight = parcelWeight;
        this.dateCreated = dateCreated;
        this.dateDue = dateDue;
        this.dateDelivered = dateDelivered;
        this.trackingId = trackingId;
        this.distanceTravelled = distanceTravelled;
        this.pic1 = pic1;
        this.pic2 = pic2;
        this.comments = comments;
    }

    public Job(String id, String status, String parcelType, String parcelSize, String parcelWeight, String trackingId) {
        this.id = id;
        this.status = status;
        this.parcelType = parcelType;
        this.parcelSize = parcelSize;
        this.parcelWeight = parcelWeight;
        this.trackingId = trackingId;
    }
}
