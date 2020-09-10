package com.example.quyen.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CongViec2Adapter extends ArrayAdapter<CongViec2> {
    Activity context;
    int layoutId;
    ArrayList<CongViec2> arr;

    public CongViec2Adapter(Activity context, int layoutId, ArrayList<CongViec2> arr){
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

            holder.tvGio = (TextView) convertView.findViewById(R.id.tvGio);
            holder.tvGood = (TextView) convertView.findViewById(R.id.tvGood);
            holder.tvNoGood = (TextView) convertView.findViewById(R.id.tvNoGood);

            holder.btnMenu = (Button)convertView.findViewById(R.id.btnMenuCv2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        CongViec2 cv = arr.get(position);

        holder.tvGio.setText(cv.Gio.toString("HH:mm"));
        holder.tvGood.setText(cv.Good.toString());
        holder.tvNoGood.setText(cv.NoGood.toString());

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, v);
                // Inflate the menu from xml
                popup.getMenuInflater().inflate(R.menu.popup_cong_viec2, popup.getMenu());
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_sua_cv2:
                                ((CongViec2Activity) context).OnBtnSuaCv2Click(holder.ref);
                                return true;
                            case R.id.item_xoa_cv2:
                                ((CongViec2Activity) context).OnBtnXoaCv2Click(holder.ref);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                // Handle dismissal with: popup.setOnDismissListener(...);
                // Show the menu
                popup.show();

            }
        });

        return convertView;
    }

    private  class ViewHolder{
        TextView tvGio;
        TextView tvGood;
        TextView tvNoGood;

        Button btnMenu;

        int ref;
    }
}
