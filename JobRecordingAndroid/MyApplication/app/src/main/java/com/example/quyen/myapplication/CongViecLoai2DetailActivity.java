package com.example.quyen.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.core4j.Enumerable;
import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.quyen.myapplication.CongViecActivity.LaChiTietHL;

public class CongViecLoai2DetailActivity extends AppCompatActivity {

    CongViec congViec;
    private BroadcastReceiver mReceiver;
    ODataConsumer consumer;

    ListView lvHinh;
    HinhDinhKemAdapter adapterHinh;
    byte[] arrayHinh;
    int tempPos;
    static final int REQUEST_IMAGE_CAPTURE = 123;
    public static final String TAG = CongViecLoai2DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cong_viec_loai2_detail);

        Intent intent = getIntent();
        congViec = (CongViec) intent.getSerializableExtra("CongViec");
        lvHinh = (ListView) findViewById(R.id.lvHinh);
        adapterHinh = new HinhDinhKemAdapter(this, R.layout.item_hinh_dinh_kem, congViec.HinhDinhKems);
        lvHinh.setAdapter(adapterHinh);
        lvHinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hienthihinh(adapterHinh.getItem(i));
            }
        });
        updateCongViec();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnMenu = (Button)findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(CongViecLoai2DetailActivity.this, v);
                // Inflate the menu from xml
                popup.getMenuInflater().inflate(R.menu.popup_cong_viec2_detail, popup.getMenu());
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_sua_cv_phu:
                                OnBtnSuaCvPhuClick();
                                return true;
                            case R.id.item_chup_hinh:
                                ActivityCompat.requestPermissions(CongViecLoai2DetailActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                                return  true;
                            case R.id.item_sua_dtglv:
                                suaThongTinCongViecKhac();
                                return  true;
                            case R.id.item_xoa:
                                OnBtnXoaClick();
                                return  true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        if (congViec.GioKT == null) {
            findViewById(R.id.lineGioNC).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.tvGioNC)).setText(congViec.GioNguyenCong+"'");
        }
        ((TextView) findViewById(R.id.tvGhiChu)).setText(congViec.GhiChu);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        String serviceUrl= Setting.ServerUrl;
        OClientBehavior basicAuth = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(basicAuth).build();

    }

    void Hienthihinh(HinhDinhKem hinhDinhKem){
        LayoutInflater inflater = (LayoutInflater) CongViecLoai2DetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_hinh_dinh_kem, null);
        final AlertDialog builder = new AlertDialog.Builder(CongViecLoai2DetailActivity.this)
                .setView(v)
                .create();
        builder.show();

        final Bitmap bitmap = BitmapFactory.decodeByteArray(hinhDinhKem.Data, 0, hinhDinhKem.Data.length);
        ((ImageView) builder.findViewById(R.id.imageViewHinh)).setImageBitmap(bitmap);
        ((Button)  builder.findViewById(R.id.buttonChupLai)).setText("Lưu");
        builder.findViewById(R.id.buttonChupLai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.findViewById(R.id.buttonXacNhan).setVisibility(View.INVISIBLE);
        builder.findViewById(R.id.buttonHuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    void DialogChupHinh(Bitmap bitmap){
        LayoutInflater inflater = (LayoutInflater) CongViecLoai2DetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_hinh_dinh_kem, null);
        final AlertDialog builder = new AlertDialog.Builder(CongViecLoai2DetailActivity.this)
                .setView(v)
                .create();
        builder.show();
        ((ImageView) builder.findViewById(R.id.imageViewHinh)).setImageBitmap(bitmap);
        builder.findViewById(R.id.buttonChupLai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(CongViecLoai2DetailActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                builder.dismiss();
            }
        });
        builder.findViewById(R.id.buttonXacNhan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) builder.findViewById(R.id.imageViewHinh)).getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                arrayHinh = byteArrayOutputStream.toByteArray();
                new UpdateHinhTask().execute();
                builder.dismiss();
            }
        });
        builder.findViewById(R.id.buttonHuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    void suaThongTinCongViecKhac() { // Thêm công việc
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_cong_viec_phu, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Đối tượng làm việc")
                .create();
        dialog.show();
        dialog.findViewById(R.id.item_LSX).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonMaLSX();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.item_May).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChonMayTask().execute();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.item_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan("Quét chi tiết");
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.item_extra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnBtnGhiChuClickNew();
                dialog.dismiss();
            }
        });
    }

    private void scan(String prompt) {
        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class)
                .setPrompt(prompt)
                .initiateScan();
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

    public void chonMaLSX() { // Thêm công việc
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_chon_lxs, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Mã Lệnh Sản Xuất")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText maLSX = (EditText) ((AlertDialog)dialog).findViewById(R.id.editTextLSX);
                        new ChonLSXTask().execute(maLSX.getText().toString().trim());
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void OnBtnGhiChuClickNew() { // Dialog nhập thông tin ghi chú và giờ nhân công thay đổi, giờ nhân công chỉ dc thiết lập khi công việc đã được bấm nút kết thúc
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_ghi_chu, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Ghi chú")
                .setIcon(R.drawable.logo)
                .create();

        dialog.show();
        dialog.findViewById(R.id.btnOkGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText edtdtglv = (EditText) dialog.findViewById(R.id.edtDTgLV);
                    EditText ghichu = (EditText) dialog.findViewById(R.id.editGhiChu);
                    new UpdateDTgLamViecTask().execute(edtdtglv.getText().toString().trim(),ghichu.getText().toString().trim());
                } catch(Exception ex){ }
                dialog.dismiss();
            }
        });

        ((EditText) dialog.findViewById(R.id.edtDTgLV)).setText(congViec.DoiTuongLD);
        ((EditText) dialog.findViewById(R.id.editGhiChu)).setText(congViec.GhiChu);

        if (true){
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lineTimeNC);
            a.setVisibility(View.GONE);
            Button btn  =  (Button) dialog.findViewById(R.id.btnCameraGhiChu);
            btn.setVisibility(View.INVISIBLE);
        }

        dialog.findViewById(R.id.btnHuyGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateCongViec() {
        TextView tvCvPhu = (TextView) findViewById(R.id.tvCvPhu);
        tvCvPhu.setText(congViec.CongViecPhu.Ten);
        TextView tvDTLV = (TextView) findViewById(R.id.tvDTLV);
        tvDTLV.setText(congViec.DoiTuongLD);
        ((TextView) findViewById(R.id.tvGhiChu)).setText(congViec.GhiChu);
        if (congViec.DoiTuongLD != null){
            if (congViec.DoiTuongLD.length() != 0) {
                LinearLayout lineDTLV = (LinearLayout) findViewById(R.id.lineDTLV);
                lineDTLV.setVisibility(View.VISIBLE);
            } else {
                LinearLayout lineDTLV = (LinearLayout) findViewById(R.id.lineDTLV);
                lineDTLV.setVisibility(View.GONE);
            }
        } else {
            LinearLayout lineDTLV = (LinearLayout) findViewById(R.id.lineDTLV);
            lineDTLV.setVisibility(View.GONE);
        }
    }

    private void OnBtnXoaClick() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        new XoaCvTask().execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn xóa?").setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
    }

    void OnBtnSuaCvPhuClick(){
        new ChonCvPhuTask().execute();
    }

    public void OnBtnXoaHinhClick(int ref) {
        final int pos = ref;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        tempPos = pos;
                        new XoaHinhTask().execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn xóa?").setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();

    }

    class UpdateHinhTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        HinhDinhKem tempHinh;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {

                int id = congViec.Id;
                tempHinh = new HinhDinhKem();
                tempHinh.Data = arrayHinh;

                OEntity newHinh = consumer.createEntity("HinhDinhKems")
                        .properties(OProperties.int32("HinhDinhKem_CongViec", id))
                        .properties(OProperties.binary("Hinh", tempHinh.Data))
                        .execute();
                tempHinh.Id =  newHinh.getProperty("Id", Integer.class).getValue();
                return 1;
            } catch (Exception ex) {
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    congViec.HinhDinhKems.add(tempHinh);
                    adapterHinh.notifyDataSetChanged();
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Đã Update hình ảnh thành công.", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class ChonLSXTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<May> choiceList = new ArrayList<May>();

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Enumerable<OEntity> lenhSanXuats;
                if (params[0] !=null){
                    lenhSanXuats = consumer.getEntities("LenhSanXuats")
                            .filter("startswith(MaSo, '"+params[0]+"')")
                            .execute();
                } else {
                    lenhSanXuats  = consumer.getEntities("LenhSanXuats")
                            .execute();
                }
                for (OEntity lsx : lenhSanXuats){
                    String maSo = lsx.getProperty("MaSo", String.class).getValue();
                    boolean ngayHTGC = lsx.getProperty("NgayHTGC", LocalDateTime.class).getValue() != null?true:false;
                    if (ngayHTGC == false){
                        choiceList.add(new May(1,maSo,1));
                    }
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    if (!choiceList.isEmpty()){
                        LayoutInflater inflater = (LayoutInflater)CongViecLoai2DetailActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.dialog_chon_may, null);
                        final AlertDialog builder = new AlertDialog.Builder(CongViecLoai2DetailActivity.this)
                                .setTitle("Chọn máy")
                                .setIcon(R.drawable.logo)
                                .setView(v)

                                .setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                        builder.show();

                        final EditText editsearch = (EditText) builder.findViewById(R.id.editSearch);
                        ((CheckBox)builder.findViewById(R.id.allMayCheckBox)).setVisibility(View.GONE);
                        final MayAdapter adt = new MayAdapter(CongViecLoai2DetailActivity.this, R.layout.item_may, choiceList);

//                        Capture Text in EditText
                        editsearch.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                                adt.getFilter().filter(": " + text);
                                adt.notifyDataSetChanged();
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1,
                                                          int arg2, int arg3) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                                // TODO Auto-generated method stub
                            }
                        });
                        ListView lv = (ListView) builder.findViewById(R.id.list_may);
                        lv.setAdapter(adt);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Toast.makeText(CongViecLoai2DetailActivity.this, adt.getItem(i).MaSo + "", Toast.LENGTH_SHORT).show();
                                new UpdateDTgLamViecTask().execute(adt.getItem(i).MaSo + "","");
                                builder.dismiss();
                            }
                        });
                    } else {
                        chonMaLSX();
                        Toast.makeText(CongViecLoai2DetailActivity.this, "Mã Không đúng, vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -1:
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class ChonCvPhuTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<CongViecPhu> choiceList = new ArrayList<CongViecPhu>();

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {

                for (OEntity ct : consumer.getEntities("CongViecPhus").execute()) {
                    CongViecPhu newNc = new CongViecPhu();
                    newNc.Id = ct.getProperty("Id", Integer.class).getValue();
                    newNc.Ten = ct.getProperty("Ten", String.class).getValue();
                    newNc.MaSo = ct.getProperty("MaSo", String.class).getValue();
                    choiceList.add(newNc);
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                LayoutInflater inflater = (LayoutInflater)CongViecLoai2DetailActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(CongViecLoai2DetailActivity.this)
                        .setTitle("Chọn công việc")
                        .setIcon(R.drawable.logo)
                        .setView(v)

                        .setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                builder.show();

                final EditText editsearch = (EditText) builder.findViewById(R.id.editSearch);
                builder.findViewById(R.id.allMayCheckBox).setVisibility(View.GONE);
                final CongViecPhuAdapter adt = new CongViecPhuAdapter(CongViecLoai2DetailActivity.this, R.layout.item_cong_viec_phu, choiceList);
//                 Capture Text in EditText
                editsearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                        adt.getFilter().filter(text);
                        adt.notifyDataSetChanged();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                        // TODO Auto-generated method stub
                    }
                });

                ListView lv = (ListView) builder.findViewById(R.id.list_may);
                lv.setAdapter(adt);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        new UpdateCvPhuTask().execute(adt.getItem(i));
                        builder.dismiss();
                    }
                });
            } else {
                Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }


            progressBar.dismiss();
        }
    }

    class ChonMayTask extends AsyncTask<Integer, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<May> choiceList = new ArrayList<May>();

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {

                for (OEntity may : consumer.getEntities("Mays")
                        .orderBy("MaSo")
                        .execute())
                {
                    Integer temp = may.getProperty("May_ToSX", Integer.class).getValue();

                    May m = new May(
                            may.getProperty("Id", Integer.class).getValue(),
                            may.getProperty("MaSo", String.class).getValue(),
                            temp != null ? temp : 0
                    );
                    choiceList.add(m);
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                LayoutInflater inflater = (LayoutInflater)CongViecLoai2DetailActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(CongViecLoai2DetailActivity.this)
                        .setTitle("Chọn máy")
                        .setIcon(R.drawable.logo)
                        .setView(v)
                        .setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                builder.show();

                final EditText editsearch = (EditText) builder.findViewById(R.id.editSearch);
                final CheckBox checkBox = (CheckBox)builder.findViewById(R.id.allMayCheckBox);
                final MayAdapter adt = new MayAdapter(CongViecLoai2DetailActivity.this, R.layout.item_may, choiceList);

                // Capture Text in EditText
                editsearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                        adt.getFilter().filter(": " + text);
                        adt.notifyDataSetChanged();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                        // TODO Auto-generated method stub
                    }
                });

                ListView lv = (ListView) builder.findViewById(R.id.list_may);
                lv.setAdapter(adt);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,int which, long arg3)
                    {
                        Toast.makeText(CongViecLoai2DetailActivity.this, adt.getItem(which).MaSo+"", Toast.LENGTH_SHORT).show();
                        new UpdateDTgLamViecTask().execute(adt.getItem(which).MaSo,"");
                        builder.dismiss();
                    }
                });

                checkBox.setChecked(true);
                checkBox.setVisibility(View.GONE);

            } else {
                Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }

            progressBar.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE ){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                DialogChupHinh(bitmap);
            }
            return;
        }
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String ma = result.getContents();
                new ChonMaTask().execute(ma);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class ChonMaTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<May> choiceList = new ArrayList<May>();
        String maChiTiet;

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                for (OEntity ct : consumer.getEntities("ChiTiets")
                        .filter("Id eq " + Integer.parseInt(params[0]))
                        .execute()) {
                    String ma = ct.getProperty("MaSo", String.class).getValue();
                    maChiTiet = ma;
                }
                return 2;
            } catch (Exception ex1){}
            try {
                choiceList = new ArrayList<May>();
                if (LaChiTietHL( params[0])) {
                    for (OEntity ct : consumer.getEntities("ChiTietHLs")
                            .filter("startswith(TenNgan, '"+params[0]+"')")
                            .execute()) {

                        String ma = ct.getProperty("TenNgan", String.class).getValue();

                        choiceList.add(new May(0, ma, 0));
                    }
                } else {
                    for (OEntity ct : consumer.getEntities("ChiTiets")
                            .filter("startswith(MaSo, '"+params[0]+"')")
                            .execute()) {

                        String ma = ct.getProperty("MaSo", String.class).getValue();

                        choiceList.add(new May(0, ma, 0));
                    }
                }

                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                if (choiceList.size()>1) {
                    LayoutInflater inflater = (LayoutInflater) CongViecLoai2DetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.dialog_chon_may, null);
                    final AlertDialog builder = new AlertDialog.Builder(CongViecLoai2DetailActivity.this)
                            .setTitle("Chọn chi tiết")
                            .setView(v)
                            .setIcon(R.drawable.logo)
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();

                    builder.show();
                    builder.findViewById(R.id.editSearch).setVisibility(View.GONE);
                    builder.findViewById(R.id.allMayCheckBox).setVisibility(View.GONE);
                    final MayAdapter adt = new MayAdapter(CongViecLoai2DetailActivity.this, R.layout.item_may, choiceList);
                    ListView lv = (ListView) builder.findViewById(R.id.list_may);
                    lv.setAdapter(adt);
                    // Capture Text in EditText

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
                            May may = adt.getItem(which);
                            new UpdateDTgLamViecTask().execute(may.MaSo,"");
                            Toast.makeText(CongViecLoai2DetailActivity.this, may.MaSo + "", Toast.LENGTH_SHORT).show();
                            builder.dismiss();
                        }
                    });
                } else {
                    if (choiceList.size() ==1){
                        new UpdateDTgLamViecTask().execute(choiceList.get(0).MaSo,"");
                        Toast.makeText(CongViecLoai2DetailActivity.this,"Scanned "+ choiceList.get(0).MaSo, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CongViecLoai2DetailActivity.this, "Không có mã", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                if ( result == 2) {
                    new UpdateDTgLamViecTask().execute(maChiTiet,"");
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Scanned "+maChiTiet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.dismiss();
        }
    }

    class UpdateDTgLamViecTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        String dTglv,ghiChu;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                dTglv = params[0];
                ghiChu = params[1];
                OEntity CongViecs = consumer.getEntity("CongViecs",congViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.string("DoiTuongLamViec", dTglv))
                        .properties(OProperties.string("GhiChu", ghiChu))
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
                    congViec.DoiTuongLD = dTglv;
                    congViec.GhiChu = ghiChu;
                    updateCongViec();
                    break;
                case -1:
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class UpdateCvPhuTask extends AsyncTask<CongViecPhu, Long, Integer> {
        MyProgressDialog progressBar;
        CongViecPhu tempCvPhu;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(CongViecPhu... params) {

            try {
                tempCvPhu = params[0];

                OEntity CongViecs = consumer.getEntity("CongViecs",congViec.Id).execute();

                consumer.updateEntity(CongViecs)
                        .properties(OProperties.int32("CongViec_CongViecPhu", tempCvPhu.Id))
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
                    congViec.CongViecPhu = tempCvPhu;
                    updateCongViec();
                    break;
                case -1:
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class XoaCvTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                consumer.deleteEntity("CongViecs", congViec.Id)
                        .ifMatch("*")
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
                    congViec = null;
                    onBackPressed();
                    break;
                case -1:
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class XoaHinhTask extends AsyncTask<Integer, Long, Integer> {
        MyProgressDialog progressBar;
        HinhDinhKem hinh;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecLoai2DetailActivity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            try {
                hinh = congViec.HinhDinhKems.get(tempPos);

                consumer.deleteEntity("HinhDinhKems", hinh.Id)
                        .ifMatch("*")
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
                    congViec.HinhDinhKems.remove(hinh);
                    adapterHinh.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecLoai2DetailActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }

            progressBar.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(this, "Bạn không cho phép mở Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("CongViec", congViec);
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
}
