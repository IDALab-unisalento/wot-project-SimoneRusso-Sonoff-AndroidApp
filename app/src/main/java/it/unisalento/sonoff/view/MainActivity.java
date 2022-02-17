package it.unisalento.sonoff.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import it.unisalento.sonoff.listener.MainListener;
import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.service.MqttService;

@SuppressWarnings("FieldMayBeFinal")
public class MainActivity extends AppCompatActivity{

    private ToggleButton toggleButton;
    private TextView tvAccess;
    private static final String REQUEST_ACCEPT = "Notification";
    private User user;
    private ProgressDialog progressDialog;
    private Intent mymqttservice_intent;
    private TextView tvDashboard;
    private Button button;
    private MainListener mainListener;
    private RestService restService;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        if(user != null) {
            setContentView(R.layout.activity_main);

            mymqttservice_intent = new Intent(this, MqttService.class);
            startService(mymqttservice_intent);

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Recupero lo stato degli ingressi");
            progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressDialog.show();

            toggleButton = findViewById(R.id.toggleBtn);
            tvAccess = findViewById(R.id.tvAccess);
            tvDashboard = findViewById(R.id.tvDashboard);
            button = findViewById(R.id.btnAccess);

            mainListener = new MainListener(this);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(REQUEST_ACCEPT));

            restService = new RestService(getApplicationContext());
            restService.getInitialState(this);

            toggleButton.setOnCheckedChangeListener(mainListener);
            button.setOnClickListener(mainListener);

            if(user.getRole().equals("admin")){
                tvDashboard.setVisibility(View.VISIBLE);
                tvDashboard.setClickable(true);
                tvDashboard.setOnClickListener(mainListener);
            }
        }
        else{
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            Log.d("receiver", "Got message: " + status);
            toggleButton.setChecked(status.equals("ON"));
        }

    };

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public TextView getTvAccess() {
        return tvAccess;
    }

    public User getUser() {
        return user;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }
    
}