package com.example.aethoneventsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categories; // List of group categories (Accepted, Declined, Pending, Waitlist)
    private Map<String, List<String>> participants; // Map of participants under each category
    private String eventId;

    public EntrantsExpandableListAdapter(Context context, List<String> categories, Map<String, List<String>> participants, String eventId) {
        this.context = context;
        this.categories = categories;
        this.participants = participants;
        this.eventId = eventId;

    }

    @Override
    public int getGroupCount() {
        return categories.size(); // Number of groups (e.g., Accepted, Declined)
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String category = categories.get(groupPosition); // Get the category name
        return participants.get(category).size(); // Return the size of the participant list for this category
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition); // Return the group name
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String category = categories.get(groupPosition); // Get the group name
        return participants.get(category).get(childPosition); // Return the specific participant in this group
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true; // IDs are stable
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition); // Get group name

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(groupTitle); // Set the group title
        textView.setPadding(50, 20, 20, 20); // Adjust padding to match styling (optional)

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = (String) getChild(groupPosition, childPosition); // Get participant name

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.itemText);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        textView.setText(childText); // Set participant name

        deleteButton.setOnClickListener(v -> {
            // Handle delete button click
            showConfirmDialog(groupPosition, childPosition);
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; // Allow children to be selectable
    }

    private void showConfirmDialog(int groupPosition, int childPosition) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        // Create the AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView);

        // Get the dialog elements
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        // Set custom text for the dialog title and message
        dialogTitle.setText("Delete Entrant");
        dialogMessage.setText("Are you sure you want to delete the entrant from this list?");


        // Set up the dialog
        AlertDialog alertDialog = dialogBuilder.create();

        // Set the positive button click listener
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categories.get(groupPosition);
                String entrantId = participants.get(category).get(childPosition);

                // Remove the entrant from the current category in Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Events").document(eventId).collection(category).document(entrantId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Add the entrant to the "Cancelled" list in Firestore
                            db.collection("Events").document(eventId).collection("Cancelled").document(entrantId)
                                    .set(new HashMap<String, Object>())
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Remove the entrant from the local list and update the adapter
                                        participants.get(category).remove(childPosition);
                                        notifyDataSetChanged();
                                        alertDialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure to add to "Cancelled" list
                                        Log.w("EntrantsExpandableListAdapter", "Error adding entrant to Cancelled list", e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure to remove from current category
                            Log.w("EntrantsExpandableListAdapter", "Error removing entrant from current category", e);
                        });
            }
        });

        // Set the negative button click listener
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Show the dialog
        alertDialog.show();
    }

}