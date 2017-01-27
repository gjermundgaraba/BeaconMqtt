package com.gjermundbjaanes.beaconmqtt.beacondb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.beacondb.BeaconContract.BeaconEntry.COLUMN_NAME_INFORMAL_NAME;
import static com.gjermundbjaanes.beaconmqtt.beacondb.BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR;
import static com.gjermundbjaanes.beaconmqtt.beacondb.BeaconContract.BeaconEntry.COLUMN_NAME_MINOR;
import static com.gjermundbjaanes.beaconmqtt.beacondb.BeaconContract.BeaconEntry.COLUMN_NAME_UUID;

public class BeaconPersistence {

    private final BeaconDbHelper beaconDbHelper;

    public BeaconPersistence(Context context) {
        beaconDbHelper = new BeaconDbHelper(context);
    }

    public List<BeaconResult> getBeacons() {
        SQLiteDatabase db = beaconDbHelper.getReadableDatabase();

        try {
            String[] columns = {
                    COLUMN_NAME_UUID,
                    COLUMN_NAME_MAJOR,
                    COLUMN_NAME_MINOR,
                    COLUMN_NAME_INFORMAL_NAME,
            };

            Cursor cursor = db.query(BeaconContract.BeaconEntry.TABLE_NAME, columns, null, null, null, null, null);

            List<BeaconResult> beacons = new ArrayList<>();
            while(cursor.moveToNext()) {
                String uuid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UUID));
                String major = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MAJOR));
                String minor = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MINOR));
                String informalName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_INFORMAL_NAME));
                beacons.add(new BeaconResult(uuid, major, minor, informalName));
            }
            cursor.close();

            return beacons;
        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public void saveBeacon(Beacon beacon, String informalBeaconName) {
        SQLiteDatabase db = beaconDbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME_UUID, beacon.getId1().toString());
            values.put(COLUMN_NAME_MAJOR, beacon.getId2().toString());
            values.put(COLUMN_NAME_MINOR, beacon.getId3().toString());
            values.put(COLUMN_NAME_INFORMAL_NAME, informalBeaconName);

            db.insert(BeaconContract.BeaconEntry.TABLE_NAME, null, values);
        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public BeaconResult getBeacon(String uuid, String major, String minor) {
        SQLiteDatabase db = beaconDbHelper.getReadableDatabase();

        try {
            String[] columns = {
                    COLUMN_NAME_UUID,
                    COLUMN_NAME_MAJOR,
                    COLUMN_NAME_MINOR,
                    COLUMN_NAME_INFORMAL_NAME,
            };

            String selection = COLUMN_NAME_UUID + "=? AND " + COLUMN_NAME_MAJOR + "=? AND " + COLUMN_NAME_MINOR + "=?";

            Cursor cursor = db.query(BeaconContract.BeaconEntry.TABLE_NAME, columns, selection, new String[] {uuid, major, minor}, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                String informalName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_INFORMAL_NAME));
                cursor.close();

                return new BeaconResult(uuid, major, minor, informalName);
            }

        } finally {
            if (db != null) {
                db.close();
            }
        }

        return null;
    }
}
