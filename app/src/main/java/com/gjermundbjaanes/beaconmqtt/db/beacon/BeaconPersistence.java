package com.gjermundbjaanes.beaconmqtt.db.beacon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gjermundbjaanes.beaconmqtt.db.DbHelper;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconContract.BeaconEntry.COLUMN_NAME_INFORMAL_NAME;
import static com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR;
import static com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconContract.BeaconEntry.COLUMN_NAME_MINOR;
import static com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconContract.BeaconEntry.COLUMN_NAME_UUID;
import static com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconContract.BeaconEntry.TABLE_NAME;

public class BeaconPersistence {

    private static final String PRIMARY_KEY_SELECTION = COLUMN_NAME_UUID + "=? AND " + COLUMN_NAME_MAJOR + "=? AND " + COLUMN_NAME_MINOR + "=?";
    private final DbHelper dbHelper;

    public BeaconPersistence(Context context) {
        dbHelper = new DbHelper(context);
    }

    public List<BeaconResult> getBeacons() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = {
                    COLUMN_NAME_UUID,
                    COLUMN_NAME_MAJOR,
                    COLUMN_NAME_MINOR,
                    COLUMN_NAME_INFORMAL_NAME,
            };

            Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

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
        saveBeacon(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString(), informalBeaconName);
    }

    public void saveBeacon(String uuid, String major, String minor, String informalBeaconName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME_UUID, uuid);
            values.put(COLUMN_NAME_MAJOR, major);
            values.put(COLUMN_NAME_MINOR, minor);
            values.put(COLUMN_NAME_INFORMAL_NAME, informalBeaconName);

            db.insert(TABLE_NAME, null, values);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public BeaconResult getBeacon(String uuid, String major, String minor) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = {
                    COLUMN_NAME_UUID,
                    COLUMN_NAME_MAJOR,
                    COLUMN_NAME_MINOR,
                    COLUMN_NAME_INFORMAL_NAME,
            };

            Cursor cursor = db.query(TABLE_NAME, columns, PRIMARY_KEY_SELECTION, new String[] {uuid, major, minor}, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
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

    public boolean deleteBeacon(BeaconResult beaconResult) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            int numberOfRowsAffected = db.delete(TABLE_NAME, PRIMARY_KEY_SELECTION, new String[] {beaconResult.getUuid(), beaconResult.getMajor(), beaconResult.getMinor()});
            return numberOfRowsAffected != 0;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
