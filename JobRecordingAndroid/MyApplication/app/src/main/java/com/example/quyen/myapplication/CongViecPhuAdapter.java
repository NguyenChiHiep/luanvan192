package com.example.quyen.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CongViecPhuAdapter extends ArrayAdapter<CongViecPhu> {
    Activity context;
    int layoutId;
    ArrayList<CongViecPhu> arr,mOriginalValues;

    public CongViecPhuAdapter(Activity context, int layoutId, ArrayList<CongViecPhu> arr){
        super(context, layoutId, arr);

        this.context = context;
        this.layoutId = layoutId;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public CongViecPhu getItem(int pos) {
        return arr.get(pos);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);

            holder.tvTen = (TextView) convertView.findViewById(R.id.tvTen);
            holder.tvMaSo = (TextView) convertView.findViewById(R.id.tvMaSo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        CongViecPhu cv = arr.get(position);
        holder.tvTen.setText(cv.Ten);
        holder.tvMaSo.setText(cv.MaSo);

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                arr = (ArrayList<CongViecPhu>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<CongViecPhu> FilteredArrList = new ArrayList<CongViecPhu>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<CongViecPhu>(arr); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
//                    constraint = constraint.toString().toLowerCase();
                    String s = constraint.toString();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        CongViecPhu data = mOriginalValues.get(i);
                        if (s.isEmpty() || data.MaSo.toLowerCase().startsWith(s)) {
                            FilteredArrList.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    private  class ViewHolder{
        TextView tvMaSo;
        TextView tvTen;
        int ref;
    }
}
