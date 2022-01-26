package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.HashMap;
import java.util.Map;
import it.unisalento.sonoff.R;
import it.unisalento.sonoff.view.DashboardActivity;

@SuppressWarnings("ConstantConditions")
public class DashboardListener implements View.OnClickListener {
    @SuppressWarnings("FieldMayBeFinal")
    private DashboardActivity activity;


    public DashboardListener(DashboardActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
            if(view.getId() == R.id.btnAddUser) {
                if (activity.getEtNewEmail().getText().toString().length() != 0 && activity.getEtNewPwd().getText().toString().length() != 0 && activity.getEtRole().getText().toString().length() != 0) {
                    ProgressDialog progress = new ProgressDialog(activity);
                    progress.setTitle("Loading");
                    progress.setMessage("Operazione in corso...");
                    progress.setCancelable(false);
                    progress.show();
                    createUser(progress);
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

    private void createUser(ProgressDialog progress) {

    }

    private void addUserToDb(ProgressDialog progress) {
    }


}
