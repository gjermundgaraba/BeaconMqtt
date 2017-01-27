package com.gjermundbjaanes.beaconmqtt.mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.MQTT_PORT_KEY;
import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.MQTT_SERVER_KEY;
import static com.gjermundbjaanes.beaconmqtt.settings.SettingsActivity.MQTT_TOPIC_KEY;

public class MqttBroadcaster {

    private static final String TAG = MqttBroadcaster.class.getName();
    private static final String CLIENT_ID = "AndroidMqttBeacon";
    private MqttAndroidClient mqttAndroidClient = null;
    private final SharedPreferences defaultSharedPreferences;

    public MqttBroadcaster(final Context context) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String mqttServer = defaultSharedPreferences.getString(MQTT_SERVER_KEY, null);
        String mqttPort = defaultSharedPreferences.getString(MQTT_PORT_KEY, null);

        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (MQTT_SERVER_KEY.equals(key) || MQTT_PORT_KEY.equals(key)) {
                    String mqttServer = defaultSharedPreferences.getString(MQTT_SERVER_KEY, null);
                    String mqttPort = defaultSharedPreferences.getString(MQTT_PORT_KEY, null);

                    connectToMqttServer(mqttServer, mqttPort, context);
                }
            }
        });

        connectToMqttServer(mqttServer, mqttPort, context);
    }

    private void connectToMqttServer(String mqttServer, String mqttPort, Context context) {
        if (mqttServer != null && mqttPort != null) {
            final String serverUri = "tcp://" + mqttServer + ":" + mqttPort;

            mqttAndroidClient = new MqttAndroidClient(context, serverUri, CLIENT_ID);

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);

            try {
                mqttAndroidClient.connect(mqttConnectOptions, context, new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e(TAG, "Failed to connect to: " +  serverUri, exception);
                    }
                });
            } catch (MqttException e) {
                Log.e(TAG, "Failed to connect to: " +  e);
            }

        } else {
            Log.i(TAG, "Mqtt Server or Port not set");
        }

    }

    public void publisMessage(String uuid, String major, String minor, String event) {
        if (mqttAndroidClient != null) {
            String defaultTopic = "beacon";
            String preferenceTopic = defaultSharedPreferences.getString(MQTT_TOPIC_KEY, defaultTopic);

            if (preferenceTopic.isEmpty()) {
                preferenceTopic = defaultTopic;
            }

            String topic = preferenceTopic;
            if (!topic.endsWith("/")) {
                topic += "/";
            }
            topic += event;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uuid", uuid);
                jsonObject.put("major", major);
                jsonObject.put("minor", minor);
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(jsonObject.toString().getBytes());
                mqttAndroidClient.publish(topic, mqttMessage);
            } catch (MqttException | JSONException e) {
                Log.e(TAG, "Error Publishing on topic: " + topic, e);
            }
        } else {
            Log.i(TAG, "Publish not done because mqttAndroidClient is not set up");
        }

    }
}
