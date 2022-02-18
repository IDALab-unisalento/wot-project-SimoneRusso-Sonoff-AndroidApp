package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.view.View;

import android.widget.TextView;

import org.apache.commons.lang3.RandomStringUtils;
import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.view.DashboardActivity;

public class DashboardListener implements View.OnClickListener {
    private final DashboardActivity activity;
    public DashboardListener(DashboardActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
            if(view.getId() == R.id.btnAddUser) {
                if (activity.getEtNewEmail().getText().toString().length() != 0 && activity.getEtNewPwd().getText().toString().length() != 0 && activity.getEtRole().getText().toString().length() != 0) {
                    activity.setProgressDialog(new ProgressDialog(activity));
                    activity.getProgressDialog().setTitle("Loading");
                    activity.getProgressDialog().setMessage("Operazione in corso...");
                    activity.getProgressDialog().setCancelable(false);
                    activity.getProgressDialog().show();
                    createUser(activity.getProgressDialog(), activity.getEtNewEmail().getText().toString(),
                            activity.getEtNewPwd().getText().toString(), activity.getEtRole().getText().toString());
                } else {
                    if (activity.getEtNewEmail().getText().toString().length() == 0)
                        activity.getEtNewEmail().setError("Non può essere vuoto!");
                    if (activity.getEtNewPwd().getText().toString().length() == 0)
                        activity.getEtNewPwd().setError("Non può essere vuoto!");
                    if (activity.getEtRole().getText().toString().length() == 0)
                        activity.getEtRole().setError("Non può essere vuoto!");
                }
            }
            if (view.getId() == R.id.btnRandPwd){
                String randonString = RandomStringUtils.randomAlphanumeric(16);
                activity.getEtNewPwd().setText(randonString);
            }
    }

    private void createUser(ProgressDialog progress, String username, String password, String role) {
        RestService restService = new RestService(activity);
        restService.createUser(username, password, role, progress, activity);


    }

}
