package com.gjermundbjaanes.beaconmqtt.db.log;

import android.provider.BaseColumns;

public class LogContract {
    private LogContract() {}

    public static class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "log";

        public static final String COLUMN_NAME_TIME= "event_time";
        public static final String COLUMN_NAME_LOG_LINE = "log_line";
        public static final String COLUMN_NAME_EXTRA_INFO = "extra_info";
    }
}
