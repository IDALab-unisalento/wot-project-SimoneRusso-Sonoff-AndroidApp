package it.unisalento.sonoff.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
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
    private ImageView touchSensorImage;
    private ImageView pirSensorImage;
    private TextView tvAccess;
    private User user;
    private ProgressDialog progressDialog;
    private Intent mymqttservice_intent;
    private TextView tvDashboard;
    private TextView tvLogEvent;
    private Button button;
    private MainListener mainListener;
    private RestService restService;
    private Intent intent;
    private static final String STATUS_ONE = "1";
    private static final String PIR_SENSOR = "2";
    private static final String TOUCH_SENSOR = "3";

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
            tvLogEvent = findViewById(R.id.tvLogEvent);
            button = findViewById(R.id.btnAccess);
            pirSensorImage = findViewById(R.id.pirSensorImage);
            touchSensorImage = findViewById(R.id.touchSensorImage);

            mainListener = new MainListener(this);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(STATUS_ONE));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(PIR_SENSOR));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(TOUCH_SENSOR));

            restService = new RestService(getApplicationContext());
            restService.getInitialState(this);

            toggleButton.setOnCheckedChangeListener(mainListener);
            button.setOnClickListener(mainListener);
            tvLogEvent.setOnClickListener(mainListener);

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
            switch (intent.getAction()) {
                case STATUS_ONE:
                    String status = intent.getStringExtra("status");
                    Log.d("receiver", "Got message: " + status);
                    toggleButton.setChecked(status.equals("ON"));
                    break;
                case PIR_SENSOR:
                    String pir = intent.getStringExtra("pirSensor");
                    Log.d("receiver", "Got message: " + pir);
                    if (pir.equals("ON")) {
                        pirSensorImage.setImageResource(R.drawable.ic_baseline_circle_green);
                    } else {
                        pirSensorImage.setImageResource(R.drawable.ic_baseline_circle_gray);
                    }
                    break;
                case TOUCH_SENSOR:
                    String touch = intent.getStringExtra("touchSensor");
                    Log.d("receiver", "Got message: " + touch);
                    if (touch.equals("ON")) {
                        touchSensorImage.setImageResource(R.drawable.ic_baseline_circle_green);
                        if (tvAccess.getVisibility() == View.VISIBLE) {
                            tvAccess.setText(R.string.access_deny);
                            tvAccess.setTextColor(Color.RED);
                        }
                    } else {
                        touchSensorImage.setImageResource(R.drawable.ic_baseline_circle_gray);
                        if ((tvAccess.getVisibility() == View.VISIBLE)) {
                            tvAccess.setText(R.string.access_ok);
                            tvAccess.setTextColor(Color.GREEN);
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + intent.getAction());
            }

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                User u = (User) data.getSerializableExtra("user");
                user.setRefreshToken(u.getRefreshToken());
                user.setToken(u.getToken());

            }
        }

    }

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

    public ImageView getTouchSensorImage() {
        return touchSensorImage;
    }

    public ImageView getPirSensorImage() {
        return pirSensorImage;
    }
}