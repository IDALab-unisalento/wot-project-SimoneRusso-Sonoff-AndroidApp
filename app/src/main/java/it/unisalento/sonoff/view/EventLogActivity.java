package it.unisalento.sonoff.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.Event;
import it.unisalento.sonoff.model.User;
import it.unisalento.sonoff.restService.RestService;

public class EventLogActivity extends AppCompatActivity {

    private User user;
    private ArrayList<Event> eventList = new ArrayList<>();
    private RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        if(user!=null) {
            setContentView(R.layout.activity_event_log);

            restService = new RestService(getApplicationContext());
            restService.getEventLog(this);


            //TODO: Recycler view
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
}