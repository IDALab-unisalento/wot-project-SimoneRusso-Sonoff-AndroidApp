package it.unisalento.sonoff.model;

import java.util.Date;

public class Event {
    private String eventType;
    private String user;
    private String date;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        String[] temp = date.replace("T", " ").substring(0,19).split(" ");
        String y = temp[0].split("-")[0];
        String m = temp[0].split("-")[1];
        String d = temp[0].split("-")[2];
        this.date = d+"-"+m+"-"+y+", "+temp[1];
    }
}
