# BeaconMqtt

## About
Android app to forward beacon events over MQTT (for home automation scenarios).

The basic idea behind the app is to allow you to integrate beacons into your home automation systems using MQTT.

In the app you can search for and add beacons. 
You can then configure an MQTT broker that will be notified each time the beacons enter or exists the range of the phone. 

There are a lot of configuration options like:
* MQTT Becaon Enter Topic
* MQTT Beacon Exit Topic
* Time between scans
* Time spent on scans
* Notifications for debug purposes
* Log on the app for debug purposes

Libraries used:
* Eclipse MQTT
* Android AltBeacon

## Screenshots

### Overivew

![Overview](https://github.com/bjaanes/BeaconMqtt/raw/master/screenshots/overview.png)

### Adding Beacons

![Adding Beacons](https://github.com/bjaanes/BeaconMqtt/raw/master/screenshots/add_beacon.png)

### Beacons Settings

![Beacon Settings](https://github.com/bjaanes/BeaconMqtt/raw/master/screenshots/beacon_settings.png)

### MQTT Settings

![MQTT Settings](https://github.com/bjaanes/BeaconMqtt/raw/master/screenshots/mqtt_settings.png)

### Notifications

![Notifications](https://github.com/bjaanes/BeaconMqtt/raw/master/screenshots/notification.png)