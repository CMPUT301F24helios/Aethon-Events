package com.example.aethoneventsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MainEventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public MainEventAdapter(Context context, List<Event> events) {
        super(context, R.layout.event_item, events);
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        Event event = events.get(position);

        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventLocation = convertView.findViewById(R.id.event_location);
        ImageView eventImage = convertView.findViewById(R.id.event_image);

        Picasso.get()
                .load(event.getImageUrl())
                .placeholder(R.drawable.aethon_color_ghoda_flipped) // Optional placeholder
                .into(eventImage);
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate());
        eventLocation.setText(event.getLocation());
        
        return convertView;
    }
}
