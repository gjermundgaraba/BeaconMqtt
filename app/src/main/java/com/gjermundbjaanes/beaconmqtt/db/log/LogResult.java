package com.gjermundbjaanes.beaconmqtt.db.log;

import java.util.Date;

public class LogResult {

    private String time;
    private String line;
    private String extraInfo;

    public LogResult(String time, String line, String extraInfo) {
        this.time = time;
        this.line = line;
        this.extraInfo = extraInfo;
    }

    public String getTime() {
        return time;
    }

    public String getLine() {
        return line;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
}
