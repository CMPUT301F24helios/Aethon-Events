package com.example.aethoneventsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private OnEditButtonClickListener editButtonClickListener;

    public interface OnEditButtonClickListener {
        void onEditButtonClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, OnEditButtonClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.editButtonClickListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.nameTextView.setText(event.getName());
        holder.dateTextView.setText(event.getEventDate());
        holder.locationTextView.setText(event.getLocation());
        holder.editButton.setOnClickListener(v -> editButtonClickListener.onEditButtonClick(event));

        // Load image using Picasso
        Picasso.get()
                .load(event.getImageUrl()) // Provide the image URL
                .placeholder(R.drawable.baseline_camera_alt_24) // Placeholder image while loading
                .error(R.drawable.baseline_insert_invitation_24) // Error image if loading fails
                .into(holder.eventImage);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEventActivity.class);
            intent.putExtra("eventId", String.valueOf(event.getEventId()));
            intent.putExtra("name", event.getName());
            intent.putExtra("date", event.getEventDate());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("capacity", event.getCapacity());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("imageUrl", event.getImageUrl());
            ((AdminViewActivity) context).startActivityForResult(intent, 1); // Request code = 1
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, locationTextView;
        Button editButton;
        ImageView eventImage;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.event_name);
            dateTextView = itemView.findViewById(R.id.event_date);
            locationTextView = itemView.findViewById(R.id.event_location);
            editButton = itemView.findViewById(R.id.edit_event_button);
            eventImage = itemView.findViewById(R.id.event_image);

        }
    }
}
