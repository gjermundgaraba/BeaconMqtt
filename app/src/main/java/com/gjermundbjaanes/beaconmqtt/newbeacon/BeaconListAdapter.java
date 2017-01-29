package com.gjermundbjaanes.beaconmqtt.newbeacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.R;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

public class BeaconListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<BeaconListElement> beacons;

    public BeaconListAdapter(Context context) {
        beacons = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateBeacons(List<BeaconListElement> beacons) {
        this.beacons = beacons;
        this.notifyDataSetChanged();
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
        View rowView = layoutInflater.inflate(R.layout.list_new_beacon_layout, parent, false);
        BeaconListElement beacon = beacons.get(position);

        TextView uuidView = (TextView) rowView.findViewById(R.id.beacon_uuid);
        uuidView.setText(beacon.getUuid());

        TextView detailsView = (TextView) rowView.findViewById(R.id.beacon_details);
        String details = "Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor();
        if (beacon.isSaved()) {
            details = "Already saved... " + details;
        }
        detailsView.setText(details);

        return rowView;
    }
}
