package com.example.sec.whereami;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Factory on 2016-07-18.
 */
public class GeoList extends ArrayAdapter<String>{
    private final Activity context;
    String address[];
    public GeoList(Activity context, String[] address) {
        super(context, R.layout.list_item, address);
        this.address = address;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item,null,true);
        TextView text = (TextView) rowView.findViewById(R.id.address);
        text.setText(address[position]);
        return rowView;
    }
}
