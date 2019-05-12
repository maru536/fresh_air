package com.perfect.freshair.Control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perfect.freshair.Model.MyBLEDevice;
import com.perfect.freshair.R;

import java.util.ArrayList;

public class BLEScanArrayAdapter extends BaseAdapter {

    private ArrayList<MyBLEDevice> items;
    private Context context;
    private TextView textName;
    private TextView textAddr;

    public BLEScanArrayAdapter(Context context, ArrayList<MyBLEDevice> arrayList) {
        items = arrayList;
        this.context = context;
    }

    public void addItem(MyBLEDevice item) {
        if (!items.contains(item))
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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ble_scan_list_item, parent, false);
        }

        textName = (TextView) view.findViewById(R.id.scan_list_item_name);
        textName.setText(items.get(position).getName());
        textAddr = (TextView) view.findViewById(R.id.scan_list_item_addr);
        textAddr.setText(items.get(position).getAddr());
        return view;
    }
}
