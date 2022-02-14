package it.unisalento.sonoff.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        if(user != null) {
            setContentView(R.layout.activity_main);

            Intent mymqttservice_intent = new Intent(this, MqttService.class);
            startService(mymqttservice_intent);

            toggleButton = findViewById(R.id.toggleBtn);
            tvAccess = findViewById(R.id.tvAccess);
            TextView tvDashboard = findViewById(R.id.tvDashboard);
            Button button = findViewById(R.id.btnAccess);

            MainListener listener = new MainListener(this);

            //LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(REQUEST_ACCEPT));

            RestService restService = new RestService(getApplicationContext());
            restService.getStatus(this.toggleButton, this, user);

            toggleButton.setOnCheckedChangeListener(listener);
            button.setOnClickListener(listener);

            if(user.getRole().equals("admin")){
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

    /*private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            Log.d("receiver", "Got message: " + status);
            toggleButton.setChecked(status.equals("ON"));
        }

    };*/

    public TextView getTvAccess() {
        return tvAccess;
    }

    public User getUser() {
        return user;
    }
}