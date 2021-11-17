package it.unisalento.sonoff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Listener implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    RestService restService;
    private FirebaseAuth mAuth;
    LoginActivity loginActivity;
    MainActivity mainActivity;

    public Listener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        restService = new RestService(mainActivity.getApplicationContext());
    }
    public Listener(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
        mAuth = FirebaseAuth.getInstance();
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
            case R.id.btnLogin:
                ProgressDialog progress = new ProgressDialog(loginActivity);
                progress.setTitle("Loading");
                progress.setMessage("Recupero i dati utente");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                mAuth.signInWithEmailAndPassword(loginActivity.getEtEmail().getText().toString(), loginActivity.getEtPwd().getText().toString())
                        .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase Login", "signInWithEmail:success");
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference usersRef = db.collection("users");
                                    usersRef
                                            .whereEqualTo("email", loginActivity.getEtEmail().getText().toString())
                                            .whereEqualTo("role", "admin")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(loginActivity, MainActivity.class);
                                                        if(!task.getResult().isEmpty()){
                                                            intent.putExtra("role", "admin");

                                                        }
                                                        else{
                                                            intent.putExtra("role", "common");
                                                        }
                                                        loginActivity.startActivity(intent);
                                                        loginActivity.finish();
                                                        progress.dismiss();
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Firebase Login", "signInWithEmail:failure", task.getException());
                                    loginActivity.getEtEmail().setError("");
                                    loginActivity.getEtPwd().setError("");
                                    loginActivity.getTvErLog().setVisibility(View.VISIBLE);
                                    progress.dismiss();
                                    /*Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();*/
                                }
                            }
                        });
                break;
        }
    }
}
