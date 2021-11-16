package it.unisalento.sonoff;

import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;


public class RestService {
    String address = "http://172.20.10.4:8082";
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
                        Log.w("Rest (getStatus()):", "stato corrente " + response);
                        switcher.setChecked(response.equals("ON"));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", anError.toString());
                    }
                });
    }
    public void getStatus(TextView textView){
        AndroidNetworking.get(address+"/getStatus/"+clientId)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Rest (getStatus()):", "stato corrente " + response);
                        if(response.equals("ON")) {
                            textView.setText("Accesso consentito");
                            textView.setTextColor(Color.GREEN);
                        }
                        else{
                            textView.setText("Accesso non consentito");
                            textView.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", anError.toString());
                    }
                });
    }


    public void changeStatusON(CompoundButton switcher, TextView textView) {
        AndroidNetworking.get(address+"/changeStatusON/"+clientId)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (changeStatus()):", "status changed" + response);
                        textView.setText("");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        switcher.setChecked(false);
                    }
                });
    }

    public void changeStatusOFF(CompoundButton switcher, TextView textView) {
        AndroidNetworking.get(address+"/changeStatusOFF/"+clientId)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (changeStatus()):", "status changed " + response);
                        textView.setText("");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        switcher.setChecked(true);
                    }
                });


    }

    public void saveToken(String token) {
        AndroidNetworking.post(address+"/saveToken")
                .addBodyParameter("token", token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                    @Override
                    public void onError(ANError error) {
                    }
                });
    }
}
