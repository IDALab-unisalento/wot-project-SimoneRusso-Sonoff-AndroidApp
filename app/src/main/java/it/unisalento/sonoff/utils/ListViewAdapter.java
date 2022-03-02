package it.unisalento.sonoff.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import it.unisalento.sonoff.R;
import it.unisalento.sonoff.model.Event;

public class ListViewAdapter extends ArrayAdapter<Event> {
    public ListViewAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }
        // Lookup view for data population
        TextView tvEvent = (TextView) convertView.findViewById(R.id.tvEvent);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
        // Populate the data into the template view using the data object
        tvEvent.setText(event.getEventType().toUpperCase(Locale.ROOT));
        tvDate.setText(event.getDate());
        tvUser.setText(event.getUser());
        // Return the completed view to render on screen
        return convertView;
    }
}