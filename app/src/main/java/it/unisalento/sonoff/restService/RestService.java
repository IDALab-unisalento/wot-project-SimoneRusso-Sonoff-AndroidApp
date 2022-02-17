package it.unisalento.sonoff.restService;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.Credential;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.view.LoginActivity;
import it.unisalento.sonoff.view.MainActivity;

@SuppressLint({"HardwareIds", "UseSwitchCompatOrMaterialCode"})
public class RestService {
    //TODO: indirizzi ip
    //CASA
    //String address = "http://192.168.1.100:8082";
    //STUDIUM
    //String address = "http://10.20.72.9:8082";
    //HOTSPOT
    private String ip = "10.3.141.130";

    String address = "http://"+ip+":8082";
    String clientId;

    public RestService(Context context, int fragment) {
        AndroidNetworking.initialize(context);
        clientId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)+fragment;
    }

    public RestService(Context context){
        AndroidNetworking.initialize(context);
        clientId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public void getStatus(ToggleButton toggleButton, User user, int input){
        AndroidNetworking.post(address+"/getStatus/"+clientId+"/"+input)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("Rest (getStatus()):", "stato corrente " + response);
                        try {
                            String status = String.valueOf(response.get("status"));
                            toggleButton.setChecked(status.equals("ON"));
                        } catch (JSONException e) {
                            e.getMessage();
                        }
                        try {
                            JSONObject jsonUser = (JSONObject) response.get("user");
                            user.setToken(jsonUser.getString("token"));
                            user.setRefreshToken(jsonUser.getString("refreshToken"));
                            user.notify();
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", "stato corrente message:" + anError.getErrorDetail());

                    }
                });

    }
    public void getStatus(TextView textView, User user, int input){
        AndroidNetworking.post(address+"/getStatus/"+clientId+"/"+input)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("Rest (getStatus()):", "stato corrente " + response);
                        textView.setVisibility(View.VISIBLE);
                        try {
                            String status = String.valueOf(response.get("status"));
                            if(status.equals("ON")) {
                                textView.setText(R.string.access_ok);
                                textView.setTextColor(Color.parseColor("#417A00"));
                            }
                            else if(status.equals("OFF")){
                                textView.setText(R.string.access_deny);
                                textView.setTextColor(Color.RED);
                            }
                        } catch (JSONException e) {
                        }
                        try {
                            JSONObject jsonUser = (JSONObject) response.get("user");
                            user.setToken(jsonUser.getString("token"));
                            user.setRefreshToken(jsonUser.getString("refreshToken"));
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", "stato corrente message:" + anError.getErrorBody());
                        Log.e("Rest (getStatus()):", "stato corrente body:" + anError.getErrorBody());
                    }
                });
    }


    public void changeStatusON(CompoundButton toggleButton, User user, int input) {
        AndroidNetworking.post(address+"/changeStatusON/"+clientId+"/"+input)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        toggleButton.setChecked(true);
                        try {
                            JSONObject jsonUser = (JSONObject) response.get("user");
                            user.setToken(jsonUser.getString("token"));
                            user.setRefreshToken(jsonUser.getString("refreshToken"));
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", "stato corrente message:" + anError.getErrorCode());
                        Log.e("Rest (getStatus()):", "stato corrente message:" + anError.getErrorDetail());

                        if(anError.getErrorCode()==401){

                        }
                        else
                            toggleButton.setChecked(false);
                    }
                });

    }

    public void changeStatusOFF(CompoundButton toggleButton, User user, int input) {
        AndroidNetworking.post(address+"/changeStatusOFF/"+clientId+"/"+input)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        toggleButton.setChecked(false);
                        try {
                            JSONObject jsonUser = (JSONObject) response.get("user");
                            user.setToken(jsonUser.getString("token"));
                            user.setRefreshToken(jsonUser.getString("refreshToken"));
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", "stato corrente message:" + anError.getErrorCode());
                        Log.e("Rest (getStatus()):", "stato corrente message:" + anError.getErrorDetail());
                        if(anError.getErrorCode()==401){

                        }
                        else
                            toggleButton.setChecked(true);
                    }
                });
    }

    public void getAccessToken(LoginActivity activity, ProgressDialog progress, String username, String password){
        Credential credential = new Credential(username, password);

        AndroidNetworking.post(address+"/auth")
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(credential)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User user = new User();
                        try {
                            user.setUsername((String) response.get("username"));
                            user.setRole((String) response.get("role"));
                            user.setToken((String) response.get("token"));
                            user.setRefreshToken((String) response.get("refreshToken"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("user", user);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getMessage());
                        progress.dismiss();

                    }
                });
    }

    public void createUser(String username, String password, String role, User user, ProgressDialog progress, TextView tvErDash) {
        AndroidNetworking.post(address+"/createUser/"+username+"/"+password+"/"+role)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(user)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        tvErDash.setVisibility(View.VISIBLE);
                        tvErDash.setText("Operazione completata");
                    }

                    @Override
                    public void onError(ANError anError) {
                        progress.dismiss();
                        tvErDash.setVisibility(View.VISIBLE);
                        tvErDash.setText("Si è verificat un errore");
                    }
                });
    }
}
