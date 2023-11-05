package com.example.mhike;

public class HikeDataModel {
    private Integer id;
    private String hikeName;
    private String location;
    private double hikeLength;
    private String hikeDate;
    private boolean parkingAvailable;
    private String equipment;
    private String difficulty;
    private String description;
    private float rating;

    public HikeDataModel() {
    }

    public HikeDataModel(String hikeName, String location, double hikeLength, String hikeDate, boolean parkingAvailable, String equipment, String difficulty, String description, float rating) {
        this.hikeName = hikeName;
        this.location = location;
        this.hikeLength = hikeLength;
        this.hikeDate = hikeDate;
        this.parkingAvailable = parkingAvailable;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.description = description;
        this.rating = rating;
    }

    public HikeDataModel(Integer id, String hikeName, String location, double hikeLength, String hikeDate, boolean parkingAvailable, String equipment, String difficulty, String description, float rating) {
        this.id = id;
        this.hikeName = hikeName;
        this.location = location;
        this.hikeLength = hikeLength;
        this.hikeDate = hikeDate;
        this.parkingAvailable = parkingAvailable;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.description = description;
        this.rating = rating;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHikeName() {
        return hikeName;
    }

    public void setHikeName(String hikeName) {
        this.hikeName = hikeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getHikeLength() {
        return hikeLength;
    }

    public void setHikeLength(double hikeLength) {
        this.hikeLength = hikeLength;
    }

    public String getHikeDate() {
        return hikeDate;
    }

    public void setHikeDate(String hikeDate) {
        this.hikeDate = hikeDate;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean matchesSearchQuery(String query) {
        return hikeName.toLowerCase().contains(query.toLowerCase());
    }
}
