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

public class TrangThaiMayAdapter extends ArrayAdapter<TrangThaiMay> {
    Activity context;
    int layoutId;
    ArrayList<TrangThaiMay> arr;
    ArrayList<TrangThaiMay> mOriginalValues;

    public TrangThaiMayAdapter(Activity context, int layoutId, ArrayList<TrangThaiMay> arr){
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
    public TrangThaiMay getItem(int i) {
        return arr.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder{
        TextView textView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(layoutId, null);
            holder.textView = (TextView) view.findViewById(R.id.tvTenBuoc);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textView.setText(arr.get(i).NoiDung);
        if (i % 2 == 1) {
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                arr = (ArrayList<TrangThaiMay>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<TrangThaiMay> FilteredArrList = new ArrayList<TrangThaiMay>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<TrangThaiMay>(arr); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    String s = constraint.toString();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        TrangThaiMay data = mOriginalValues.get(i);
                        if (s.isEmpty() || data.NoiDungTG.toLowerCase().startsWith(s)) {
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

}
