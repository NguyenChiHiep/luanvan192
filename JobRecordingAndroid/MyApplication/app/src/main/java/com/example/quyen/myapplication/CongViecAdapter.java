package com.example.quyen.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CongViecAdapter extends ArrayAdapter<CongViec> {
    Activity context;
    int layoutId;
    ArrayList<CongViec> arr;

    public CongViecAdapter(Activity context, int layoutId, ArrayList<CongViec> arr){
        super(context, layoutId, arr);

        this.context = context;
        this.layoutId = layoutId;
        this.arr = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CongViec cv = arr.get(position);

        if (cv.PhanLoai == 1) {
            final ViewHolder holder;

            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_cong_viec_loai1, null);

            holder.tvChiTiet        = (TextView) convertView.findViewById(R.id.tvChiTiet);
            holder.tvMay            = (TextView) convertView.findViewById(R.id.tvMay);
            holder.tvGioBD          = (TextView) convertView.findViewById(R.id.tvGioBD);
            holder.tvGioKTSetup     = (TextView) convertView.findViewById(R.id.tvGioKTSetup);
            holder.tvGioBDChay      = (TextView) convertView.findViewById(R.id.tvGioBDChay);
            holder.tvGioTD1         = (TextView) convertView.findViewById(R.id.tvGioTD1);
            holder.tvGioTT1         = (TextView) convertView.findViewById(R.id.tvGioTT1);
            holder.tvGioTD2         = (TextView) convertView.findViewById(R.id.tvGioTD2);
            holder.tvGioTT2         = (TextView) convertView.findViewById(R.id.tvGioTT2);
            holder.tvGioKT          = (TextView) convertView.findViewById(R.id.tvGioKT);
            holder.tvGioBD_BS       = (TextView) convertView.findViewById(R.id.tvGioBD_BS);
            holder.tvGioKT_BS       = (TextView) convertView.findViewById(R.id.tvGioKT_BS);
            holder.tvTongGood       = (TextView) convertView.findViewById(R.id.tvTongGood);
            holder.tvTongDoDang     = (TextView) convertView.findViewById(R.id.tvTongDoDang);
            holder.tvTongNoGood     = (TextView) convertView.findViewById(R.id.tvTongNoGood);
            holder.tvNguyenCong     = (TextView) convertView.findViewById(R.id.tvNguyenCong);
            holder.tvBuoc           = (TextView) convertView.findViewById(R.id.tvBuoc);
            holder.btnMenu          = (Button) convertView.findViewById(R.id.btnMenu);

            convertView.setTag(holder);

            holder.ref = position;

            holder.tvChiTiet.setText(cv.ChiTiet);
            if (cv.May != null)
                holder.tvMay.setText(cv.May.MaSo);

            holder.tvGioBD.setText(cv.GioBD.toString("HH:mm"));

            if (cv.GioKTSetup != null) {
                holder.tvGioKTSetup.setText(cv.GioKTSetup.toString("HH:mm"));
            }else {
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineGioKTSetup);
                a.setVisibility(View.GONE);
            }

            if (cv.GioBDChay != null){
                holder.tvGioBDChay.setText(cv.GioBDChay.toString("HH:mm"));
            } else {
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineGioBDChay);
                a.setVisibility(View.GONE);
            }

            if (cv.GioTamDung1 != null){
                holder.dem = 1;
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineTD1);
                a.setVisibility(View.VISIBLE);
                holder.tvGioTD1.setText(cv.GioTamDung1.toString("HH:mm"));
            }

            if (cv.GioTiepTuc1 != null){
                holder.dem = 2;
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineTT1);
                a.setVisibility(View.VISIBLE);
                holder.tvGioTT1.setText(cv.GioTiepTuc1.toString("HH:mm"));
            }

            if (cv.GioTamDung2 != null){
                holder.dem = 3;
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineTD2);
                a.setVisibility(View.VISIBLE);
                holder.tvGioTD2.setText(cv.GioTamDung2.toString("HH:mm"));
            }

            if (cv.GioTiepTuc2 != null){
                holder.dem = 4;
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineTT2);
                a.setVisibility(View.VISIBLE);
                holder.tvGioTT2.setText(cv.GioTiepTuc2.toString("HH:mm"));
            }

            if (cv.GioKT != null) {
                holder.tvGioKT.setText(cv.GioKT.toString("HH:mm"));
                holder.tvGioKT.setTextSize(14);
            }

            if (cv.GioBD_BS != null)
                holder.tvGioBD_BS.setText(cv.GioBD_BS.toString("HH:mm"));

            if (cv.GioKT_BS != null)
                holder.tvGioKT_BS.setText(cv.GioKT_BS.toString("HH:mm"));

            holder.tvTongGood.setText(cv.TongGood.toString());
            if (cv.TongDoDang != null){
                if (cv.TongDoDang == 0){
                    LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lnDoDang);
                    a.setVisibility(View.GONE);
                } else {
                    LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lnDoDang);
                    a.setVisibility(View.VISIBLE);
                    holder.tvTongDoDang.setText(cv.TongDoDang.toString());
                }
            } else {
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lnDoDang);
                a.setVisibility(View.GONE);
            }
            holder.tvTongNoGood.setText(cv.TongNoGood.toString());
            if (cv.NguyenCong != null)
                holder.tvNguyenCong.setText(cv.NguyenCong.NoiDung);
            else {
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineNguyenCong);
                a.setVisibility(View.GONE);
            }

            if (cv.Buocs == null){
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineBuoc);
                a.setVisibility(View.GONE);
            }
            holder.tvBuoc.setText(cv.Buocs);

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final PopupMenu popup = new PopupMenu(context, v);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.popup_cong_viec_loai_1, popup.getMenu());

                    if (!holder.tvGioKTSetup.getText().equals("HH:mm")){ // nếu đang tạm dừng sau setup thì sẽ đổi thành bắt đầu chạy máy
                        popup.getMenu().findItem(R.id.item_ktset_bdchay).setTitle("Bắt đầu chạy máy");
                    }
                    if (cv.GioBDChay != null){ // nếu đang tạm dừng sau setup thì sẽ đổi thành bắt đầu chạy máy
                        popup.getMenu().findItem(R.id.item_ktset_bdchay).setVisible(false);
                    }
                    if(cv.GioKT != null){ // Nếu công việc đã kết thúc thì sẽ ẩn các item tạm dừng/ tiếp tục và item kết thúc
                        popup.getMenu().findItem(R.id.item_ktset_bdchay).setVisible(false);
                        popup.getMenu().findItem(R.id.item_pause_resume).setVisible(false);
                        popup.getMenu().findItem(R.id.item_dung).setVisible(false);
                    }

                    if (holder.dem == 4){// Hiển thị ra Tạm dừng hay tiếp tục, nếu tạm dừng, tiếp tục 2 lần sẽ ẩn item kết thúc
                        popup.getMenu().findItem(R.id.item_pause_resume).setVisible(false);
                    } else {
                        if(holder.dem == 0 || holder.dem == 2){
                            popup.getMenu().findItem(R.id.item_pause_resume).setTitle("Tạm dừng");
                        } else {
                            popup.getMenu().findItem(R.id.item_dung).setVisible(false);
                            popup.getMenu().findItem(R.id.item_ktset_bdchay).setVisible(false);
                            popup.getMenu().findItem(R.id.item_pause_resume).setTitle("Tiếp Tục");
                        }
                    }

                    // Setup menu item selection
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_ghi_chu:
                                    ((CongViecActivity) context).OnBtnGhiChuClick(holder.ref);
                                    return true;
                                case R.id.item_them_slg:
                                    ((CongViecActivity) context).OnBtnThemSLClick(holder.ref);
                                    return true;
                                case R.id.item_ktset_bdchay:
                                    ((CongViecActivity) context).DungSetBDChay(holder.ref);
                                    return true;
                                case R.id.item_pause_resume:
                                    ((CongViecActivity) context).TamDungTiepTuc(holder.ref,holder.dem);
                                    return true;
                                case R.id.item_dung:
                                    ((CongViecActivity) context).OnBtnDungClick(holder.ref);
                                    return true;
                                case R.id.item_sua_gio:
                                    ((CongViecActivity) context).OnBtnSuaGioClick(holder.ref);
                                    return true;
                                case R.id.item_copy_cv:
                                    ((CongViecActivity) context).OnBtnCopyClick(holder.ref);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
