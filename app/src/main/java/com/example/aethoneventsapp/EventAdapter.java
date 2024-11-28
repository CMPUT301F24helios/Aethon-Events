package com.example.aethoneventsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>  {
    private List<Event> eventList;
    private Context context;
    private OnEditButtonClickListener editButtonClickListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnEditButtonClickListener {
        void onEditButtonClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, OnEditButtonClickListener listener, FirebaseFirestore db) {
        this.context = context;
        this.eventList = eventList;
        this.editButtonClickListener = listener;
    }
    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
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
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.collection("Events")
                                .whereEqualTo("eventId", event.getEventId())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                        db.collection("Events").document(documentId).delete();
                                        eventList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                        Toast.makeText(context, "Event not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast.makeText(context, "Error deleting event", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();

        });

        // Load image using Picasso
        String imageUrl = event.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl) // Load the image from the URL
                    .placeholder(R.drawable.baseline_camera_alt_24) // Placeholder while loading
                    .error(R.drawable.baseline_insert_invitation_24) // Fallback if loading fails
                    .into(holder.eventImage);
        } else {
            // If the URL is null or empty, set a default image
            holder.eventImage.setImageResource(R.drawable.baseline_camera_alt_24);
        }

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEventActivity.class);
            intent.putExtra("eventId", String.valueOf(event.getEventId()));
            intent.putExtra("name", event.getName());
            intent.putExtra("date", event.getEventDate());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("capacity", event.getCapacity());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("imageUrl", event.getImageUrl());
            intent.putExtra("eventObject", event);
            context.startActivity(intent); // Navigate to EditEventActivity

        });

    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, locationTextView;
        Button editButton, deleteButton;
        ImageView eventImage;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.event_name);
            dateTextView = itemView.findViewById(R.id.event_date);
            locationTextView = itemView.findViewById(R.id.event_location);
            editButton = itemView.findViewById(R.id.editButton);
            eventImage = itemView.findViewById(R.id.event_image);
            deleteButton = itemView.findViewById(R.id.deleteButton);


        }
    }
}
