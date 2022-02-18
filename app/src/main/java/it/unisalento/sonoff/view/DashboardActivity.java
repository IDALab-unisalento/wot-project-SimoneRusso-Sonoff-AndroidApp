package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    private Button btnAddUser;
    private ImageButton btnRandPwd;
    private DashboardListener dashboardListener;
    private Intent intent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        user = (User) getIntent().getSerializableExtra("user");

        if(user != null) {
            tvErDash = findViewById(R.id.tvErDash);
            etNewEmail = findViewById(R.id.etNewEmail);
            etNewPwd = findViewById(R.id.etNewPwd);
            etRole = findViewById(R.id.etRole);
            btnAddUser = findViewById(R.id.btnAddUser);
            btnRandPwd = findViewById(R.id.btnRandPwd);

            dashboardListener = new DashboardListener(this);

            btnAddUser.setOnClickListener(dashboardListener);
            btnRandPwd.setOnClickListener(dashboardListener);

        }
        else{
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(1, intent);
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

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}