package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.unisalento.sonoff.listener.LoginListener;
import it.unisalento.sonoff.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPwd;
    private TextView tvErLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPwd = findViewById(R.id.etPwd);
        tvErLog = findViewById(R.id.tvErLog);
        Button btnLogin = findViewById(R.id.btnLogin);
        LoginListener listener = new LoginListener(this);
        btnLogin.setOnClickListener(listener);
    }

    public EditText getEtEmail() {
        return etEmail;
    }

    public EditText getEtPwd() {
        return etPwd;
    }

    public TextView getTvErLog() {
        return tvErLog;
    }
}