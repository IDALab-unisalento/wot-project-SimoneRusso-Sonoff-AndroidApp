package it.unisalento.sonoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity{

    private Switch lockSwitch;
    private Button button;
    private TextView tvAccess;
    private TextView tvDashboard;
    private static final String REQUEST_ACCEPT = "Notification";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_main);

            lockSwitch = findViewById(R.id.lockSwitch);
            button = findViewById(R.id.btnAccess);
            tvAccess = findViewById(R.id.tvAccess);
            tvDashboard = findViewById(R.id.tvDashboard);

            String role = getIntent().getStringExtra("role");
            if(role.equals("admin")){
                tvDashboard.setVisibility(View.VISIBLE);
                tvDashboard.setClickable(true);
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(REQUEST_ACCEPT));

            RestService restService = new RestService(getApplicationContext());
            restService.getStatus(this.lockSwitch);

            Listener listener = new Listener(this);

            lockSwitch.setOnCheckedChangeListener(listener);
            button.setOnClickListener(listener);
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
            lockSwitch.setChecked(status.equals("ON"));
        }

    };

    public TextView getTvAccess() {
        return tvAccess;
    }

    public void setTvAccess(TextView tvAccess) {
        this.tvAccess = tvAccess;
    }
}