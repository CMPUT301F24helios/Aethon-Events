package com.example.aethoneventsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>  {
    public static List<Event> eventList;
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
            Log.d("DEBUG", "Navigating with Event ID: " + event.getEventId());
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
        holder.deleteQrButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete QR Code")
                    .setMessage("Are you sure you want to delete and regenerate this QR code?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.collection("Events")
                                .document(String.valueOf(event.getEventId())) // Use the current document ID
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Generate a new event ID
                                        int newEventId = generateEventId();

                                        // Get the data from the old document snapshot
                                        Map<String, Object> documentData = documentSnapshot.getData();

                                        if (documentData != null) {
                                            // Add the new event ID to the document data
                                            documentData.put("eventId", newEventId);

                                            // Fill the lists from old subcollections
                                            fillLists(db, event.getEventId(), lists -> {
                                                // Create the new event document
                                                db.collection("Events")
                                                        .document(String.valueOf(newEventId))
                                                        .set(documentData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            // Add the lists as subcollections in the new document
                                                            addListsToNewDocument(db, newEventId, lists, () -> {
                                                                // Delete the old document
                                                                db.collection("Events")
                                                                        .document(String.valueOf(event.getEventId()))
                                                                        .delete()
                                                                        .addOnSuccessListener(aVoid2 -> {
                                                                            // Update local event object and adapter
                                                                            event.setEventId(newEventId);
                                                                            notifyItemChanged(position);
                                                                            Toast.makeText(context, "QR code regenerated successfully", Toast.LENGTH_SHORT).show();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            e.printStackTrace();
                                                                            Toast.makeText(context, "Error deleting old QR code", Toast.LENGTH_SHORT).show();
                                                                        });
                                                            });
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            e.printStackTrace();
                                                            Toast.makeText(context, "Error saving new QR code", Toast.LENGTH_SHORT).show();
                                                        });
                                            });
                                        }
                                    } else {
                                        Toast.makeText(context, "Event not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast.makeText(context, "Error accessing database", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void fillLists(
            FirebaseFirestore db,
            int eventId,
            OnListsFilledCallback callback) {

        Map<String, List<String>> lists = new HashMap<>();
        lists.put("WaitingList", new ArrayList<>());
        lists.put("Notifications", new ArrayList<>());
        lists.put("Pending", new ArrayList<>());
        lists.put("Accepted", new ArrayList<>());
        lists.put("Declined", new ArrayList<>());

        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (String key : lists.keySet()) {
            Task<QuerySnapshot> task = db.collection("Events")
                    .document(String.valueOf(eventId))
                    .collection(key)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            lists.get(key).add(doc.getId());
                        }
                    });
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks).addOnSuccessListener(taskList -> callback.onListsFilled(lists));
    }
    private void addListsToNewDocument(
            FirebaseFirestore db,
            int newEventId,
            Map<String, List<String>> lists,
            Runnable onSuccess) {

        WriteBatch batch = db.batch();

        for (Map.Entry<String, List<String>> entry : lists.entrySet()) {
            CollectionReference newSubcollection = db.collection("Events")
                    .document(String.valueOf(newEventId))
                    .collection(entry.getKey());

            if (entry.getValue().isEmpty()) {
                // Add a placeholder document to empty collections
                Map<String, Object> placeholderData = new HashMap<>();
                placeholderData.put("placeholder", true);
                batch.set(newSubcollection.document("placeholder"), placeholderData);
            } else {
                for (String docId : entry.getValue()) {
                    batch.set(newSubcollection.document(docId), new HashMap<>()); // Add empty data or relevant data
                }
            }
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e("Firestore", "Error creating collections");
                });
    }


    interface OnListsFilledCallback {
        void onListsFilled(Map<String, List<String>> lists);
    }


    // Generate a unique numeric event ID based on current timestamp
    private int generateEventId() {
        long timestamp = System.currentTimeMillis();
        String base36String = Long.toString(timestamp, 36).toUpperCase();

        // Convert the base36 alphanumeric string back to a number (if you need strictly numeric IDs)
        return base36String.hashCode() & Integer.MAX_VALUE; // Ensure positive integer
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, locationTextView;
        Button editButton, deleteButton, deleteQrButton;
        ImageView eventImage;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.event_name);
            dateTextView = itemView.findViewById(R.id.event_date);
            locationTextView = itemView.findViewById(R.id.event_location);
            editButton = itemView.findViewById(R.id.editButton);
            eventImage = itemView.findViewById(R.id.event_image);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteQrButton = itemView.findViewById(R.id.deleteQrButton);

        }
    }
}
