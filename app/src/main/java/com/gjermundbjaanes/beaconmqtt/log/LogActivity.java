package com.gjermundbjaanes.beaconmqtt.log;

import android.os.Bundle;
import android.preference.PreferenceManager;
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

        logListView = (ListView) findViewById(R.id.log_list);

        List<LogResult> logs = new LogPerstiance(this).getLogs();
        LogListViewAdapter logListViewAdapter = new LogListViewAdapter(logs, this);

        logListView.setAdapter(logListViewAdapter);

        boolean loggingOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GENEARL_LOG_KEY, false);

        if (!loggingOn) {
            Toast.makeText(this, "Logging is turned off, go to settings to turn it on.", Toast.LENGTH_LONG).show();
        }
    }

}
