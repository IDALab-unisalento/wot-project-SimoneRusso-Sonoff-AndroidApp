package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.view.LoginActivity;
import it.unisalento.sonoff.view.MainActivity;

public class LoginListener implements View.OnClickListener {

    private LoginActivity activity;
    private FirebaseAuth mAuth;

    public LoginListener(LoginActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                ProgressDialog progress = new ProgressDialog(activity);
                progress.setTitle("Loading");
                progress.setMessage("Recupero i dati utente");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                mAuth.signInWithEmailAndPassword(activity.getEtEmail().getText().toString(), activity.getEtPwd().getText().toString())
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //If login success
                                if (task.isSuccessful()) {
                                    Log.d("Firebase Login", "signInWithEmail:success");
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference usersRef = db.collection("users");
                                    usersRef
                                            .whereEqualTo("email", activity.getEtEmail().getText().toString())
                                            .whereEqualTo("role", "admin")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(activity, MainActivity.class);
                                                        if(!task.getResult().isEmpty()){
                                                            intent.putExtra("role", "admin");

                                                        }
                                                        else{
                                                            intent.putExtra("role", "common");
                                                        }
                                                        activity.startActivity(intent);
                                                        activity.finish();
                                                        progress.dismiss();
                                                    }
                                                    else{
                                                        activity.getTvErLog().setText("Qualcosa non ha funzionato, riprova.\n Se il problema persiste riprova più tardi");
                                                        activity.getTvErLog().setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Firebase Login", "signInWithEmail:failure", task.getException());
                                    activity.getEtEmail().setError("");
                                    activity.getEtPwd().setError("");
                                    activity.getTvErLog().setText("Email o password errati!");
                                    activity.getTvErLog().setVisibility(View.VISIBLE);
                                    progress.dismiss();
                                }
                            }
                        });
                break;
        }

    }
}
