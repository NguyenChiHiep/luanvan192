package com.example.quyen.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class BuocAdapter extends ArrayAdapter<Buoc> {
    Activity context;
    int layoutId;
    ArrayList<Buoc> arr;

    public BuocAdapter(Activity context, int layoutId, ArrayList<Buoc> arr){
        super(context, layoutId, arr);

        this.context = context;
        this.layoutId = layoutId;
        this.arr = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder.tvTen = (TextView) convertView.findViewById(R.id.tvTenBuoc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        Buoc nc = arr.get(position);

        holder.tvTen.setText(nc.Ten);

        return convertView;
    }

    private  class ViewHolder {
        TextView tvTen;
        int ref;
    }
}
