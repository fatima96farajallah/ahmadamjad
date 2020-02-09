package com.example.oxygenapplication;

public class Swimmer {

    double oxygen;
    double heartRate;

    public Swimmer(double oxygen, double heartRate) {
        this.oxygen = oxygen;
        this.heartRate = heartRate;
    }

    public Swimmer() {
    }

    public double getOxygen() {
        return oxygen;
    }

    public void setOxygen(double oxygen) {
        this.oxygen = oxygen;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }


}
