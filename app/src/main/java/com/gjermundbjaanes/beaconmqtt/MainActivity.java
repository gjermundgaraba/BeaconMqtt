package com.gjermundbjaanes.beaconmqtt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconPersistence;
import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconResult;
import com.gjermundbjaanes.beaconmqtt.newbeacon.NewBeaconActivity;
import com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconOverviewAdapter beaconOverviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newBeaconIntent = new Intent(getApplicationContext(), NewBeaconActivity.class);
                startActivity(newBeaconIntent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        List<BeaconResult> beacons = new BeaconPersistence(this).getBeacons();
        ExpandableListView beaconOverviewListView = (ExpandableListView) findViewById(R.id.beacon_overview_list);
        beaconOverviewAdapter = new BeaconOverviewAdapter(this, beacons);
        beaconOverviewAdapter.setOnDeleteClickListener(new BeaconOverviewAdapter.OnDeleteClickListener() {
            @Override
            public void onBeaconDeleteClick(final BeaconResult beaconResult) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete Confirmation")
                        .setMessage("Are you sure you want to delete this beacon?")
                        .setNegativeButton(R.string.dialog_cancel_beacon, null)
                        .setPositiveButton(R.string.dialog_delete_beacon, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BeaconPersistence beaconPersistence = new BeaconPersistence(MainActivity.this);
                                boolean beaconDeleted = beaconPersistence.deleteBeacon(beaconResult);
                                if (beaconDeleted) {
                                    beaconOverviewAdapter.updateSavedBeacons(beaconPersistence.getBeacons());
                                    ((BeaconApplication) getApplication()).restartBeaconSearch();

                                } else {
                                    Toast.makeText(MainActivity.this, "Not able to delete beacon", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
        beaconOverviewListView.setAdapter(beaconOverviewAdapter);

        beaconOverviewListView.expandGroup(0);
        beaconOverviewListView.expandGroup(1);

        BeaconApplication beaconApplication = (BeaconApplication) getApplication();
        beaconApplication.setBeaconInRangeListener(new BeaconApplication.BeaconInRangeListener() {
            @Override
            public void beaconsInRangeChanged(final List<BeaconResult> beacons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconOverviewAdapter.updateBeaconsInRange(beacons);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("DERP", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        });
                    }
                    builder.show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
