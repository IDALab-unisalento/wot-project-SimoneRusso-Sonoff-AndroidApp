package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.listener.DashboardListener;
import it.unisalento.sonoff.model.User;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvErDash;
    private EditText etNewEmail;
    private EditText etNewPwd;
    private EditText etRole;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        user = getIntent().getParcelableExtra("user");

        if(user != null) {
            tvErDash = findViewById(R.id.tvErDash);
            etNewEmail = findViewById(R.id.etNewEmail);
            etNewPwd = findViewById(R.id.etNewPwd);
            etRole = findViewById(R.id.etRole);
            Button btnAddUser = findViewById(R.id.btnAddUser);
            ImageButton btnRandPwd = findViewById(R.id.btnRandPwd);

            DashboardListener listener = new DashboardListener(this);

            btnAddUser.setOnClickListener(listener);
            btnRandPwd.setOnClickListener(listener);

        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public EditText getEtNewEmail() {
        return etNewEmail;
    }

    public EditText getEtNewPwd() {
        return etNewPwd;
    }

    public TextView getTvErDash() {
        return tvErDash;
    }

    public EditText getEtRole() {
        return etRole;
    }

    public User getUser() {
        return user;
    }
}