package com.gjermundbjaanes.beaconmqtt.log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.db.beacon.BeaconPersistence;
import com.gjermundbjaanes.beaconmqtt.db.log.LogPerstiance;
import com.gjermundbjaanes.beaconmqtt.db.log.LogResult;
import com.gjermundbjaanes.beaconmqtt.newbeacon.BeaconListAdapter;

import java.util.List;

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
    }

}
