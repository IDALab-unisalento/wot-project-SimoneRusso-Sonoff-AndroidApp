package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.view.View;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.view.LoginActivity;

@SuppressWarnings({"FieldMayBeFinal", "ConstantConditions"})
public class LoginListener implements View.OnClickListener {

    private LoginActivity activity;
    private RestService restService;

    public LoginListener(LoginActivity activity) {
        this.activity = activity;
        restService = new RestService(activity.getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            ProgressDialog progress = new ProgressDialog(activity);
            progress.setTitle("Loading");
            progress.setMessage("Recupero i dati utente");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            restService.getAccessToken(activity, progress, activity.getEtUsername().getText().toString(), activity.getEtPwd().getText().toString());

        }

    }
}
