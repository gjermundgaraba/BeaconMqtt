package com.gjermundbjaanes.beaconmqtt.newbeacon;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconPersistence;
import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconResult;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.BeaconScanHelper.startBeaconScan;

public class NewBeaconActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = NewBeaconActivity.class.getName();
    private static final String REGION_ID_FOR_RANGING = "myRangingUniqueId";

    private BeaconManager beaconManager;

    private ListView beaconSearchListView;
    private List<BeaconResult> persistedBeaconList = new ArrayList<>();
    private BeaconListAdapter beaconListAdapter;
    private BeaconPersistence beaconPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        beaconSearchListView = (ListView) findViewById(R.id.beacon_search_list);

        beaconListAdapter = new BeaconListAdapter(this);
        beaconSearchListView.setAdapter(beaconListAdapter);

        beaconPersistence = new BeaconPersistence(this);
        persistedBeaconList = beaconPersistence.getBeacons();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        startBeaconScan(beaconManager, this);

        beaconSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BeaconListElement beaconListElement = (BeaconListElement) beaconSearchListView.getItemAtPosition(position);

                beaconPersistence.saveBeacon(beaconListElement.getBeacon());
                persistedBeaconList = beaconPersistence.getBeacons();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
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

                if (beaconSaved(beacon)) {
                    beaconListElement.setSaved(true);
                }

                return beaconListElement;
            }

            private boolean beaconSaved(Beacon beacon) {
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
            String errorMessage = "Not able to start ranging beacons";
            Log.e(TAG, errorMessage, e);
            Snackbar.make(this.beaconSearchListView, errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }

}
