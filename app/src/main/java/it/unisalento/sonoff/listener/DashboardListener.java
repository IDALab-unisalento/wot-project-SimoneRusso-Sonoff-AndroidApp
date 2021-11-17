package it.unisalento.sonoff.listener;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.view.DashboardActivity;

public class DashboardListener implements View.OnClickListener {
    private DashboardActivity activity;
    private FirebaseAuth mAuth2;


    public DashboardListener(DashboardActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddUser:
                if(activity.getEtNewEmail().getText().toString().length()!=0 && activity.getEtNewPwd().getText().toString().length()!=0 && activity.getEtRole().getText().toString().length()!=0) {
                    ProgressDialog progress = new ProgressDialog(activity);
                    progress.setTitle("Loading");
                    progress.setMessage("Operazione in corso...");
                    progress.setCancelable(false);
                    progress.show();
                    createUser(progress);
                }
                else{
                    if(activity.getEtNewEmail().getText().toString().length()==0)
                        activity.getEtNewEmail().setError("Non può essere vuoto!");
                    if(activity.getEtNewPwd().getText().toString().length()==0)
                        activity.getEtNewPwd().setError("Non può essere vuoto!");
                    if(activity.getEtRole().getText().toString().length()==0)
                        activity.getEtRole().setError("Non può essere vuoto!");
                }
                break;
            case R.id.btnRandPwd:
                String randonString = RandomStringUtils.randomAlphanumeric(16);
                activity.getEtNewPwd().setText(randonString);
                break;
        }
    }

    private void createUser(ProgressDialog progress) {
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAnsS9p-2Jv9u8ZBqjtWLfMXDeqT50SR6A")
                .setApplicationId("706972859833").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(activity.getApplicationContext(), firebaseOptions, "Sonoff");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("Sonoff"));
        }

        mAuth2.createUserWithEmailAndPassword(activity.getEtNewEmail().getText().toString(), activity.getEtNewPwd().getText().toString())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase auth Add", "createUserWithEmail:success");
                            addUserToDb(progress);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase auth Add", "createUserWithEmail:failure", task.getException());
                            if (task.getException().getMessage().equals("The email address is already in use by another account."))
                                activity.getEtNewEmail().setError("indirizzo email già in uso!");
                            progress.dismiss();
                        }
                    }
                });
    }

    private void addUserToDb(ProgressDialog progress) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        Map<String, Object> user = new HashMap<>();
        user.put("email", activity.getEtNewEmail().getText().toString());
        user.put("role", activity.getEtRole().getText().toString());
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        activity.getTvErDash().setText("Operazione completata!");
                        activity.getTvErDash().setTextColor(Color.GREEN);
                        activity.getTvErDash().setVisibility(View.VISIBLE);
                        activity.getEtNewEmail().setText("");
                        activity.getEtNewPwd().setText("");
                        mAuth2.signOut();
                        Log.d("Firestore add", "DocumentSnapshot written with ID: " + documentReference.getId());
                        progress.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore add", "Error adding document", e);
                        mAuth2.getCurrentUser().delete();
                        activity.getTvErDash().setText("Si è verificato un errore!");
                        activity.getTvErDash().setTextColor(Color.GREEN);
                        activity.getTvErDash().setVisibility(View.VISIBLE);
                    }
                });
    }


}
