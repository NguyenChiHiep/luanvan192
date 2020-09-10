package com.example.quyen.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import java.util.ArrayList;

public class NguyenCongAdapter extends ArrayAdapter<NguyenCong> {
    Activity context;
    int layoutId;
    ArrayList<NguyenCong> arr;
    ArrayList<NguyenCong> mOriginalValues;

    public NguyenCongAdapter(Activity context, int layoutId, ArrayList<NguyenCong> arr){
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
    public NguyenCong getItem(int pos) {
        return arr.get(pos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder.tvNoiDung = (TextView) convertView.findViewById(R.id.tvNoiDung);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        NguyenCong nc = arr.get(position);

        holder.tvNoiDung.setText(nc.NoiDung);

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

                arr = (ArrayList<NguyenCong>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<NguyenCong> FilteredArrList = new ArrayList<NguyenCong>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<NguyenCong>(arr); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    switch (constraint.toString()){
                        case "1":
                            for (int i = 0; i < mOriginalValues.size(); i++) {
                                NguyenCong data = mOriginalValues.get(i);
                                if (data.CoPhoi == true && data.HoanThanh == false) {
                                    FilteredArrList.add(data);
                                }
                            }
                            break;
                        case "2":
                            for (int i = 0; i < mOriginalValues.size(); i++) {
                                NguyenCong data = mOriginalValues.get(i);
                                if (data.CoPhoi == true && data.HoanThanh == false && data.To) {
                                    FilteredArrList.add(data);

                                }
                            }
                            break;
                        case "3":
                            for (int i = 0; i < mOriginalValues.size(); i++) {
                                NguyenCong data = mOriginalValues.get(i);
                                if (data.CoPhoi == true && data.HoanThanh == false && data.NguyenCong) {
                                    FilteredArrList.add(data);
                                }
                            }
                            break;
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
        TextView tvNoiDung;
        int ref;
    }
}
