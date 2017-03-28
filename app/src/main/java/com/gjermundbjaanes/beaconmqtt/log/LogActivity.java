package com.gjermundbjaanes.beaconmqtt.log;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gjermundbjaanes.beaconmqtt.R;
import com.gjermundbjaanes.beaconmqtt.db.log.LogPersistence;
import com.gjermundbjaanes.beaconmqtt.db.log.LogResult;

import java.util.ArrayList;
import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.GENEARL_LOG_KEY;

public class LogActivity extends AppCompatActivity {

    private LogListViewAdapter logListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final LogPersistence logPersistence = new LogPersistence(this);

        ListView logListView = (ListView) findViewById(R.id.log_list);
        List<LogResult> logs = logPersistence.getLogs();
        logListViewAdapter = new LogListViewAdapter(logs, this);
        logListView.setAdapter(logListViewAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.log_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<LogResult> refreshLogs = logPersistence.getLogs();
                logListViewAdapter.updateLogs(refreshLogs);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        boolean loggingOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GENEARL_LOG_KEY, false);
        if (!loggingOn) {
            Toast.makeText(this, R.string.logging_turned_off_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all_logs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);

            builder.setTitle(R.string.delete_logs_title)
                    .setMessage(R.string.delete_logs_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new LogPersistence(LogActivity.this).deleteAllLogs();
                            logListViewAdapter.updateLogs(new ArrayList<LogResult>());
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

}
