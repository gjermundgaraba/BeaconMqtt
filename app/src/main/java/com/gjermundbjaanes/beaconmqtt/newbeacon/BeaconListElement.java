package com.gjermundbjaanes.beaconmqtt.newbeacon;

import org.altbeacon.beacon.Beacon;

public class BeaconListElement {

    private boolean saved = false;
    private Beacon beacon;

    BeaconListElement(Beacon beacon) {
        this.beacon = beacon;
    }

    boolean isSaved() {
        return saved;
    }

    void setSaved(boolean saved) {
        this.saved = saved;
    }

    String getUuid() {
        return beacon.getId1().toString();
    }

    String getMajor() {
        return beacon.getId2().toString();
    }

    String getMinor() {
        return beacon.getId3().toString();
    }

    public Beacon getBeacon() {
        return beacon;
    }
}
