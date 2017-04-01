package com.gjermundbjaanes.beaconmqtt.newbeacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.R;

import java.util.ArrayList;
import java.util.List;

public class BeaconListAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater layoutInflater;
    private List<BeaconListElement> beacons;

    BeaconListAdapter(Context context) {
        this.context = context;

        beacons = new ArrayList<>();
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    void updateBeacons(List<BeaconListElement> beacons) {
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
        String details = context.getString(R.string.beacon_details, beacon.getMajor(), beacon.getMinor());
        if (beacon.isSaved()) {
            details = context.getString(R.string.new_beacon_details_already_saved) + details;
        }
        detailsView.setText(details);

        return rowView;
    }
}
