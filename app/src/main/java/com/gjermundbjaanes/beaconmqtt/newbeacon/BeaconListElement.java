package com.gjermundbjaanes.beaconmqtt.newbeacon;

import org.altbeacon.beacon.Beacon;

public class BeaconListElement {

    private boolean saved = false;
    private Beacon beacon;

    public BeaconListElement(Beacon beacon) {
        this.beacon = beacon;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getUuid() {
        return beacon.getId1().toString();
    }

    public String getMajor() {
        return beacon.getId2().toString();
    }

    public String getMinor() {
        return beacon.getId3().toString();
    }

    public Beacon getBeacon() {
        return beacon;
    }
}
