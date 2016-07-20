package com.example.sec.whereami;

/**
 * Created by songjonghun on 2016. 7. 18..
 */
public class CpsManager {
    float azimuth = 0;
    float pitch = 0;
    float roll = 0;
    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    public void setRoll(float roll) {
        this.roll = roll;
    }
    public float getAzimuth() {
        return azimuth;
    }
    public CpsManager() {}
}
