package com.gjermundbjaanes.beaconmqtt.beacondb;

public class BeaconResult {

    private String uuid;
    private String major;
    private String minor;
    private String informalName;

    public BeaconResult(String uuid, String major, String minor, String informalName) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.informalName = informalName;
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

    public String getInformalName() {
        return informalName;
    }
}
