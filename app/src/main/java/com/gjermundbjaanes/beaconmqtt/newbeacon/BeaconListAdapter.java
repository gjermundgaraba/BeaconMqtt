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
    private List<Beacon> beacons;

    public BeaconListAdapter(Context context) {
        beacons = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateBeacons(List<Beacon> beacons) {
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
        View rowView = layoutInflater.inflate(R.layout.list_beacon_layout, parent, false);
        Beacon beacon = beacons.get(position);

        TextView uuidView = (TextView) rowView.findViewById(R.id.beacon_uuid);
        uuidView.setText(beacon.getId1().toString());

        TextView detailsView = (TextView) rowView.findViewById(R.id.beacon_details);
        detailsView.setText("Major: " + beacon.getId2() + " Minor: " + beacon.getId3());

        return rowView;
    }
}
