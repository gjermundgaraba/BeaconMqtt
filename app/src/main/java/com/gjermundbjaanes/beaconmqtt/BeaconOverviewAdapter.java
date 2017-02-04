package com.gjermundbjaanes.beaconmqtt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconResult;

import java.util.ArrayList;
import java.util.List;

public class BeaconOverviewAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater layoutInflater;
    private List<BeaconResult> savedBeacons;
    private List<BeaconResult> beaconsInRange = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener = null;

    public BeaconOverviewAdapter(Context context, List<BeaconResult> savedBeacons) {
        this.savedBeacons = savedBeacons;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateSavedBeacons(List<BeaconResult> savedBeacons) {
        this.savedBeacons = savedBeacons;
        this.notifyDataSetChanged();
    }

    public void updateBeaconsInRange(List<BeaconResult> beaconsInRange) {
        this.beaconsInRange = beaconsInRange;
        this.notifyDataSetChanged();
    }


    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) {
            return savedBeacons.size();
        } else {
            return beaconsInRange.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0) {
            return savedBeacons.get(childPosition);
        } else {
            return beaconsInRange.get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View headerView = layoutInflater.inflate(R.layout.list_group, parent, false);

        TextView listHeader = (TextView) headerView.findViewById(R.id.list_header);

        if (groupPosition == 0) {
            listHeader.setText("Saved Beacons");
        } else {
            listHeader.setText("Saved Beacons in Range");
        }

        return headerView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.list_beacon_layout, parent, false);

        BeaconResult beacon;
        if (groupPosition == 0) {
             beacon = savedBeacons.get(childPosition);
        } else {
            beacon = beaconsInRange.get(childPosition);
        }

        TextView nameView = (TextView) rowView.findViewById(R.id.beacon_name);
        nameView.setText(beacon.getInformalName());

        TextView uuidView = (TextView) rowView.findViewById(R.id.beacon_uuid);
        uuidView.setText(beacon.getUuid());

        TextView detailsView = (TextView) rowView.findViewById(R.id.beacon_details);
        String details = "Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor();
        detailsView.setText(details);

        Button deleteButton = (Button) rowView.findViewById(R.id.delete_beacon_button);
        final BeaconResult finalBeacon = beacon;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onBeaconDeleteClick(finalBeacon);
                }
            }
        });

        return rowView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public interface OnDeleteClickListener {
        void onBeaconDeleteClick(BeaconResult beaconResult);
    }
}
