package it.unisalento.sonoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity{

    Switch lockSwitch;
    Button button;
    TextView textView;
    private static final String NOTIFICATION_RECEIVED = "NOTIFICATION_RECEIVED";
    private static final String NOTIFICATION_ELABORATED = "NOTIFICATION_ELABORATED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NOTIFICATION_RECEIVED));
        lockSwitch = findViewById(R.id.lockSwitch);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        RestService restService = new RestService(getApplicationContext());
        restService.getStatus(this.lockSwitch);


        Listener listener = new Listener(getApplicationContext(), textView);

        lockSwitch.setOnCheckedChangeListener(listener);
        button.setOnClickListener(listener);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
            Intent intentToFCM = new Intent(NOTIFICATION_ELABORATED);
            if(lockSwitch.isChecked() && status.equals("ON")){
                intentToFCM.putExtra("show", false);
            }
            else if(!lockSwitch.isChecked() && status.equals("OFF")){
                intentToFCM.putExtra("show", false);
            }
            else{
                intentToFCM.putExtra("show", true);
                lockSwitch.setChecked(status.equals("ON"));
            }
            broadcaster.sendBroadcast(intent);
        }

    };

}