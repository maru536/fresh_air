package com.perfect.freshair.Control;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perfect.freshair.R;

import java.util.ArrayList;

public class ScanArrayAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> items = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item_scan, viewGroup, false);
        }

        TextView txtName = (TextView)view.findViewById(R.id.listview_txt_name);
        TextView txtAddr = (TextView)view.findViewById(R.id.listview_txt_addr);

        BluetoothDevice device = (BluetoothDevice)getItem(i);
        txtName.setText(device.getName());
        txtAddr.setText(device.getAddress());

        return view;
    }

    public boolean isAlreadyItem(String addr)
    {
        boolean res = false;
        for(int i=0; i < getCount(); i++)
        {
            if(items.get(i).getAddress().equals(addr))
            {
                res = true;
                break;
            }
        }

        return res;
    }

    public void addItem(BluetoothDevice device)
    {
        items.add(device);
    }

    public void cleanItems()
    {
        items.clear();
    }
}