//                     Handle dismissal with: popup.setOnDismissListener(...);
//                     Show the menu
                    popup.show();
                }
            });
        } else if (cv.PhanLoai == 2)  { // phan loai == 2
            final ViewHolder holder;

            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_cong_viec_loai2, null);

            holder.tvGioBD = (TextView) convertView.findViewById(R.id.tvGioBD);
            holder.tvGioKT = (TextView) convertView.findViewById(R.id.tvGioKT);
            holder.tvGioBD_BS = (TextView) convertView.findViewById(R.id.tvGioBD_BS);
            holder.tvGioKT_BS = (TextView) convertView.findViewById(R.id.tvGioKT_BS);
            holder.tvCvPhu = (TextView) convertView.findViewById(R.id.tvCvPhu);
            holder.btnMenu = (Button) convertView.findViewById(R.id.btnMenu);
            holder.tvDTLV = (TextView) convertView.findViewById(R.id.tvDTLV);

            convertView.setTag(holder);

            holder.ref = position;

            holder.tvGioBD.setText(cv.GioBD.toString("HH:mm"));

            if (cv.GioKT != null) {
                holder.tvGioKT.setText(cv.GioKT.toString("HH:mm"));
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(Color.WHITE);
                holder.tvGioKT.setBackground(drawable);
            } else{
                holder.tvGioKT.setText("           ");
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor((0xFF << 24) | Integer.decode("#FF9900"));
                holder.tvGioKT.setBackground(drawable);
            }

            if (cv.GioBD_BS != null)
                holder.tvGioBD_BS.setText(cv.GioBD_BS.toString("HH:mm"));
            if (cv.GioKT_BS != null)
                holder.tvGioKT_BS.setText(cv.GioKT_BS.toString("HH:mm"));
            if (cv.DoiTuongLD == null){
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineDTLV);
                a.setVisibility(View.GONE);
            } else if (cv.DoiTuongLD.length() == 0){
                LinearLayout a = (LinearLayout) convertView.findViewById(R.id.lineDTLV);
                a.setVisibility(View.GONE);

            }
            holder.tvDTLV.setText(cv.DoiTuongLD);
            holder.tvCvPhu.setText(
                    cv.CongViecPhu.MaSo + "-"+ cv.CongViecPhu.Ten);

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, v);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.popup_cong_viec_loai_2, popup.getMenu());
                    // Setup menu item selection
                    if (cv.CongViecPhu.MaSo.equals("NKR") || cv.CongViecPhu.MaSo.equals("NVR")){
                        popup.getMenu().findItem(R.id.item_ghi_chu).setVisible(false);
                        popup.getMenu().findItem(R.id.item_chup_hinh).setVisible(false);
                        popup.getMenu().findItem(R.id.item_sua_gio).setVisible(false);
                    }
                    if(!holder.tvGioKT.getText().equals("           ")){
                        popup.getMenu().findItem(R.id.item_ket_thuc).setVisible(false);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_ghi_chu:
                                    ((CongViecActivity) context).OnBtnGhiChuClick(holder.ref);
                                    return true;
                                case R.id.item_ket_thuc:
                                    ((CongViecActivity) context).OnBtnKeThucClick(holder.ref);
                                    return true;
                                case R.id.item_chup_hinh:
                                    ((CongViecActivity) context).OnBtnChupHinhClick(holder.ref);
                                    return true;
                                case R.id.item_sua_gio:
                                    ((CongViecActivity) context).OnBtnSuaGioClick(holder.ref);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
//                     Handle dismissal with: popup.setOnDismissListener(...);
//                     Show the menu
                    popup.show();
                }
            });
        } else if (cv.PhanLoai == 3){
            final ViewHolder holder;
            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_cong_viec_loai3, null);
            holder.tvChiTiet = (TextView) convertView.findViewById(R.id.textTenViec);
            holder.tvMay = (TextView) convertView.findViewById(R.id.textMay);
            holder.tvGioBD = (TextView) convertView.findViewById(R.id.textThoiDiem);
            holder.btnMenu = (Button) convertView.findViewById(R.id.btnMenu);
            convertView.setTag(holder);

            holder.ref = position;

            holder.tvGioBD.setText(cv.GioBD.toString("HH:mm"));
            holder.tvMay.setText(cv.DoiTuongLD);
            holder.tvChiTiet.setText(cv.ChiTiet);
            ((TextView) convertView.findViewById(R.id.textTinhTrang)).setText(cv.NoiDung);
            ((TextView) convertView.findViewById(R.id.textTrangThai)).setText(cv.NoiDung);
            ((TextView) convertView.findViewById(R.id.textMoTa)).setText(cv.GhiChu);
            if (cv.ChiTiet.equals("Thông báo máy chờ")){
                convertView.findViewById(R.id.lineHong).setVisibility(View.GONE);
            }
            if (cv.ChiTiet.equals("Thông báo máy hỏng")){
                convertView.findViewById(R.id.lineTrangThai).setVisibility(View.GONE);
            }
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, view);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.popup_cong_viec_loai_3, popup.getMenu());
                    if (cv.ChiTiet.equals("Thông báo máy chờ")){
                        popup.getMenu().findItem(R.id.item_tinh_trang).setTitle("Sửa Mô Tả");
                    }
                    if (cv.ChiTiet.equals("Thông báo máy hỏng")){
                        popup.getMenu().findItem(R.id.item_tinh_trang).setTitle("Sửa Tình Trạng");
                        popup.getMenu().findItem(R.id.item_trang_thai).setVisible(false);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_sua_may:
                                    ((CongViecActivity) context).OnBtnSuaMayClick(holder.ref);
                                    return true;
                                case R.id.item_trang_thai:
                                    ((CongViecActivity) context).OnBtnTrangThaiClick(holder.ref);
                                    return true;
                                case R.id.item_tinh_trang:
                                    ((CongViecActivity) context).OnBtnTrangThai(holder.ref);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        }
        return convertView;
    }

    private  class ViewHolder{
        TextView tvChiTiet;
        TextView tvMay;
        TextView tvGioBD;
        TextView tvGioKTSetup;
        TextView tvGioBDChay;
        TextView tvGioKT;
        TextView tvGioBD_BS;
        TextView tvGioKT_BS;
        TextView tvGioTD1,tvGioTT1,tvGioTD2,tvGioTT2;
        TextView tvTongGood;
        TextView tvTongDoDang;
        TextView tvTongNoGood;
        TextView tvNguyenCong;
        TextView tvBuoc;
        TextView tvCvPhu;
        TextView tvDTLV;
        Button btnMenu;
        int ref;
        int dem = 0;
    }
}
