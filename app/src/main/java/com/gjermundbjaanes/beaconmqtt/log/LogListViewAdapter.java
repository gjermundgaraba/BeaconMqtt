package com.gjermundbjaanes.beaconmqtt.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.db.log.LogResult;

import java.util.List;

public class LogListViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<LogResult> logs;

    public LogListViewAdapter(List<LogResult> logs, Context context) {
        this.logs = logs;
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
}
