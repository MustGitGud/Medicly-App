package com.example.medicly;

import com.google.firebase.firestore.Exclude;

// class to hold allergy data
public class Allergy {

    @Exclude private String id; // prevent from being stored as a field in Firestore
    private String title;
    private String description;
    private String owner;

    public Allergy() {
    }

    public Allergy(String title, String description, String owner) {
        this.title = title;
        this.description = description;
        this.owner = owner;
    }

    public Allergy(String id, String title, String description, String owner) {
        this.id = id;
        this.title = title;
        this.description = description;
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
