package com.gjermundbjaanes.beaconmqtt.beacondb;

import android.provider.BaseColumns;

public class BeaconContract {

    private BeaconContract() {}

    public static class BeaconEntry implements BaseColumns {
        public static final String TABLE_NAME = "beacon";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_MINOR = "minor";
        public static final String COLUMN_NAME_MAJOR = "major";
        public static final String COLUMN_NAME_INFORMAL_NAME = "name";
    }
}
