package it.unisalento.sonoff.restService;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    //String address = "http://192.168.1.100:8082";
    String address = "http://10.3.141.130:8082";
    String clientId;

    public RestService(Context context) {
        AndroidNetworking.initialize(context);
        clientId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void getInitialState(MainActivity activity){
        AndroidNetworking.get(address+"/getStatus/"+clientId+"/"+activity.getUser().getToken())
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Rest (getStatus()):", "stato corrente " + response);
                        activity.getToggleButton().setChecked(response.equals("ON"));
                        activity.getProgressDialog().dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", anError.toString());
                        Log.e("Rest (getStatus()):", anError.getErrorBody());
                        if(anError.getErrorCode()==401){
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.finish();
                            activity.startActivity(intent);
                        }
                    }
                });
    }

    public void getState(MainActivity activity){
        AndroidNetworking.get(address+"/getStatus/"+clientId+"/"+activity.getUser().getToken())
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Rest (getStatus()):", "stato corrente " + response);
                        activity.getTvAccess().setVisibility(View.VISIBLE);

                        if(response.equals("ON")) {
                            activity.getTvAccess().setText(R.string.access_ok);
                            activity.getTvAccess().setTextColor(Color.GREEN);
                        }
                        else if(response.equals("OFF")){
                            activity.getTvAccess().setText(R.string.access_deny);
                            activity.getTvAccess().setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (getStatus()):", anError.toString());
                        if(anError.getErrorCode()==401){
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.finish();
                            activity.startActivity(intent);
                        }
                        else {
                            activity.getTvAccess().setText(R.string.access_ok);
                            activity.getTvAccess().setTextColor(Color.parseColor("#417A00"));
                            activity.getTvAccess().setVisibility(View.VISIBLE);
                        }

                    }
                });
    }


    public void changeStatusON(MainActivity activity) {
        AndroidNetworking.get(address+"/changeStatusON/"+clientId+"/"+activity.getUser().getToken())
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (changeStatus()):", "status changed" + response);
                        activity.getTvAccess().setText("");
                        activity.getTvAccess().setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        if(anError.getErrorCode()==401){
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.finish();
                            activity.startActivity(intent);
                        }
                        else
                            activity.getToggleButton().setChecked(false);
                    }
                });
    }

    public void changeStatusOFF(MainActivity activity) {
        AndroidNetworking.get(address+"/changeStatusOFF/"+clientId+"/"+activity.getUser().getToken())
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Rest (changeStatus()):", "status changed " + response);
                        activity.getTvAccess().setText("");
                        activity.getTvAccess().setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        if(anError.getErrorCode()==401){
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.finish();
                            activity.startActivity(intent);
                        }
                        else
                            activity.getToggleButton().setChecked(true);
                    }
                });
    }

    public void getAccessToken(LoginActivity activity){
        Credential credential = new Credential(activity.getEtUsername().getText().toString(), activity.getEtPwd().getText().toString());

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
                            user.setRefreshTken((String) response.get("refreshToken"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        activity.getProgressDialog().dismiss();
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("user", user);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getMessage());
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
                        tvErDash.setText(R.string.operation_completed);
                    }

                    @Override
                    public void onError(ANError anError) {
                        tvErDash.setText(R.string.error);
                    }
                });
    }
}
