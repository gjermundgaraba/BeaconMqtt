package com.gjermundbjaanes.beaconmqtt.newbeacon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gjermundbjaanes.beaconmqtt.BeaconApplication;
import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconPersistence;
import com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconResult;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NewBeaconActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = NewBeaconActivity.class.getName();
    private static final String REGION_ID_FOR_RANGING = "myRangingUniqueId";

    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private ListView beaconSearchListView;
    private List<BeaconResult> persistedBeaconList = new ArrayList<>();
    private BeaconListAdapter beaconListAdapter;
    private BeaconPersistence beaconPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);

        setUpToolBar();
        setUpBeaconList();
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpBeaconList() {
        beaconSearchListView = (ListView) findViewById(R.id.beacon_search_list);

        beaconListAdapter = new BeaconListAdapter(this);
        beaconSearchListView.setAdapter(beaconListAdapter);

        beaconPersistence = new BeaconPersistence(this);
        persistedBeaconList = beaconPersistence.getBeacons();

        beaconSearchListView.setOnItemClickListener(new OnBeaconClickListener());

        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                handleNewBeaconsInRange(beacons);
            }

            private void handleNewBeaconsInRange(Collection<Beacon> beacons) {
                final List<BeaconListElement> beaconsList = new ArrayList<>();

                for (Beacon beacon : beacons) {
                    beaconsList.add(beaconToBeaconListElement(beacon));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconListAdapter.updateBeacons(beaconsList);
                    }
                });
            }

            private BeaconListElement beaconToBeaconListElement(Beacon beacon) {
                BeaconListElement beaconListElement = new BeaconListElement(beacon);

                if (beaconIsSaved(beacon)) {
                    beaconListElement.setSaved(true);
                }

                return beaconListElement;
            }

            private boolean beaconIsSaved(Beacon beacon) {
                for (BeaconResult beaconResult : persistedBeaconList) {
                    if (beacon.getId1().toString().equals(beaconResult.getUuid()) &&
                            beacon.getId2().toString().equals(beaconResult.getMajor()) &&
                            beacon.getId3().toString().equals(beaconResult.getMinor())) {
                        return true;
                    }
                }

                return false;
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region(REGION_ID_FOR_RANGING, null, null, null));
        } catch (RemoteException e) {
            String errorMessage = getString(R.string.not_able_to_start_ranging_beacons);
            Log.e(TAG, errorMessage, e);
            Snackbar.make(this.beaconSearchListView, errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_beacon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add_beacon_manually) {
            AlertDialog.Builder builder = createAlertDialog();
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private AlertDialog.Builder createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewBeaconActivity.this);
        LayoutInflater inflater = NewBeaconActivity.this.getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_new_manual_beacon, null);
        builder.setView(dialogLayout)
                .setPositiveButton(R.string.dialog_save_beacon, new SaveBeaconClickListener(dialogLayout))
                .setNegativeButton(R.string.dialog_cancel_beacon, null);
        return builder;
    }

    private class OnBeaconClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final BeaconListElement beaconListElement = (BeaconListElement) beaconSearchListView.getItemAtPosition(position);

            if (!beaconListElement.isSaved()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewBeaconActivity.this);
                LayoutInflater inflater = NewBeaconActivity.this.getLayoutInflater();
                final View dialogLayout = inflater.inflate(R.layout.dialog_new_beacon, null);
                builder.setView(dialogLayout)
                        .setPositiveButton(R.string.dialog_save_beacon, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveBeacon();
                            }

                            private void saveBeacon() {
                                TextView newBeaconNameTextView = (TextView) dialogLayout.findViewById(R.id.dailog_new_beacon_name);
                                String informalBeaconName = newBeaconNameTextView.getText().toString();

                                beaconPersistence.saveBeacon(beaconListElement.getBeacon(), informalBeaconName);
                                persistedBeaconList = beaconPersistence.getBeacons();
                                ((BeaconApplication) getApplication()).restartBeaconSearch();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_beacon, null)
                        .show();
            }
        }
    }

    private class SaveBeaconClickListener implements DialogInterface.OnClickListener {
        private final View dialogLayout;

        SaveBeaconClickListener(View dialogLayout) {
            this.dialogLayout = dialogLayout;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            TextView newBeaconNameTextView = (TextView) dialogLayout.findViewById(R.id.manual_dailog_new_beacon_name);
            String informalBeaconName = newBeaconNameTextView.getText().toString();

            TextView newBeaconUuidTextView = (TextView) dialogLayout.findViewById(R.id.manual_dailog_new_beacon_uuid);
            String beaconUuid = newBeaconUuidTextView.getText().toString();

            TextView newBeaconMajorTextView = (TextView) dialogLayout.findViewById(R.id.manual_dailog_new_beacon_major);
            String beaconMajor = newBeaconMajorTextView.getText().toString();

            TextView newBeaconMinorTextView = (TextView) dialogLayout.findViewById(R.id.manual_dailog_new_beacon_minor);
            String beaconMinor = newBeaconMinorTextView.getText().toString();

            beaconPersistence.saveBeacon(beaconUuid, beaconMajor, beaconMinor, informalBeaconName);
            persistedBeaconList = beaconPersistence.getBeacons();
            ((BeaconApplication) getApplication()).restartBeaconSearch();
        }
    }
}
