package it.unisalento.sonoff.listener;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.view.DashboardActivity;
import it.unisalento.sonoff.view.MainActivity;

public class MainListener implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private RestService restService;
    private MainActivity mainActivity;

    public MainListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        restService = new RestService(mainActivity.getApplicationContext());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(compoundButton.isPressed()){
            if(compoundButton.isChecked())
                restService.changeStatusON(compoundButton, mainActivity.getTvAccess());
            else if (!compoundButton.isChecked())
                restService.changeStatusOFF(compoundButton, mainActivity.getTvAccess());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAccess:
                restService.getStatus(mainActivity.getTvAccess());
                break;
            case R.id.tvDashboard:
                Intent intent = new Intent(mainActivity, DashboardActivity.class);
                mainActivity.startActivity(intent);
                break;
        }
    }
}
