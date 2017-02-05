package com.gjermundbjaanes.beaconmqtt.db.log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gjermundbjaanes.beaconmqtt.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.db.log.LogContract.LogEntry.COLUMN_NAME_EXTRA_INFO;
import static com.gjermundbjaanes.beaconmqtt.db.log.LogContract.LogEntry.COLUMN_NAME_LOG_LINE;
import static com.gjermundbjaanes.beaconmqtt.db.log.LogContract.LogEntry.COLUMN_NAME_TIME;
import static com.gjermundbjaanes.beaconmqtt.db.log.LogContract.LogEntry.TABLE_NAME;


public class LogPersistence {

    private final DbHelper dbHelper;

    public LogPersistence(Context context) {
        dbHelper = new DbHelper(context);
    }

    public List<LogResult> getLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String[] columns = {
                    COLUMN_NAME_TIME,
                    COLUMN_NAME_LOG_LINE,
                    COLUMN_NAME_EXTRA_INFO
            };

            Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

            List<LogResult> logs = new ArrayList<>();
            while(cursor.moveToNext()) {
                String time = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TIME));
                String logLine = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOG_LINE));
                String extraInfo = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EXTRA_INFO));
                logs.add(new LogResult(time, logLine, extraInfo));
            }
            cursor.close();

            return logs;
        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public void saveNewLog(String logLine, String extraInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME_LOG_LINE, logLine);
            values.put(COLUMN_NAME_EXTRA_INFO, extraInfo);

            db.insert(TABLE_NAME, null, values);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void deleteAllLogs() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.delete(TABLE_NAME, null, null);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
