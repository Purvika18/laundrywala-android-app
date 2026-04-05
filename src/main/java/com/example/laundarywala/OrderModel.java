package com.example.laundarywala;

import java.util.List;

public class OrderModel {
    private String serviceType;
    private String pickupDate;
    private String pickupTime;
    private String address;
    private List<String> photoURLs;
    private String userId;

    // Empty constructor for Firestore
    public OrderModel() {
    }

    public OrderModel(String serviceType, String pickupDate, String pickupTime, String address, List<String> photoURLs, String userId) {
        this.serviceType = serviceType;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.address = address;
        this.photoURLs = photoURLs;
        this.userId = userId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getPhotoURLs() {
        return photoURLs;
    }

    public String getUserId() {
        return userId;
    }
}
