package com.gjermundbjaanes.beaconmqtt;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconPersistence;
import com.gjermundbjaanes.beaconmqtt.beacondb.BeaconResult;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;

import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.BEACON_NOTIFICATIONS_ENTER_KEY;
import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.BEACON_NOTIFICATIONS_EXIT_KEY;
import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.BEACON_PERIOD_BETWEEN_SCANS_KEY;
import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.BEACON_SCAN_PERIOD_KEY;
import static org.altbeacon.beacon.BeaconManager.DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD;
import static org.altbeacon.beacon.BeaconManager.DEFAULT_BACKGROUND_SCAN_PERIOD;
import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD;

public class BeaconApplication extends Application implements BootstrapNotifier {

    private static final String TAG = BeaconApplication.class.getName();
    private RegionBootstrap regionBootstrap;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        final BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (BEACON_PERIOD_BETWEEN_SCANS_KEY.equals(key)) {
                    Long beaconPeriodBetweenScans = Long.parseLong(sharedPreferences.getString(key, Long.valueOf(DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD).toString()));
                    beaconManager.setBackgroundBetweenScanPeriod(beaconPeriodBetweenScans);
                } else if (BEACON_SCAN_PERIOD_KEY.equals(key)) {
                    Long beaconScanPeriod = Long.parseLong(sharedPreferences.getString(key, Long.valueOf(DEFAULT_BACKGROUND_SCAN_PERIOD).toString()));
                    beaconManager.setBackgroundScanPeriod(beaconScanPeriod);
                }
            }
        };
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        Long beaconPeriodBetweenScans = Long.parseLong(defaultSharedPreferences.getString(BEACON_PERIOD_BETWEEN_SCANS_KEY, Long.valueOf(DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD).toString()));
        beaconManager.setBackgroundBetweenScanPeriod(beaconPeriodBetweenScans);

        Long beaconScanPeriod = Long.parseLong(defaultSharedPreferences.getString(BEACON_SCAN_PERIOD_KEY, Long.valueOf(DEFAULT_BACKGROUND_SCAN_PERIOD).toString()));
        beaconManager.setBackgroundScanPeriod(beaconScanPeriod);

        List<BeaconResult> beacons = new BeaconPersistence(this).getBeacons();

        List<Region> regions = new ArrayList<>(beacons.size());
        for (BeaconResult beacon : beacons) {
            String id = beacon.getUuid() + beacon.getMajor() + beacon.getMinor();
            Region region = new Region(id,
                    Identifier.parse(beacon.getUuid()),
                    Identifier.parse(beacon.getMajor()),
                    Identifier.parse(beacon.getMinor()));
            regions.add(region);
        }

        regionBootstrap = new RegionBootstrap(this, regions);
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG, "Entered region uuid: " + region.getId1() + ", major: " + region.getId2() + ", minor: " + region.getId3());
        boolean showNotification = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(BEACON_NOTIFICATIONS_ENTER_KEY, false);
        if (showNotification) {
            showNotification("Beacon spotted!", "Entered region uuid: " + region.getId1() + ", major: " + region.getId2() + ", minor: " + region.getId3());
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "Exited region uuid: " + region.getId1() + ", major: " + region.getId2() + ", minor: " + region.getId3());
        boolean showNotification = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(BEACON_NOTIFICATIONS_EXIT_KEY, false);
        if (showNotification) {
            showNotification("Beacon lost!", "Exited region uuid: " + region.getId1() + ", major: " + region.getId2() + ", minor: " + region.getId3());
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
