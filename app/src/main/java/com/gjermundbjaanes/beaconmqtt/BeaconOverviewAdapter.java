package com.gjermundbjaanes.beaconmqtt;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconResult;

import java.util.ArrayList;
import java.util.List;

public class BeaconOverviewAdapter extends BaseExpandableListAdapter {

    public interface OnDeleteClickListener {
        void onBeaconDeleteClick(BeaconResult beaconResult);
    }

    private static final int NUMBER_OF_GROUPS = 2;
    private static final int SAVED_BEACONS_GROUP_ID = 0;
    private final LayoutInflater layoutInflater;
    private Context context;
    private List<BeaconResult> savedBeacons;
    private List<BeaconResult> beaconsInRange = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener = null;

    public BeaconOverviewAdapter(Context context, List<BeaconResult> savedBeacons) {
        this.context = context;
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
        return NUMBER_OF_GROUPS;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == SAVED_BEACONS_GROUP_ID) {
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
        if (groupPosition == SAVED_BEACONS_GROUP_ID) {
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
            listHeader.setText(R.string.saved_beacons_header);
        } else {
            listHeader.setText(R.string.saved_beacons_in_range_header);
        }

        return headerView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.list_beacon_layout, parent, false);

        BeaconResult beacon = getBeaconForView(groupPosition, childPosition);

        if (beacon != null) {
            setUpTextForView(rowView, beacon);
            setUpDeleteButton(groupPosition, rowView, beacon);
        }

        return rowView;
    }

    @Nullable
    private BeaconResult getBeaconForView(int groupPosition, int childPosition) {
        BeaconResult beacon = null;

        if (groupPosition == SAVED_BEACONS_GROUP_ID) {
            beacon = savedBeacons.get(childPosition);
        } else if ((beaconsInRange.size() - 1) >= childPosition) {
            beacon = beaconsInRange.get(childPosition);
        }

        return beacon;
    }

    private void setUpTextForView(View rowView, BeaconResult beacon) {
        TextView nameView = (TextView) rowView.findViewById(R.id.beacon_name);
        nameView.setText(beacon.getInformalName());

        TextView uuidView = (TextView) rowView.findViewById(R.id.beacon_uuid);
        uuidView.setText(beacon.getUuid());

        TextView detailsView = (TextView) rowView.findViewById(R.id.beacon_details);
        String details = context.getString(R.string.beacon_details, beacon.getMajor(), beacon.getMinor());
        detailsView.setText(details);
    }

    private void setUpDeleteButton(int groupPosition, View rowView, final BeaconResult beacon) {
        Button deleteButton = (Button) rowView.findViewById(R.id.delete_beacon_button);
        if (groupPosition == SAVED_BEACONS_GROUP_ID) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onBeaconDeleteClick(beacon);
                    }
                }
            });
        } else {
            ((ViewGroup) deleteButton.getParent()).removeView(deleteButton);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
