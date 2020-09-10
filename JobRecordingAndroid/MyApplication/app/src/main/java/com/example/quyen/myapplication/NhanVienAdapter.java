package com.example.quyen.myapplication;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class NhanVienAdapter extends ArrayAdapter<NhanVien> {
    Activity context;
    int layoutId;
    ArrayList<NhanVien> arr;

    public NhanVienAdapter(Activity context, int layoutId, ArrayList<NhanVien> arr){
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
            holder.tvMaSo = (TextView) convertView.findViewById(R.id.tvMaSo);
            holder.tvHoTen = (TextView) convertView.findViewById(R.id.tvHoTen);
            holder.btnThoatNv = (Button)convertView.findViewById(R.id.btnThoatNv);
            holder.tvColor = (TextView) convertView.findViewById(R.id.tvColor);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        NhanVien nv = arr.get(position);

        holder.tvMaSo.setText(nv.MaSo);
        holder.tvHoTen.setText(nv.HoTen);

        holder.btnThoatNv.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ((NhanVienActivity) context).OnBtnThoatNvClick(holder.ref);
             }
         });

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor((0xFF << 24) | Integer.decode(nv.Color));
        holder.tvColor.setBackground(drawable);

        return convertView;
    }

    private  class ViewHolder{
        TextView tvMaSo;
        TextView tvHoTen;
        Button btnThoatNv;
        TextView tvColor;
        int ref;
    }
}
