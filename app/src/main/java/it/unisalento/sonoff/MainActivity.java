package it.unisalento.sonoff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Switch lockSwitch;
    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lockSwitch = findViewById(R.id.lockSwitch);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        RestService restService = new RestService(getApplicationContext());
        restService.getStatus(this.lockSwitch);

        Listener listener = new Listener(getApplicationContext(), textView);

        lockSwitch.setOnCheckedChangeListener(listener);
        button.setOnClickListener(listener);

    }

}