package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.view.View;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.view.LoginActivity;

public class LoginListener implements View.OnClickListener {

    private final LoginActivity activity;
    private final RestService restService;

    public LoginListener(LoginActivity activity) {
        this.activity = activity;
        restService = new RestService(activity.getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            activity.setProgressDialog(new ProgressDialog(activity));
            activity.getProgressDialog().setTitle("Loading");
            activity.getProgressDialog().setMessage("Recupero i dati utente");
            activity.getProgressDialog().setCancelable(false); // disable dismiss by tapping outside of the dialog
            activity.getProgressDialog().show();
            restService.authentication(activity);

        }

    }
}
