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
import it.unisalento.sonoff.utils.ToastRunnable;
import it.unisalento.sonoff.view.DashboardActivity;
import it.unisalento.sonoff.view.LoginActivity;
import it.unisalento.sonoff.view.MainActivity;

@SuppressLint({"HardwareIds", "UseSwitchCompatOrMaterialCode"})
public class RestService {
    //String address = "http://192.168.1.100:8082";
    String address = "http://10.3.141.130:8082";
    String clientId;
    Context context;

    public RestService(Context context) {
        this.context=context;
        AndroidNetworking.initialize(context);
        clientId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void getInitialState(MainActivity activity){
        AndroidNetworking.post(address+"/getStatus1/"+clientId)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(activity.getUser())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("Rest(getInitialState():", "stato corrente " + response);
                        try {
                            String status = response.getString("status");
                            activity.getToggleButton().setChecked(status.equals("ON"));
                            activity.getProgressDialog().dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonUser = (JSONObject) response.get("user");
                            activity.getUser().setToken(jsonUser.getString("token"));
                            activity.getUser().setRefreshToken(jsonUser.getString("refreshToken"));
                            //activity.getUser().notify();
                        } catch (JSONException e) {
                        }
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
        AndroidNetworking.post(address+"/getStatus1/"+clientId)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(activity.getUser())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("Rest (getState()):", "stato corrente " + response);
                        try {
                            String status = response.getString("status");
                            activity.getTvAccess().setVisibility(View.VISIBLE);
                            if(status.equals("ON")) {
                                activity.getTvAccess().setText(R.string.access_ok);
                                activity.getTvAccess().setTextColor(Color.GREEN);
                            }
                            else if(status.equals("OFF")){
                                activity.getTvAccess().setText(R.string.access_deny);
                                activity.getTvAccess().setTextColor(Color.RED);
                            }                } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonUser = (JSONObject) response.get("user");
                            activity.getUser().setToken(jsonUser.getString("token"));
                            activity.getUser().setRefreshToken(jsonUser.getString("refreshToken"));
                            //activity.getUser().notify();
                        } catch (JSONException e) {
                        }
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
                        else {
                            activity.getTvAccess().setVisibility(View.GONE);
                            new ToastRunnable("Qualcosa è andato storto, riprova", 500, context);
                        }
                    }
                });
    }


    public void changeStatusON(MainActivity activity) {
        AndroidNetworking.post(address+"/changeStatusON/"+clientId)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(activity.getUser())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Rest (changeStatus()):", "status changed" + response);
                        activity.getTvAccess().setText("");
                        activity.getTvAccess().setVisibility(View.GONE);
                        activity.getToggleButton().setChecked(true);
                        try {
                            if(!response.getString("token").equals("null")) {
                                activity.getUser().setToken(response.getString("token"));
                                activity.getUser().setRefreshToken(response.getString("refreshToken"));
                            }
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        if(anError.getErrorCode()==401){
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.finish();
                            activity.startActivity(intent);
                        }
                        else{
                            new ToastRunnable("Qualcosa è andato storto, riprova", 500, context);
                        }
                        activity.getToggleButton().setChecked(false);

                    }
                });
    }

    public void changeStatusOFF(MainActivity activity) {
        AndroidNetworking.post(address+"/changeStatusOFF/"+clientId)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(activity.getUser())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Rest (changeStatus()):", "status changed" + response);
                        activity.getTvAccess().setText("");
                        activity.getTvAccess().setVisibility(View.GONE);
                        activity.getToggleButton().setChecked(false);
                        try {
                            if(!response.getString("token").equals("null")) {
                                activity.getUser().setToken(response.getString("token"));
                                activity.getUser().setRefreshToken(response.getString("refreshToken"));
                            }
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Rest (changeStatus()):", anError.toString());
                        if(anError.getErrorCode()==401){
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.finish();
                            activity.startActivity(intent);
                        }
                        else{
                            new ToastRunnable("Qualcosa è andato storto, riprova", 500, context);
                        }
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
                            user.setRefreshToken((String) response.get("refreshToken"));
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
                        Log.e("createuser:", "onError: ", anError);
                        activity.getTvErLog().setVisibility(View.VISIBLE);
                        activity.getTvErLog().setTextColor(Color.RED);
                        activity.getTvErLog().setText("Username o password errati");
                    }
                });
    }

    public void createUser(String username, String password, String role, ProgressDialog progress, DashboardActivity activity) {
        AndroidNetworking.post(address+"/createUser/"+username+"/"+password+"/"+role)
                .setPriority(Priority.LOW)
                .addApplicationJsonBody(activity.getUser())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        activity.getEtRole().setText("");
                        activity.getEtNewPwd().setText("");
                        activity.getEtNewEmail().setText("");
                        activity.getTvErDash().setText(R.string.operation_completed);
                        activity.getTvErDash().setVisibility(View.VISIBLE);
                        try {
                            if(response.getString("token")!="null") {
                                activity.getUser().setToken((String) response.get("token"));
                                activity.getUser().setRefreshToken((String) response.get("refreshToken"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        activity.getTvErDash().setText(R.string.error);
                        activity.getTvErDash().setVisibility(View.VISIBLE);
                    }
                });
    }
}
