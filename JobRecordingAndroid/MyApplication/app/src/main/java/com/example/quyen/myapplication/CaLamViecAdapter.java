package com.example.quyen.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CaLamViecAdapter extends BaseAdapter {
    Activity context;
    int layoutId;
    ArrayList<CaLamViec> arr;

    public CaLamViecAdapter(Activity context, int layoutId, ArrayList<CaLamViec> arr) {
        this.context = context;
        this.layoutId = layoutId;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int i) {
        return arr.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layoutId,null);
        ((TextView) view.findViewById(R.id.tvTenBuoc)).setText(arr.get(i).MaCaLV +" - " + arr.get(i).TenCa);
        return view;
    }
}
