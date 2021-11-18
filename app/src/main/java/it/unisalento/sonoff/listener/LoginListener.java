package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import it.unisalento.sonoff.R;
import it.unisalento.sonoff.view.LoginActivity;
import it.unisalento.sonoff.view.MainActivity;

@SuppressWarnings({"FieldMayBeFinal", "ConstantConditions"})
public class LoginListener implements View.OnClickListener {

    private LoginActivity activity;
    private FirebaseAuth mAuth;

    public LoginListener(LoginActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            ProgressDialog progress = new ProgressDialog(activity);
            progress.setTitle("Loading");
            progress.setMessage("Recupero i dati utente");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            mAuth.signInWithEmailAndPassword(activity.getEtEmail().getText().toString(), activity.getEtPwd().getText().toString())
                    .addOnCompleteListener(activity, task -> {
                        //If login success
                        if (task.isSuccessful()) {
                            Log.d("Firebase Login", "signInWithEmail:success");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference usersRef = db.collection("users");
                            usersRef
                                    .whereEqualTo("email", activity.getEtEmail().getText().toString())
                                    .whereEqualTo("role", "admin")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Intent intent = new Intent(activity, MainActivity.class);
                                            if (!task1.getResult().isEmpty()) {
                                                intent.putExtra("role", "admin");
                                            } else {
                                                intent.putExtra("role", "common");
                                            }
                                            activity.startActivity(intent);
                                            activity.finish();
                                            progress.dismiss();
                                        } else {
                                            activity.getTvErLog().setText(R.string.sys_log_err);
                                            activity.getTvErLog().setVisibility(View.VISIBLE);
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase Login", "signInWithEmail:failure", task.getException());
                            activity.getEtEmail().setError("");
                            activity.getEtPwd().setError("");
                            activity.getTvErLog().setText(R.string.log_credential_error);
                            activity.getTvErLog().setVisibility(View.VISIBLE);
                            progress.dismiss();
                        }
                    });
        }

    }
}
