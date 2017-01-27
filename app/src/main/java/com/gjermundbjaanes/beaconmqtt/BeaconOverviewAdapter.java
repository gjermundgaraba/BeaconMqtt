package com.gjermundbjaanes.beaconmqtt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconResult;

import java.util.List;

public class BeaconOverviewAdapter extends BaseAdapter {
    private final List<BeaconResult> beacons;
    private final LayoutInflater layoutInflater;

    public BeaconOverviewAdapter(Context context, List<BeaconResult> beacons) {
        this.beacons = beacons;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Object getItem(int position) {
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.list_beacon_layout, parent, false);
        BeaconResult beacon = beacons.get(position);

        TextView nameView = (TextView) rowView.findViewById(R.id.beacon_name);
        nameView.setText(beacon.getInformalName());

        TextView uuidView = (TextView) rowView.findViewById(R.id.beacon_uuid);
        uuidView.setText(beacon.getUuid());

        TextView detailsView = (TextView) rowView.findViewById(R.id.beacon_details);
        String details = "Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor();
        detailsView.setText(details);

        return rowView;
    }
}
