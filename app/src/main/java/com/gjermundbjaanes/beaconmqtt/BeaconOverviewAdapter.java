package com.gjermundbjaanes.beaconmqtt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconResult;
import com.gjermundbjaanes.beaconmqtt.newbeacon.BeaconListElement;

import java.util.List;

public class BeaconOverviewAdapter extends BaseAdapter {
    private List<BeaconResult> beacons;
    private final LayoutInflater layoutInflater;
    private OnDeleteClickListener onDeleteClickListener = null;

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
        final BeaconResult beacon = beacons.get(position);

        TextView nameView = (TextView) rowView.findViewById(R.id.beacon_name);
        nameView.setText(beacon.getInformalName());

        TextView uuidView = (TextView) rowView.findViewById(R.id.beacon_uuid);
        uuidView.setText(beacon.getUuid());

        TextView detailsView = (TextView) rowView.findViewById(R.id.beacon_details);
        String details = "Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor();
        detailsView.setText(details);

        Button deleteButton = (Button) rowView.findViewById(R.id.delete_beacon_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onBeaconDeleteClick(beacon);
                }
            }
        });

        return rowView;
    }

    public void updateBeacons(List<BeaconResult> beacons) {
        this.beacons = beacons;
        this.notifyDataSetChanged();
    }


    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public interface OnDeleteClickListener {
        void onBeaconDeleteClick(BeaconResult beaconResult);
    }
}
