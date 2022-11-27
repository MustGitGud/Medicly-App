package com.example.medicly;

import com.google.firebase.firestore.Exclude;

public class SchedData {

    @Exclude
    private String id;
    private String place;
    private String date;
    private String owner;

    public SchedData() {
    }

    public SchedData(String place, String date, String owner) {
        this.place = place;
        this.date = date;
        this.owner = owner;
    }

    public SchedData(String id, String place, String date, String owner) {
        this.id = id;
        this.place = place;
        this.date = date;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
