package com.example.quyen.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NhanCongAdapter extends BaseAdapter {
    private Activity context;
    private int layout;
    private List<NhanCong> nhanCongList;

    public NhanCongAdapter(Activity context, int layout, List<NhanCong> nhanCongList) {
        this.context = context;
        this.layout = layout;
        this.nhanCongList = nhanCongList;
    }

    @Override
    public int getCount() {
        return nhanCongList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView tvCongViec,tvMCT,tvDTLV,tvMGC,tvTimeSet,tvTimeGiaCong,tvTimeNhanCong,tvGhiChu,tvTieuDe,tvTongHop;
        LinearLayout lnCongViec,lnTongHop;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            viewHolder.tvTieuDe         = (TextView) view.findViewById(R.id.textViewTieuDe);
            viewHolder.lnCongViec       = (LinearLayout) view.findViewById(R.id.linearLayoutCongViec);
            viewHolder.lnTongHop        = (LinearLayout) view.findViewById(R.id.linearLayoutTongHop);
            viewHolder.tvCongViec       = (TextView) view.findViewById(R.id.tvCongViec);
            viewHolder.tvMCT            = (TextView) view.findViewById(R.id.tvMCT);
            viewHolder.tvDTLV           = (TextView) view.findViewById(R.id.tvDtgLamViec);
            viewHolder.tvMGC            = (TextView) view.findViewById(R.id.tvMayGiaCong);
            viewHolder.tvTimeSet        = (TextView) view.findViewById(R.id.tvTimeSetup);
            viewHolder.tvTongHop        = (TextView) view.findViewById(R.id.textViewTongHop);
            viewHolder.tvTimeGiaCong    = (TextView) view.findViewById(R.id.tvTimeGiaCong);
            viewHolder.tvTimeNhanCong   = (TextView) view.findViewById(R.id.tvTimeNhanCong);
            viewHolder.tvGhiChu         = (TextView) view.findViewById(R.id.tvGhiChu);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        NhanCong nhanCong = nhanCongList.get(i);
        viewHolder.tvTieuDe.setText(nhanCong.TieuDe);
        if (nhanCong.TieuDe.length() != 0 && !nhanCong.TieuDe.equals("Tổng thời gian Công nhân")){
            viewHolder.tvTieuDe.setVisibility(View.VISIBLE);
            viewHolder.lnCongViec.setVisibility(View.GONE);
        } else if (nhanCong.TieuDe.equals("Tổng thời gian Công nhân")){
            viewHolder.tvTongHop.setVisibility(View.VISIBLE);
            viewHolder.lnTongHop.setVisibility(View.GONE);
        }
        viewHolder.tvTongHop.setText(nhanCong.TieuDe);
        viewHolder.tvCongViec.setText(nhanCong.CongViec);
        viewHolder.tvMCT.setText(nhanCong.MaChiTiet);
        viewHolder.tvDTLV.setText(nhanCong.DTgLamViec);
        viewHolder.tvMGC.setText(nhanCong.MayGiaCong);
        viewHolder.tvTimeSet.setText(nhanCong.TimeSetup!=0?nhanCong.TimeSetup+" '":"");
        viewHolder.tvTimeGiaCong.setText(nhanCong.TimeGiaCong != 0?nhanCong.TimeGiaCong +" '":"");
        viewHolder.tvTimeNhanCong.setText(nhanCong.TimeNhanCong!=0?nhanCong.TimeNhanCong +" '":"");
        viewHolder.tvGhiChu.setText(nhanCong.GhiChu.length() > 25 ? nhanCong.GhiChu.substring(0,23)+"..":nhanCong.GhiChu);
        return view;
    }
}
