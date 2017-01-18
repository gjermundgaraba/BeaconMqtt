package com.gjermundbjaanes.beaconmqtt.beacondb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BeaconDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Beacon.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BeaconContract.BeaconEntry.TABLE_NAME + " (" +
                    BeaconContract.BeaconEntry.COLUMN_NAME_UUID + " TEXT," +
                    BeaconContract.BeaconEntry.COLUMN_NAME_MINOR + " TEXT," +
                    BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR + " TEXT," +
                    "PRIMARY KEY (" +
                    BeaconContract.BeaconEntry.COLUMN_NAME_UUID + ", " +
                    BeaconContract.BeaconEntry.COLUMN_NAME_MINOR + ", " +
                    BeaconContract.BeaconEntry.COLUMN_NAME_MAJOR + ")" +
                    ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BeaconContract.BeaconEntry.TABLE_NAME;

    public BeaconDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
