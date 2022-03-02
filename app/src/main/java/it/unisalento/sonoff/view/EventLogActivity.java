package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.Event;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.restService.RestService;
import it.unisalento.sonoff.utils.ListViewAdapter;

public class EventLogActivity extends AppCompatActivity {

    private User user;
    private ArrayList<Event> eventList = new ArrayList<>();
    private RestService restService;
    private ListView listView;
    private ProgressDialog progressDialog;
    private TextView countTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        if(user!=null) {

            setContentView(R.layout.activity_event_log);
            listView = findViewById(R.id.listView);
            countTextView = findViewById(R.id.count);

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Recupero i dati");
            progressDialog.setCancelable(false);// disable dismiss by tapping outside of the dialog
            progressDialog.show();

            restService = new RestService(getApplicationContext());
            restService.getEventLog(this);
        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public TextView getCountTextView() {
        return countTextView;
    }

    public void setCountTextView(TextView countTextView) {
        this.countTextView = countTextView;
    }
}