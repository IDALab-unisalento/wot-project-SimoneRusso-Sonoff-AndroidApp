This project has been divided in five parts:
1.    Design and development of an Android native app;
2.    Activation of two networks and development of a gateway
a.    The gateway must accept REST API calls;
b.    Creation of two networks through Raspberry Pi;
1.    Modification of the Android app that will send the on and off commands to the gateway instead of the Sonoff Mini;
3.    Design and development of a backend that accepts commands through REST API, forwards them to the gateway and edits the Android app in order to include a button to ask for permission to access to a protected area;
4.    Implementation of push notification when the state of the Sonoff Mini changes;
5.    Implementation of an Authentication system in app.
Each branch contains one of the five parts, in branch named fase-1 there is the first point of the bulleted list, in fase-2 there is the second point and so on.
In this repository there is the Android native app.
The gateway can be found at www.cercalink.com and the backend at www.cercalink.it.

# Fase 1

At this stage, an Android app that is able to communicate directly with the Sonoff Mini through the MQTT protocol app has been developed. It should be noted that the first phase does not involve the creation of a secondary network, it implies that, in order to allow communication, the app and the Sonoff Mini must necessarily be connected to the same network, otherwise it would not be possible to reach the Sonoff.
At launch, the app subscribes itself to the topic "stat/tasmota_8231A8/POWER1", which remains open for the entire life of the application, and publishes an empty message on the topic "cmnd/tasmota_8231A8/Power1", in order to obtain the status of the device and to be able to set the UI correctly.
The Sonoff Mini publishes a message containing the status of the device on the first topic when the latter changed or in response to a publication on the second topic.

![Algorithm schema](./images/1.png)
Figure 1: Phase 1 sequence diagram (connection to the broker)

When a user changes the status of the device, the app publishes a message containing the status on which the user want to bring the Sonoff Mini (ON or OFF)  on the topic "cmnd/tasmota_8231A8/POWER1"

![Algorithm schema](./images/2.png)
Figure 2: Phase 1 sequence diagram (state change)

# Fase 2

This phase has been divided into three points: 
» Creation of a secondary network that will not be accessible to anyone and to which just the Sonoff Mini can be connected;
» Creation of a gateway that accepts incoming REST calls and translates them into commands for the Sonoff;
» Changing the app that, with the introduction of the gateway, will no longer communicate directly with the Sonoff, but with the gateway.
The realization of the secondary network takes place through the Raspberry Pi, which, connected to a LAN network, has been configured as a wireless access point.

![Algorithm schema](./images/3.png)
Figure 3: Distinguishing the two networks through the Raspberry Pi

The gateway, developed in Java with the Spring framework, exposes three APIs:
» getStatus(), this API opens the connection with the broker, subscribes to the topic "stat/tasmota_8231A8/POWER1", publishes an empty message on the topic "cmnd/tasmota_8231A8/Power1" and closes the connection after intercepting the message. Finally, it returns the status of the Sonoff;
» changestatusON(), this API opens the connection, makes a publication on the topic "cmnd/tasmota_8231A8/POWER1" with the message "ON", thus changing the status of the device to ON, closes the connection and returns a ResponseEntity;
» changeStatusOFF(), this API opens the connection, makes a publication on the topic "cmnd/tasmota_8231A8/POWER1" with the message "OFF", thus changing the status of the device to ON, closes the connection and returns a ResponseEntity.
All APIs take the client id as a parameter, so that it can manage the connections of the various clients to the MQTT broker.
The app has been modified to be able to make REST calls. In particular, at launch it must make (makes) a call to the endpoint of the api getStatus(), in order to obtain the status of the device and to be able to set the UI correctly. 

![Algorithm schema](./images/4.png)
Figure 4: Phase 2 sequence diagram (connecting to the broker)

While it has to contact the API changeStatusON() and changeStatusOFF() when a user wants to change the state of the Sonoff.
 
 ![Algorithm schema](./images/5.png)
Figura 5: Diagramma di sequenza della fase 2 (cambio di stato)

# Fase 3

For the third phase, the backend, which accepts incoming REST calls and contacts the gateway always through REST calls, has been developed.
The backend has been developed using Java with the Spring framework and exposes three APIs:
» getStatus(), this API contacts the getStatus() API exposed by the gateway and finally returns the status of the Sonoff; 

 ![Algorithm schema](./images/6.png)
Figure 6: Step 3 sequence diagram (connecting to the broker)

» changestatusON(), this API contacts the changestatusON() API exposed by the gateway and returns a ResponseEntity;
» changestatusOFF(), this API contacts the changestatusOFF() API exposed by the gateway and returns a ResponseEntity.
 
![Algorithm schema](./images/7.png)
Figure 7: Phase 3 sequence diagram (state change)

Moreover, at this stage the app has been modified, adding a button that allows the user to request access to the area protected by the Sonoff. When the user requests access, the app contacts the backend via a REST call, the backend, again via REST API, contacts the gateway. 

![Algorithm schema](./images/8.png)
Figure 8: Phase 3 diagram (access request)
 
# Fase 4

In the fourth phase, the push notification system has been implemented through the Cloud Messaging and Cloud Firestore services, offered by Google's Firebase platform.
When the app is launched for the first time, the FCM SDK generates a registration token for the client app instance. The token allows the platform to identify a device on which the app is installed and must be saved to ensure that it is possible to send notifications to that specific instance of the app. 
 ![Algorithm schema](./images/9.png)
Figure 9: Phase 4 sequence diagram (first launch of the app)

The gateway, at launch, connects to the broker and subscribes to the topic "stat/tasmota_8231A8/POWER1" to intercept changes in Sonoff status. When a message arrives on this topic, the gateway queries the Cloud Firestore database (in order to obtain the tokens that identify the devices on which the app has been installed), builds the notification body, which contains the status of the Sonoff, and sends it in Multicast to the devices registered through the tokens. 
The Android app, upon receipt of the push notification, shows the notification to the user and makes the necessary changes to the UI to correctly show the current status of the Sonoff Mini. 
  ![Algorithm schema](./images/10.png)
Figure 10: Step 4 sequence diagram (push notification)

# Fase 5

In the last phase, a user authentication system has been implemented through the Authentication service offered by Google's Firebase platform.
At startup, the app shows the login Activity. After authentication, the user is redirected to the Activity that allows them to send commands to the Sonoff and to request access to the protected area. 
There is no Activity that allows users to register independently, rather a dashboard has been implemented, reserved for users defined as administrators, which allows them to add new users by entering their email, assigning them a password and the role they play. 
  ![Algorithm schema](./images/11.png)
Figure 11: Diagram of the sequence of phase 5 (login and creation of new accounts)
