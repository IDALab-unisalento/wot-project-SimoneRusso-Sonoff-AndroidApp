package it.unisalento.sonoff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPwd;
    private Button btnLogin;
    private TextView tvErLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPwd = findViewById(R.id.etPwd);
        tvErLog = findViewById(R.id.tvErLog);
        btnLogin = findViewById(R.id.btnLogin);
        Listener listener = new Listener(this);
        btnLogin.setOnClickListener(listener);
    }

    public EditText getEtEmail() {
        return etEmail;
    }

    public void setEtEmail(EditText etEmail) {
        this.etEmail = etEmail;
    }

    public EditText getEtPwd() {
        return etPwd;
    }

    public void setEtPwd(EditText etPwd) {
        this.etPwd = etPwd;
    }

    public TextView getTvErLog() {
        return tvErLog;
    }

    public void setTvErLog(TextView tvErLog) {
        this.tvErLog = tvErLog;
    }
}