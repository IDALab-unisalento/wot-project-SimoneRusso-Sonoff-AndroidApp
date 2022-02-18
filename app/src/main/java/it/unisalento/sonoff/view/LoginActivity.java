package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.unisalento.sonoff.listener.LoginListener;
import it.unisalento.sonoff.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPwd;
    private TextView tvErLog;
    private Button btnLogin;
    private LoginListener loginListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etUsername);
        etPwd = findViewById(R.id.etPwd);
        tvErLog = findViewById(R.id.tvErLog);
        btnLogin = findViewById(R.id.btnLogin);
        loginListener = new LoginListener(this);
        btnLogin.setOnClickListener(loginListener);
    }

    public EditText getEtUsername() {
        return etUsername;
    }

    public EditText getEtPwd() {
        return etPwd;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public TextView getTvErLog() {
        return tvErLog;
    }

    public void setTvErLog(TextView tvErLog) {
        this.tvErLog = tvErLog;
    }
}