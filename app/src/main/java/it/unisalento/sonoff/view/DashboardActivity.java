package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.listener.DashboardListener;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvErDash;
    private EditText etNewEmail;
    private EditText etNewPwd;
    private EditText etRole;
    private Button btnAddUser;
    private ImageButton btnRandPwd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            tvErDash = findViewById(R.id.tvErDash);
            etNewEmail = findViewById(R.id.etNewEmail);
            etNewPwd = findViewById(R.id.etNewPwd);
            etRole = findViewById(R.id.etRole);
            btnAddUser = findViewById(R.id.btnAddUser);
            btnRandPwd = findViewById(R.id.btnRandPwd);

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

    public void setEtNewEmail(EditText etNewEmail) {
        this.etNewEmail = etNewEmail;
    }

    public EditText getEtNewPwd() {
        return etNewPwd;
    }

    public void setEtNewPwd(EditText etNewPwd) {
        this.etNewPwd = etNewPwd;
    }

    public TextView getTvErDash() {
        return tvErDash;
    }

    public void setTvErDash(TextView tvErDash) {
        this.tvErDash = tvErDash;
    }

    public EditText getEtRole() {
        return etRole;
    }

    public void setEtRole(EditText etRole) {
        this.etRole = etRole;
    }
}