package it.unisalento.sonoff;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

public class RestService {
    String address = "http://192.168.1.67:8081";
    String clientId;

    public RestService(Context context) {
        AndroidNetworking.initialize(context);
        clientId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void getStatus(Switch switcher){
        AndroidNetworking.get(address+"/getStatus/"+clientId)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (getStatus()):", "stato corrente " + response);
                        switcher.setChecked(response.equals("ON"));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", anError.toString());
                    }
                });
    }

    public void changeStatusON(CompoundButton switcher) {
        AndroidNetworking.get(address+"/changeStatusON")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (changeStatus()):", "stato corrente " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        switcher.setChecked(false);
                    }
                });
    }

    public void changeStatusOFF(CompoundButton switcher) {
        AndroidNetworking.get(address+"/changeStatusOFF")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (changeStatus()):", "stato corrente " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        switcher.setChecked(true);
                    }
                });


    }

}
