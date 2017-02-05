package com.gjermundbjaanes.beaconmqtt.db.beacon;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BeaconResult) {
            BeaconResult other = (BeaconResult) obj;
            return this.uuid.equals(other.getUuid()) && this.major.equals(other.getMajor()) && this.minor.equals(other.getMinor());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (41 * (41 + (uuid + major + minor).hashCode()));
    }
}
