package com.example.quyen.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ThoiGianNguyenCongActivity extends AppCompatActivity {
    private BroadcastReceiver mReceiver;
    ODataConsumer consumer;
    NhanVien nhanVien;
    ArrayList<NhanCong> nhanCongs;
    NhanCongAdapter nhanCongAdapter;
    LocalDateTime gioBD_CV,gioKT_CV;
    int tongGio;

    public static final String TAG = ThoiGianNguyenCongActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoi_gian_nguyen_cong);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        nhanVien = (NhanVien) intent.getSerializableExtra("NhanVien"); // Màn hình chính sẽ truyền dữ liệu vào cho màn hình này để mình có thể lấy dữ liệu ra.
        nhanCongs = new ArrayList<>();
        LoadListNguyenCong();
        final ListView listView = (ListView) findViewById(R.id.listViewNC);
        ((TextView) findViewById(R.id.textViewChonCa)).setText(nhanVien.caLamViec != null ? nhanVien.caLamViec.MaCaLV : "CA");
        try {
            for (CongViec congViec: nhanVien.congViecs){
                if (congViec.PhanLoai!= 3){
                    gioBD_CV = congViec.GioBD;
                    ((TextView) findViewById(R.id.textViewBatDauCa)).setText(gioBD_CV.toString("HH:mm"));
                    ((TextView) findViewById(R.id.textViewNgay)).setText(gioBD_CV.toString("dd/MM/yyyy"));
                }
            }
        } catch (Exception ex) { }
        findViewById(R.id.textViewNgay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChonNgay();
            }
        });
        try {
            gioKT_CV = GetTimeFinish();
            ((TextView) findViewById(R.id.textViewKetThucCa)).setText(gioKT_CV.toString("HH:mm"));
        } catch (Exception ex) { }
        try {
            tongGio = Minutes.minutesBetween(gioBD_CV,gioKT_CV).getMinutes();
            ((TextView) findViewById(R.id.edtTongTimeLamViec)).setText("Tổng t/g làm việc max: "+tongGio+"'");
        } catch (Exception ex) { }

        findViewById(R.id.linearLayoutChonCa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadCaLamViecTask().execute();
            }
        });
        nhanCongAdapter = new NhanCongAdapter(this, R.layout.item_gio_nhan_cong, nhanCongs);
        listView.setAdapter(nhanCongAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (nhanCongs.get(i).TieuDe.length() == 0) {
                    OnBtnSuaNGGhiChuClick(i);
                } else {
                    switch (i) {
                        case 1:
                            OnBtnGioCongViec();
                            break;
                    }
                }
                return false;
            }
        });
        String serviceUrl = Setting.ServerUrl;
        OClientBehavior basicAuth = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(basicAuth).build();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void ChonNgay(){
        LocalDateTime localDateTime = new LocalDateTime();
        if (gioBD_CV != null) {
           localDateTime = gioBD_CV;
        }
        int ngay = localDateTime.getDayOfMonth();
        int thang = localDateTime.getMonthOfYear()-1;
        int nam = localDateTime.getYear();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth,0,0);
                LocalDateTime localDateTime1 = LocalDateTime.fromCalendarFields(calendar);
            }
        }, nam, thang, ngay);
        datePickerDialog.show();
    }

    LocalDateTime GetTimeFinish(){
        if (nhanVien.congViecs != null) {
            LocalDateTime timeHT = gioBD_CV;
            for (CongViec cv: nhanVien.congViecs){
                try {
                    if (Minutes.minutesBetween(timeHT, cv.GioKT).getMinutes() > 0 && cv.PhanLoai != 3) {
                        timeHT = cv.GioKT;
                    }
                } catch (Exception ex){}
            }
            return timeHT;
        }
        return null;
    }

    void LoadListNguyenCong(){
        nhanCongs.add(new NhanCong(-1,"Ăn cơm, nghỉ(được chấm giờ)","","","","",0,0,nhanVien.caLamViec!=null?nhanVien.caLamViec.GioNghi:0,0,""));
        nhanCongs.add(new NhanCong(-1,"Việc riêng cá nhân(trong CT, trong giờ, không chấm giờ)","","","","",0,0,0,0,""));
        nhanCongs.add(new NhanCong(-1,"Việc riêng cá nhân(Ra khỏi công ty)","","","","",0,0,0,0,""));
        for (int i =0;i<nhanVien.congViecs.size();i++){ // kiểm tra, tính toán dữ liệu.
            CongViec congViec = nhanVien.congViecs.get(i);
            LocalDateTime gioBD = congViec.GioBD_BS != null? congViec.GioBD_BS:congViec.GioBD;
            LocalDateTime gioKTSetup = congViec.GioKTSetup_BS != null? congViec.GioKTSetup_BS:congViec.GioKTSetup;
            LocalDateTime gioBDChay = congViec.GioBDChay_BS != null? congViec.GioBDChay_BS:congViec.GioBDChay;
            LocalDateTime gioTD1 = congViec.GioTamDung1_BS != null? congViec.GioTamDung1_BS:congViec.GioTamDung1;
            LocalDateTime gioTT1 = congViec.GioTiepTuc1_BS != null? congViec.GioTiepTuc1_BS:congViec.GioTiepTuc1;
            LocalDateTime gioTD2 = congViec.GioTamDung2_BS != null? congViec.GioTamDung2_BS:congViec.GioTamDung2;
            LocalDateTime gioTT2 = congViec.GioTiepTuc2_BS != null? congViec.GioTiepTuc2_BS:congViec.GioTiepTuc2;
            LocalDateTime gioKT = congViec.GioKT_BS != null? congViec.GioKT_BS:congViec.GioKT;
            if (congViec.GioKT != null && congViec.PhanLoai == 1){
                int giosetmay = 0;
                int giochaymay;
                int gioTamDung1;
                int gioTamDung2;
                try {
                    giosetmay = Minutes.minutesBetween(gioBD,gioKTSetup).getMinutes();
                    giochaymay = Minutes.minutesBetween(gioBDChay,gioKT).getMinutes();
                } catch (Exception ex){
                    giochaymay = Minutes.minutesBetween(gioBD, gioKT).getMinutes();
                }
                try {
                    gioTamDung1 =  Minutes.minutesBetween(gioTD1,gioTT1).getMinutes();
                    if (Minutes.minutesBetween(gioTD1,gioKTSetup).getMinutes()>0 || Minutes.minutesBetween(gioTT1,gioKTSetup).getMinutes()>0){
                        giosetmay = giosetmay - gioTamDung1;
                    }
                    if (Minutes.minutesBetween(gioTD1,gioBDChay).getMinutes()<0 || Minutes.minutesBetween(gioTT1,gioBDChay).getMinutes()<0)   {
                        giochaymay = giochaymay - gioTamDung1;
                    }
                } catch (Exception ex){ }
                try {
                    gioTamDung2 =  Minutes.minutesBetween(gioTD2,gioTT2).getMinutes();
                    if (Minutes.minutesBetween(gioTD2,gioKTSetup).getMinutes()>0 || Minutes.minutesBetween(gioTT2,gioKTSetup).getMinutes()>0){
                        giosetmay = giosetmay - gioTamDung2;
                    }
                    if (Minutes.minutesBetween(gioTD2,gioBDChay).getMinutes()<0 || Minutes.minutesBetween(gioTT2,gioBDChay).getMinutes()<0)   {
                        giochaymay = giochaymay - gioTamDung2;
                    }
                } catch (Exception ex){ }
                if (congViec.NguyenCong == null){
                    nhanCongs.add(new NhanCong(i, "","", congViec.ChiTiet, congViec.DoiTuongLD, congViec.May.MaSo, giosetmay, giochaymay, congViec.GioNguyenCong,0, congViec.GhiChu == null ? "" : congViec.GhiChu));
                } else {
                    String nguyenCongString = congViec.NguyenCong.NoiDung;
                    try{
                        String[] string = nguyenCongString.split("-");
                        nguyenCongString = string[1].trim();
                    } catch (Exception e){}
                    nhanCongs.add(new NhanCong(i,"", nguyenCongString, congViec.ChiTiet, congViec.DoiTuongLD, congViec.May.MaSo, giosetmay, giochaymay, congViec.GioNguyenCong,0, congViec.GhiChu == null ? "" : congViec.GhiChu));
                }
            }
            if (congViec.GioKT != null && congViec.PhanLoai == 2){
                if (!congViec.CongViecPhu.MaSo.equals("NKR") && !congViec.CongViecPhu.MaSo.equals("NVR")){
                    String congViecPhuString = congViec.CongViecPhu.Ten;
                    try{
                        String[] string = congViecPhuString.split("-");
                        congViecPhuString = string[1].trim();
                    } catch (Exception e){}
                    nhanCongs.add(new NhanCong(i,"",congViecPhuString,"",congViec.DoiTuongLD,"",0,0,congViec.GioNguyenCong,0,congViec.GhiChu== null?"":congViec.GhiChu));
                } else {
                    if (congViec.CongViecPhu.MaSo.equals("NKR")){
                        nhanCongs.get(1).TimeNhanCong =congViec.GioNguyenCong;
                        nhanCongs.get(1).id = i;
                    }
                    if (congViec.CongViecPhu.MaSo.equals("NVR")){
                        nhanCongs.get(2).TimeNhanCong = congViec.GioNguyenCong;
                        nhanCongs.get(2).id = i;
                    }
                }
            }
        }
        int timeSUTong = 0;
        int timeGCTong = 0;
        int timeNCTong = 0;
        int timeTCTong = 0;
        for (int i = 0; i<nhanCongs.size();i++){
            timeSUTong += nhanCongs.get(i).TimeSetup;
            timeGCTong += nhanCongs.get(i).TimeGiaCong;
            timeNCTong += nhanCongs.get(i).TimeNhanCong;
            timeTCTong += nhanCongs.get(i).TimeTangCa;
        }
        nhanCongs.add(new NhanCong(0,"Tổng thời gian Công nhân","","","","",timeSUTong,timeGCTong,timeNCTong,timeTCTong,""));
    }

    void UpdateTimeCaLamViec(){
        int timeSUTong = 0;
        int timeGCTong = 0;
        int timeNCTong = 0;
        int timeTCTong = 0;
        for (int i = 0; i<nhanCongs.size()-1;i++){
            timeSUTong += nhanCongs.get(i).TimeSetup;
            timeGCTong += nhanCongs.get(i).TimeGiaCong;
            timeNCTong += nhanCongs.get(i).TimeNhanCong;
            timeTCTong += nhanCongs.get(i).TimeTangCa;
        }
        nhanCongs.get(nhanCongs.size()-1).TimeSetup   =timeSUTong;
        nhanCongs.get(nhanCongs.size()-1).TimeGiaCong   =timeGCTong;
        nhanCongs.get(nhanCongs.size()-1).TimeNhanCong  =timeNCTong;
        nhanCongs.get(nhanCongs.size()-1).TimeTangCa    =timeTCTong;
        nhanCongAdapter.notifyDataSetChanged();
    }
    public void OnBtnGioCongViec(){
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_gio_ca_nhan, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Giờ cá nhân")
                .setIcon(R.drawable.logo)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int a =0;
                        try{
                            a = Integer.parseInt(((TextView)((AlertDialog)dialogInterface).findViewById(R.id.editTextGioCaNhan)).getText().toString().trim());
                        } catch (Exception ex){ }
                        new UpdateTimeCVTask().execute(a+"");
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
        ((TextView) dialog.findViewById(R.id.editTextGioCaNhan)).setText(nhanCongs.get(1).TimeNhanCong==0?"":nhanCongs.get(1).TimeNhanCong+"");
    }

    public void OnBtnSuaNGGhiChuClick(final int position) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_ghi_chu, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Ghi chú công việc : " + nhanCongs.get(position).CongViec)
                .setIcon(R.drawable.logo)
                .create();
        dialog.show();
        final EditText timeNC = (EditText) dialog.findViewById(R.id.edtTimeNC);
        final EditText ghiChu = (EditText) dialog.findViewById(R.id.editGhiChu);
        timeNC.setText(nhanCongs.get(position).TimeNhanCong == 0?"":nhanCongs.get(position).TimeNhanCong + "");
        ghiChu.setText(nhanCongs.get(position).GhiChu + "");
        dialog.findViewById(R.id.btnCameraGhiChu).setVisibility(View.GONE);
        dialog.findViewById(R.id.lineDTgLV).setVisibility(View.GONE);
        dialog.findViewById(R.id.btnOkGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new GhiChuTimeTask().execute(position+"",timeNC.getText().toString().trim(),ghiChu.getText().toString().trim());
                } catch(Exception ex){ }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnHuyGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("NhanVien",nhanVien);   // Nhấn back về thì dữ liệu vẫn được cập nhật
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    class LoadCaLamViecTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<CaLamViec> caLamViecArrayList;
        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(ThoiGianNguyenCongActivity.this);
        }
        @Override
        protected Integer doInBackground(String... strings) {
            caLamViecArrayList = new ArrayList<>();
            try{
                for (OEntity calamviec : consumer.getEntities("CaLamViecs").execute()){
                    int id = calamviec.getProperty("Id", Integer.class).getValue();
                    String tenca = calamviec.getProperty("NoiDung", String.class).getValue();
                    int tgNghi = (int)  ((calamviec.getProperty("GioNghi",Double.class).getValue())/60);
                    String[] tokens = tenca.split(" ");
                    String maca = "";
                    for (String a: tokens){
                        maca +=a.charAt(0);
                    }
                    caLamViecArrayList.add(new CaLamViec(id,maca,tenca,tgNghi));
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }
        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1){
                LayoutInflater inflater = (LayoutInflater)ThoiGianNguyenCongActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(ThoiGianNguyenCongActivity.this)
                        .setTitle("Chọn ca làm việc:")
                        .setIcon(R.drawable.logo)
                        .setView(v)
                        .setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                builder.show();
                builder.findViewById(R.id.editSearch).setVisibility(View.GONE);
                builder.findViewById(R.id.allMayCheckBox).setVisibility(View.GONE);
                final CaLamViecAdapter adt = new CaLamViecAdapter(ThoiGianNguyenCongActivity.this, R.layout.item_buoc, caLamViecArrayList);
                ListView lv = (ListView) builder.findViewById(R.id.list_may);
                lv.setAdapter(adt);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        DialogDoiCaLamViec((CaLamViec) adt.getItem(i));
                        builder.dismiss();
                    }
                });
            } else {
                Toast.makeText(ThoiGianNguyenCongActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }
            progressBar.dismiss();
        }
    }

    void DialogDoiCaLamViec(final CaLamViec caLamViec){
        AlertDialog.Builder builder = new AlertDialog.Builder(ThoiGianNguyenCongActivity.this);
        builder.setMessage("Bạn có muốn đổi sang ca "+caLamViec.TenCa+" làm việc không?")
                .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateCalamViecTask().execute(caLamViec);
                    }
                })
                .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nhanVien.caLamViec = caLamViec;
                        ((TextView) findViewById(R.id.textViewChonCa)).setText(nhanVien.caLamViec.MaCaLV);
                        nhanCongs.get(0).TimeNhanCong = nhanVien.caLamViec.GioNghi;
                        UpdateTimeCaLamViec();
                    }
                })
                .show();
    }

    class UpdateTimeCVTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        int bienTam;
        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(ThoiGianNguyenCongActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            bienTam = 0;
            try {
                bienTam = Integer.parseInt(params[0]);
                if (nhanCongs.get(1).id == -1) {
                    tempCongViec = new CongViec();
                    tempCongViec.CongViecPhu = new CongViecPhu();
                    OEntity CongViecKhacs = consumer.getEntities("CongViecPhus").filter("MaSo eq 'NKR'").top(1).execute().first();
                    tempCongViec.CongViecPhu.Id = CongViecKhacs.getProperty("Id", Integer.class).getValue();
                    tempCongViec.CongViecPhu.Ten = CongViecKhacs.getProperty("Ten", String.class).getValue();
                    tempCongViec.CongViecPhu.MaSo = CongViecKhacs.getProperty("MaSo", String.class).getValue();
                    tempCongViec.PhanLoai = 2;
                    tempCongViec.GioBD = new LocalDateTime();
                    tempCongViec.GioKT = new LocalDateTime();
                    tempCongViec.GioNguyenCong = bienTam;
                    OEntity newCongViec = consumer.createEntity("CongViecs")
                            .properties(OProperties.int32("CongViec_NhanVien", nhanVien.Id))
                            .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                            .properties(OProperties.datetime("GioKT", tempCongViec.GioKT))
                            .properties(OProperties.int32("ThoiGianNhanCong", tempCongViec.GioNguyenCong))
                            .properties(OProperties.int32("CongViec_CongViecPhu", tempCongViec.CongViecPhu.Id))
                            .execute();
                    tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                } else {
                    tempCongViec = nhanVien.congViecs.get(nhanCongs.get(1).id);
                    OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                    consumer.updateEntity(CongViecs)
                            .properties(OProperties.int32("ThoiGianNhanCong",bienTam))
                            .properties(OProperties.string("GhiChu"," updateGhiChu"))
                            .execute();
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    if (nhanCongs.get(1).id == -1) {
                        nhanCongs.get(1).TimeNhanCong = bienTam;
                        nhanVien.congViecs.add(tempCongViec);
                        UpdateTimeCaLamViec();
                        nhanCongAdapter.notifyDataSetChanged();
                    } else {
                        tempCongViec.GioNguyenCong = bienTam;
                        nhanCongs.get(1).TimeNhanCong = bienTam;
                        UpdateTimeCaLamViec();
                        nhanCongAdapter.notifyDataSetChanged();
                    }
                    break;
                case -1:
                    Toast.makeText(ThoiGianNguyenCongActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class UpdateCalamViecTask extends AsyncTask<CaLamViec, Long, Integer> {
        MyProgressDialog progressBar;
        CaLamViec caLamViec;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(ThoiGianNguyenCongActivity.this);
        }

        @Override
        protected Integer doInBackground(CaLamViec... params) {

            try {
                caLamViec = params[0];
                OEntity Nhanvien = consumer.getEntity("NhanViens",nhanVien.Id).execute();
                consumer.updateEntity(Nhanvien)
                        .properties(OProperties.int32("NhanVien_CaLamViec",caLamViec.Id))
                        .execute();
                return 1;
            } catch (Exception ex) {
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    nhanVien.caLamViec = caLamViec;
                    ((TextView) findViewById(R.id.textViewChonCa)).setText(nhanVien.caLamViec.MaCaLV);
                    nhanCongs.get(0).TimeNhanCong = nhanVien.caLamViec.GioNghi;
                    UpdateTimeCaLamViec();
                    break;
                case -1:
                    Toast.makeText(ThoiGianNguyenCongActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class GhiChuTimeTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        String updateGhiChu;
        int timeNC,position;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(ThoiGianNguyenCongActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                position =Integer.parseInt(params[0]);

                try {
                    timeNC = Integer.parseInt(params[1]);
                } catch (Exception ex1){ }

                updateGhiChu = params[2];  // Update dữ liệu thời gian nhân công kiểu int32 và ghi chú lên database
                tempCongViec = nhanVien.congViecs.get(nhanCongs.get(position).id);
                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.int32("ThoiGianNhanCong",timeNC))
                        .properties(OProperties.string("GhiChu", updateGhiChu))
                        .execute();
                return 1;
            } catch (Exception ex) {
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    nhanCongs.get(position).GhiChu = updateGhiChu; // dòng này để cập nhật hiển thị tại màn hình này thôi nè
                    tempCongViec.GhiChu = updateGhiChu;  // cập nhật dữ liệu ghi chú cho list công việc
                    nhanCongs.get(position).TimeNhanCong = timeNC; // dòng này để cập nhật hiển thị tại màn hình này thôi nè
                    tempCongViec.GioNguyenCong = timeNC; // cập nhật dữ liệu ghi chú cho list công việc
                    UpdateTimeCaLamViec();
                    nhanCongAdapter.notifyDataSetChanged();
                    break;  // làm xong việc thì thoát ra để làm việc khác không thì nghỉ khỏe
                case -1:
                    Toast.makeText(ThoiGianNguyenCongActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }
}