package it.unisalento.sonoff.view;

import android.annotation.SuppressLint;
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

import com.google.firebase.auth.FirebaseAuth;

import it.unisalento.sonoff.listener.MainListener;
import it.unisalento.sonoff.R;
import it.unisalento.sonoff.restService.RestService;

@SuppressWarnings("FieldMayBeFinal")
public class MainActivity extends AppCompatActivity{

    private ToggleButton toggleButton;
    private TextView tvAccess;
    private static final String REQUEST_ACCEPT = "Notification";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_main);

            toggleButton = findViewById(R.id.toggleBtn);
            tvAccess = findViewById(R.id.tvAccess);
            TextView tvDashboard = findViewById(R.id.tvDashboard);
            Button button = findViewById(R.id.btnAccess);

            MainListener listener = new MainListener(this);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(REQUEST_ACCEPT));

            RestService restService = new RestService(getApplicationContext());
            restService.getStatus(this.toggleButton);

            toggleButton.setOnCheckedChangeListener(listener);
            button.setOnClickListener(listener);

            String role = getIntent().getStringExtra("role");
            if(role.equals("admin")){
                tvDashboard.setVisibility(View.VISIBLE);
                tvDashboard.setClickable(true);
                tvDashboard.setOnClickListener(listener);
            }
        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
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

    public TextView getTvAccess() {
        return tvAccess;
    }
}