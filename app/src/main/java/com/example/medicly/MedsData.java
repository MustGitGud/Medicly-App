package com.example.medicly;

import com.google.firebase.firestore.Exclude;

public class MedsData {

    @Exclude private String id;
    private String medicine;
    private String intake;
    private String owner;

    public MedsData() {
    }

    public MedsData(String medicine, String intake, String owner) {
        this.medicine = medicine;
        this.intake = intake;
        this.owner = owner;
    }

    public MedsData(String id, String medicine, String intake, String owner) {
        this.id = id;
        this.medicine = medicine;
        this.intake = intake;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
