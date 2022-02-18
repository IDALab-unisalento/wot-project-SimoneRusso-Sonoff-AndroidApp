package it.unisalento.sonoff.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.utils.ToastRunnable;
import it.unisalento.sonoff.view.MainActivity;


@SuppressWarnings("FieldCanBeLocal")
public class MqttService extends Service {
    private final String ip = "10.3.141.130";
    private final String port = "1883";
    private final IBinder mBinder = new LocalBinder();
    private Handler mHandler;
    private ArrayList<Integer> idsNot = new ArrayList();
    private static final String REQUEST_ACCEPT = "Notification";

    private static final String TAG = "mqttservice";
    private static boolean hasWifi = false;
    private static boolean hasMmobile = false;
    private ConnectivityManager mConnMan;
    private volatile IMqttAsyncClient mqttClient;
    private String uniqueID;


    class MQTTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            IMqttToken token;
            boolean hasConnectivity = false;
            boolean hasChanged = false;
            NetworkInfo infos[] = mConnMan.getAllNetworkInfo();
            for (int i = 0; i < infos.length; i++) {
                if (infos[i].getTypeName().equalsIgnoreCase("MOBILE")) {
                    if ((infos[i].isConnected() != hasMmobile)) {
                        hasChanged = true;
                        hasMmobile = infos[i].isConnected();
                    }
                    Log.d(TAG, infos[i].getTypeName() + " is " + infos[i].isConnected());
                } else if (infos[i].getTypeName().equalsIgnoreCase("WIFI")) {
                    if ((infos[i].isConnected() != hasWifi)) {
                        hasChanged = true;
                        hasWifi = infos[i].isConnected();
                    }
                    Log.d(TAG, infos[i].getTypeName() + " is " + infos[i].isConnected());
                }
            }
            hasConnectivity = hasMmobile || hasWifi;
            Log.v(TAG, "hasConn: " + hasConnectivity + " hasChange: " + hasChanged + " - " + (mqttClient == null || !mqttClient.isConnected()));
            if (hasConnectivity && hasChanged && (mqttClient == null || !mqttClient.isConnected())) {
                doConnect();

            }


        }
    }


    public class LocalBinder extends Binder {
        public MqttService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MqttService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void publish(String topic, MqttMessage message) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);// we create a 'shared" memory where we will share our preferences for the limits and the values that we get from onsensorchanged
        try {

            mqttClient.publish(topic, message);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate() {

        mHandler = new Handler();//for toasts
        IntentFilter intentf = new IntentFilter();
        setClientID();
        intentf.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new MQTTBroadcastReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged()");
        android.os.Debug.waitForDebugger();
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");

    }


    private void setClientID() {
        uniqueID = "ANDROID:"+android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        Log.d(TAG, "uniqueID=" + uniqueID);

    }


    private void doConnect() {
        String broker = "tcp://" + ip + ":" + port;
        Log.d(TAG, "mqtt_doConnect()");
        IMqttToken token;
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setMaxInflight(100);//handle more messages!!so as not to disconnect
        options.setAutomaticReconnect(true);
        try {
            mqttClient = new MqttAsyncClient(broker, uniqueID, new MemoryPersistence());
            token = mqttClient.connect(options);
            token.waitForCompletion(3500);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    try {
                        mqttClient.disconnectForcibly();
                        mqttClient.connect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    Log.i(TAG, "Message arrived from topic " + topic);
                    Log.i(TAG, msg.toString());
                    showNotification("Cambio di stato", msg.toString());
                    LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
                    Intent intent = new Intent(REQUEST_ACCEPT);
                    intent.putExtra("status", msg.toString());
                    broadcaster.sendBroadcast(intent);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("published");
                }
            });

            mqttClient.subscribe("stat/tasmota_8231A8/POWER1" , 2);

        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            switch (e.getReasonCode()) {
                case MqttException.REASON_CODE_BROKER_UNAVAILABLE:
                    mHandler.post(new ToastRunnable("WE ARE OFFLINE BROKER_UNAVAILABLE!", 1500, getApplicationContext()));
                    break;
                case MqttException.REASON_CODE_CLIENT_TIMEOUT:
                    mHandler.post(new ToastRunnable("WE ARE OFFLINE CLIENT_TIMEOUT!", 1500, getApplicationContext()));
                    break;
                case MqttException.REASON_CODE_CONNECTION_LOST:
                    mHandler.post(new ToastRunnable("WE ARE OFFLINE CONNECTION_LOST!", 1500, getApplicationContext()));
                    break;
                case MqttException.REASON_CODE_SERVER_CONNECT_ERROR:
                    Log.v(TAG, "c" + e.getMessage());
                    e.printStackTrace();
                    break;
                case MqttException.REASON_CODE_FAILED_AUTHENTICATION:
                    Intent i = new Intent("RAISEALLARM");
                    i.putExtra("ALLARM", e);
                    Log.e(TAG, "b" + e.getMessage());
                    break;
                default:
                    Log.e(TAG, "a" + e.getMessage());
            }
        }
        mHandler.post(new ToastRunnable("WE ARE ONLINE!", 500, getApplicationContext()));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        return START_STICKY;
    }

    private void showNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Sonoff", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        if(idsNot.isEmpty()){
            idsNot.add(0);
        }
        else{
            idsNot.add(idsNot.size());
        }
        notificationManager.notify(idsNot.get(idsNot.size()-1) , notificationBuilder.build());
    }
}