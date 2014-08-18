package com.adamschalmers.com;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<String> {
    public ItemAdapter(Context context, ArrayList<String> items) {
       super(context, R.layout.item_layout, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       String item = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
       }
       // Lookup view for data population
       TextView tvName = (TextView) convertView.findViewById(R.id.tvPrecursor);
       TextView tvHome = (TextView) convertView.findViewById(R.id.tvContent);
       // Populate the data into the template view using the data object
       tvName.setText("Reminder: ");
       tvHome.setText(item);
       // Return the completed view to render on screen
       return convertView;
   }
}