package com.example.medicly;

import com.google.firebase.firestore.Exclude;

public class DiagnosisData {
    @Exclude private String id;
    private String title;
    private String description;
    private String date;
    private String owner;

    public DiagnosisData() {
    }

    public DiagnosisData(String title, String description, String date, String owner) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.owner = owner;
    }

    public DiagnosisData(String id, String title, String description, String date, String owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
