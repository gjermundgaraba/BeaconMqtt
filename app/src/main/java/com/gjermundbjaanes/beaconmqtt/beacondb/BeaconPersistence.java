package com.gjermundbjaanes.beaconmqtt.beacondb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

public class BeaconPersistence {

    private final BeaconDbHelper beaconDbHelper;

    public BeaconPersistence(Context context) {
        beaconDbHelper = new BeaconDbHelper(context);
    }

    public List<BeaconResult> getBeacons() {
        SQLiteDatabase db = beaconDbHelper.getReadableDatabase();

        String[] columns = {
                BeaconContract.BeaconEntry.COLUMN_NAME_UUID,
                BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR,
                BeaconContract.BeaconEntry.COLUMN_NAME_MINOR,
        };

        Cursor cursor = db.query(BeaconContract.BeaconEntry.TABLE_NAME, columns, null, null, null, null, null);

        List<BeaconResult> beacons = new ArrayList<>();
        while(cursor.moveToNext()) {
            String uuid = cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COLUMN_NAME_UUID));
            String major = cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR));
            String minor = cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COLUMN_NAME_MINOR));
            beacons.add(new BeaconResult(uuid, major, minor));
        }
        cursor.close();

        return beacons;
    }

    public void saveBeacon(Beacon beacon) {
        SQLiteDatabase db = beaconDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BeaconContract.BeaconEntry.COLUMN_NAME_UUID, beacon.getId1().toString());
        values.put(BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR, beacon.getId2().toString());
        values.put(BeaconContract.BeaconEntry.COLUMN_NAME_MINOR, beacon.getId3().toString());

        db.insert(BeaconContract.BeaconEntry.TABLE_NAME, null, values);
    }
}
