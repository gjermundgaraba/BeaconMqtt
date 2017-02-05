package com.gjermundbjaanes.beaconmqtt.log;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconPersistence;
import com.gjermundbjaanes.beaconmqtt.db.log.LogPerstiance;
import com.gjermundbjaanes.beaconmqtt.db.log.LogResult;
import com.gjermundbjaanes.beaconmqtt.newbeacon.BeaconListAdapter;

import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.GENEARL_LOG_KEY;

public class LogActivity extends AppCompatActivity {

    private ListView logListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final LogPerstiance logPerstiance = new LogPerstiance(this);

        logListView = (ListView) findViewById(R.id.log_list);
        List<LogResult> logs = logPerstiance.getLogs();
        final LogListViewAdapter logListViewAdapter = new LogListViewAdapter(logs, this);
        logListView.setAdapter(logListViewAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.log_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<LogResult> refreshLogs = logPerstiance.getLogs();
                logListViewAdapter.updateLogs(refreshLogs);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        boolean loggingOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GENEARL_LOG_KEY, false);
        if (!loggingOn) {
            Toast.makeText(this, "Logging is turned off, go to settings to turn it on.", Toast.LENGTH_LONG).show();
        }
    }

}
