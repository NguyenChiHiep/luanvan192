package com.example.quyen.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Quyen on 7/27/2017.
 */

public class MayAdapter extends ArrayAdapter<May> {
    Activity context;
    int layoutId;
    ArrayList<May> arr;
    ArrayList<May> mOriginalValues;

    public MayAdapter(Activity context, int layoutId, ArrayList<May> arr){
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
    public May getItem(int pos) {
        return arr.get(pos);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MayAdapter.ViewHolder holder = null;
        if (convertView == null) {

            holder = new MayAdapter.ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder.tvMaSo = (TextView) convertView.findViewById(R.id.tvMaSoMay);

          convertView.setTag(holder);
        } else {
          holder = (MayAdapter.ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        May nc = arr.get(position);

        holder.tvMaSo.setText(nc.MaSo);

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

                arr = (ArrayList<May>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<May> FilteredArrList = new ArrayList<May>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<May>(arr); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    String s = constraint.toString();
                    String[] tokens = s.split(":");
                    String to = tokens[0].trim();
                    String ms = tokens[1].trim();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        May data = mOriginalValues.get(i);
                        if ( (ms.isEmpty() || data.MaSo.toLowerCase().startsWith(ms))
                                && (to.isEmpty() || data.ToSX_Id == Integer.parseInt(to))) {
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

    private  class ViewHolder {
        TextView tvMaSo;
        int ref;
    }
}
