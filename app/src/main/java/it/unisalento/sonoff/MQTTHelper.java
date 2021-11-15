package it.unisalento.sonoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import android.provider.Settings.Secure;


public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;

    private final String brokerAddress = "tcp://192.168.1.67:1883";
    private final String statTopic= "stat/tasmota_8231A8/POWER1";
    private final String cmdTopic = "cmnd/tasmota_8231A8/Power1";

    @SuppressLint("HardwareIds")
    public MQTTHelper(Context context){
        String clientId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        mqttAndroidClient = new MqttAndroidClient(context, brokerAddress, clientId);
        Log.d("MQTTHelper constructor", "Client created: " + clientId);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT connect", "connected succesfully");
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT connect", "Failed to connect to: " + brokerAddress + exception.toString());
                }
            });
        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(statTopic, 2, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT subscibe","Subscribed!");
                    Log.d("MQTT subscibe","Trying to get status...");
                    publish(cmdTopic, "");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT subscribe", "Subscribe failed!");
                }
            });

        } catch (MqttException ex) {
            Log.e("MQTT subscribe", "Exception while subscribing");
            ex.printStackTrace();
        }
    }

    public void publish(String topic, String message){
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        try {
            mqttAndroidClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT publish", "Meesage sent correctly, token" + asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT publish", "Failed to sent meesage, token:" + asyncActionToken);

                }
            });
        } catch (MqttException e) {
            Log.e("MQTT publish", "Exception while publishing");
            e.printStackTrace();
        }
    }

}