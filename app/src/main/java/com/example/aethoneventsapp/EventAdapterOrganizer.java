package com.example.aethoneventsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import com.bumptech.glide.Glide;


import android.content.Context;


public class EventAdapterOrganizer extends ArrayAdapter<Event> {
    private final Context context;
    private final List<Event> events;

    public EventAdapterOrganizer(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout if necessary
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        }

        // Get the event for the current position
        Event event = events.get(position);

        // Bind data to the views in the layout
        TextView titleTextView = convertView.findViewById(R.id.eventTitleTextView);
        TextView typeTextView = convertView.findViewById(R.id.eventTypeTextView);
        TextView timeTextView = convertView.findViewById(R.id.eventTimeTextView);
        ImageView typeIcon = convertView.findViewById(R.id.eventTypeIcon);
        ImageView posterImageView = convertView.findViewById(R.id.eventPosterImageView);

        // Set event name
        titleTextView.setText(event.getName());

        // Determine if the event is virtual or in-person based on location
        if (event.getLocation().toLowerCase().contains("virtual")) {
            typeTextView.setText("Virtual");
            typeIcon.setImageResource(R.drawable.ic_virtual); // Replace with actual icon
        } else {
            typeTextView.setText(event.getLocation());
            typeIcon.setImageResource(R.drawable.ic_virtual); // Replace with actual icon
        }

        // Set event date
        timeTextView.setText(event.getEventDate());
        // Set event date and time
        timeTextView.setText(event.getEventDate());
        // Set poster image (use placeholder if the URL or bitmap is null)
        if (event.getUrl() != null) {
            Glide.with(context)
                    .load(event.getUrl())  // Load the image from Firebase Storage URL
                    .circleCrop()  // Crop the image into a circle
                    .into(posterImageView);  // Set the image into the ImageView
        } else {
            posterImageView.setImageResource(R.drawable.event1); // Fallback image
        }

        return convertView;
    }
}