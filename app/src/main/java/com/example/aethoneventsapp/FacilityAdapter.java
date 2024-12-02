package com.example.aethoneventsapp;

import android.app.AlertDialog;
import android.content.Context;
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

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {
    public static List<Facility> facilityList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FacilityAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    public void setFacilityList(List<Facility> facilityList) {
        this.facilityList = facilityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.nameTextView.setText(facility.getName());
        holder.locationTextView.setText(facility.getLocation());

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Facility")
                    .setMessage("Are you sure you want to delete this facility?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.collection("facilities")
                                .whereEqualTo("facilityId", facility.getFacilityId())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                        db.collection("facilities").document(documentId).delete();
                                        facilityList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(context, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Facility not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast.makeText(context, "Error deleting facility", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, locationTextView;
        Button deleteButton;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.facility_name);
            locationTextView = itemView.findViewById(R.id.facility_location);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }
}
