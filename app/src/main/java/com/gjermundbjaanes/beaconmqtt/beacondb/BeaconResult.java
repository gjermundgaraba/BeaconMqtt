package com.gjermundbjaanes.beaconmqtt.beacondb;

public class BeaconResult {

    private String uuid;
    private String major;
    private String minor;

    public BeaconResult(String uuid, String major, String minor) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }
}
