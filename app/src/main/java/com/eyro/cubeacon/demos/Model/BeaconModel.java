package com.eyro.cubeacon.demos.Model;

public class BeaconModel {

    private String UUID = "";
    private String major = "";
    private String minor = "";
    private String namaBeacon = "";
    private String range = "";

    public BeaconModel(){

    }

    public BeaconModel(String nama, String id, String Major, String Minor){
        this.namaBeacon = nama;
        this.UUID = id;
        this.major = Major;
        this.minor = Minor;
    }

    public String getUUID() {
        return UUID;
    }
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMajor() {
        return major;
    }
    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }
    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getNamaBeacon() {
        return namaBeacon;
    }
    public void setNamaBeacon(String namaBeacon) {
        this.namaBeacon = namaBeacon;
    }

    public String getRange() {
        return range;
    }
    public void setRange(String range) {
        this.range = range;
    }
}
