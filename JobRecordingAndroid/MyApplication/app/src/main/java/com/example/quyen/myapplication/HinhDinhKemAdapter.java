package com.example.quyen.myapplication;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HinhDinhKemAdapter extends ArrayAdapter<HinhDinhKem> {
    Activity context;
    int layoutId;
    ArrayList<HinhDinhKem> arr;

    public HinhDinhKemAdapter(Activity context, int layoutId, ArrayList<HinhDinhKem> arr){
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
            holder.imgHinh = (ImageView) convertView.findViewById(R.id.imgHinh);
            holder.btnXoa  = (Button) convertView.findViewById(R.id.btnXoaHinh);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        HinhDinhKem hinh = arr.get(position);

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(hinh.Data, 0, hinh.Data.length);
            holder.imgHinh.setImageBitmap(bitmap);
        } catch (OutOfMemoryError err) {
            System.gc();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            holder.imgHinh.setImageBitmap(bitmap);
        }

        holder.btnXoa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (context instanceof CongViec2Activity){
                    CongViec2Activity activity = (CongViec2Activity) context;
                    activity.OnBtnXoaHinhClick(holder.ref);
                } else {
                    CongViecLoai2DetailActivity activity = (CongViecLoai2DetailActivity)context;
                    activity.OnBtnXoaHinhClick(holder.ref);
                }
            }
        });

        return convertView;
    }

    private  class ViewHolder{
        ImageView imgHinh;
        Button btnXoa;
        int ref;
    }
}
