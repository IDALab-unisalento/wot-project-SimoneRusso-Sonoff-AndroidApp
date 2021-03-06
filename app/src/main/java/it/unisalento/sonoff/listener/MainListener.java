package it.unisalento.sonoff.listener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.view.DashboardActivity;
import it.unisalento.sonoff.view.MainActivity;

public class MainListener implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final RestService restService;
    private final MainActivity mainActivity;

    public MainListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        restService = new RestService(mainActivity.getApplicationContext());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(compoundButton.isPressed()){
            if(compoundButton.isChecked())
                restService.changeStatusON(mainActivity);
            else if (!compoundButton.isChecked())
                restService.changeStatusOFF(mainActivity);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnAccess) {
            restService.getState(mainActivity);
        }
        if(view.getId() == R.id.tvDashboard){
            Intent intent = new Intent(mainActivity, DashboardActivity.class);
            intent.putExtra("user", mainActivity.getUser());
            mainActivity.startActivityForResult(intent, Activity.RESULT_OK);

        }
    }
}
