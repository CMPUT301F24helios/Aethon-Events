package com.example.aethoneventsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class EntrantsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categories; // List of group categories (Accepted, Declined, Pending, Waitlist)
    private Map<String, List<String>> participants; // Map of participants under each category

    public EntrantsExpandableListAdapter(Context context, List<String> categories, Map<String, List<String>> participants) {
        this.context = context;
        this.categories = categories;
        this.participants = participants;
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
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(childText); // Set participant name
        textView.setPadding(80, 15, 15, 15); // Adjust padding to match styling (optional)

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; // Allow children to be selectable
    }
}