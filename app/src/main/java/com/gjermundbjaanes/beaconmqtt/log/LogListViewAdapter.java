package com.gjermundbjaanes.beaconmqtt.log;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.BeaconApplication;
import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.db.log.LogResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogListViewAdapter extends BaseAdapter {

    private static final String TAG = LogListViewAdapter.class.getName();

    private LayoutInflater layoutInflater;
    private List<LogResult> logs;

    public LogListViewAdapter(List<LogResult> logs, Context context) {
        updateLogs(logs);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Object getItem(int position) {
        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.list_log_layout, parent, false);
        LogResult log = logs.get(position);

        TextView logLineView = (TextView) rowView.findViewById(R.id.log_line);
        logLineView.setText(log.getLine());

        TextView logTimeView = (TextView) rowView.findViewById(R.id.log_time);
        logTimeView.setText(log.getTime());

        return rowView;
    }

    void updateLogs(List<LogResult> refreshLogs) {
        this.logs = refreshLogs;

        Collections.sort(this.logs, new Comparator<LogResult>() {
            @Override
            public int compare(LogResult logResult, LogResult otherLogResult) {
                SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                int compare = 0;
                try {
                    Date firstDate = iso8601Format.parse(logResult.getTime());
                    Date secondDate = iso8601Format.parse(otherLogResult.getTime());

                    if (firstDate.before(secondDate)) {
                        compare = 1;
                    } else if (secondDate.before(firstDate)) {
                        compare = -1;
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Failed to sort properly on log", e);
                }

                return compare;
            }
        });

        notifyDataSetChanged();
    }
}
