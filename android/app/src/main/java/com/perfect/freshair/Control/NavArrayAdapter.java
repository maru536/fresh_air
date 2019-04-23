package com.perfect.freshair.Control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perfect.freshair.R;

import java.util.ArrayList;

public class NavArrayAdapter extends BaseAdapter {

    private ArrayList<String> items;
    private Context context;

    public NavArrayAdapter(Context context) {
        items = new ArrayList<String>();
        this.context = context;
    }

    public void addItem(String item)
    {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.navigation_list_item, parent, false);
        }

        TextView textView = (TextView)view.findViewById(R.id.nav_list_item);
        textView.setText(items.get(position));

        return view;
    }
}
