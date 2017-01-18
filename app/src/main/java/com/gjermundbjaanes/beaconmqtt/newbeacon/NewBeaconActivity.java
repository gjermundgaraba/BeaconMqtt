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

public class NewBeaconActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = NewBeaconActivity.class.getName();
    private static final String REGION_ID_FOR_RANGING = "myRangingUniqueId";

    private BeaconManager beaconManager;

    private ListView beaconListView;
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

        beaconListView = (ListView) findViewById(R.id.add_beacon_list);

        beaconListAdapter = new BeaconListAdapter(this);
        beaconListView.setAdapter(beaconListAdapter);

        beaconPersistence = new BeaconPersistence(this);
        persistedBeaconList = beaconPersistence.getBeacons();

        startBeaconScan();
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BeaconListElement beaconListElement = (BeaconListElement) beaconListView.getItemAtPosition(position);

                beaconPersistence.saveBeacon(beaconListElement.getBeacon());
                persistedBeaconList = beaconPersistence.getBeacons();
            }
        });
    }

    private void startBeaconScan() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

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
            Snackbar.make(this.beaconListView, errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }

}
