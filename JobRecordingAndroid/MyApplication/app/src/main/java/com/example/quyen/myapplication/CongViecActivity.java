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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.core.ORelatedEntitiesLinkInline;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CongViecActivity extends AppCompatActivity {

    ListView lvCongViec;
    CongViecAdapter adapter;
    ODataConsumer consumer;
    NhanVien nhanVien;
    CongViec congViecNew;
    String tempChiTiet;
    NguyenCong tempNguyenCong;
    String tempBuoc;
    CongViecPhu tempCvPhu;
    May tempMay;
    int tempPos;
    byte[] byteArray;

    public static final String TAG = CongViecActivity.class.getSimpleName();

    public  static final int CONG_VIEC = 100;
    public  static final int NHAN_VIEN = 200;

    static final int REQUEST_IMAGE_CAPTURE = 123;

    private BroadcastReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cong_viec);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themCongViec(view);
            }
        });
        Intent intent = getIntent();
        nhanVien = (NhanVien) intent.getSerializableExtra("NhanVien");
        TextView tvMaSo = (TextView)findViewById(R.id.tvMaSo);
        TextView tvHoTen = (TextView)findViewById(R.id.tvHoTen);
        tvMaSo.setText(nhanVien.MaSo);
        tvHoTen.setText(nhanVien.HoTen);
        LinearLayout layout = (LinearLayout)findViewById(R.id.grNhanVien);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor((0xFF << 24) | Integer.decode(nhanVien.Color));
        layout.setBackground(drawable);

        lvCongViec = (ListView) findViewById(R.id.lvCongViec);
        adapter = new CongViecAdapter(this, R.layout.item_cong_viec_loai1, nhanVien.congViecs);
        lvCongViec.setAdapter(adapter);

        lvCongViec.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                CongViec cv = nhanVien.congViecs.get(position);
                if (cv.PhanLoai == 1) {
                    Intent intent = new Intent(CongViecActivity.this, CongViec2Activity.class);
                    intent.putExtra("CongViec", cv);
                    tempPos = position;
                    startActivityForResult(intent, CONG_VIEC);
                }
                else if (cv.PhanLoai == 2){
                    if (!cv.CongViecPhu.MaSo.equals("NKR") && !cv.CongViecPhu.MaSo.equals("NVR")) {
                        Intent intent = new Intent(CongViecActivity.this, CongViecLoai2DetailActivity.class);
                        intent.putExtra("CongViec", cv);
                        tempPos = position;
                        startActivityForResult(intent, CONG_VIEC);
                    }
                }
            }
        });

        scrollEnd();

        String serviceUrl= Setting.ServerUrl; // đường dẫn liên kết
        OClientBehavior basicAuth = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(basicAuth).build(); // Tạo liên kết

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void scrollEnd() {
        lvCongViec.post(new Runnable() {
            @Override
            public void run() {
                lvCongViec.setSelection(lvCongViec.getCount()-1);
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

    public void doiMatKhau(View view) { // Đổi mật khẩu đăng nhập của nhân viên

        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_doi_mat_khau, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Đổi mật khẩu")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText matKhauTxt = (EditText) ((AlertDialog)dialog).findViewById(R.id.editMatKhau);
                        EditText matKhau2Txt = (EditText) ((AlertDialog)dialog).findViewById(R.id.editMatKhau2);

                        String matKhau = matKhauTxt.getText().toString();
                        String matKhau2 = matKhau2Txt.getText().toString();
                        if (((EditText) ((AlertDialog)dialog).findViewById(R.id.editMatKhauCu)).getText().toString().trim().equals(nhanVien.MatKhau)) {
                            if (!matKhau.equals(nhanVien.MatKhau)) {
                                if (matKhau.length() == 4) {
                                    if (matKhau.equals(matKhau2)) {
                                        new DoiMatKhauTask().execute(matKhau);
                                    } else
                                        Toast.makeText(CongViecActivity.this, "Mã xác nhận không giống.", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(CongViecActivity.this, "Vui lòng nhập 4 mã số bí mật làm mật khẩu.", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(CongViecActivity.this, "Mật khẩu mới khác mật khẩu cũ.", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(CongViecActivity.this, "Sai mật khẩu.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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

        EditText matKhauTxt = (EditText) dialog.findViewById(R.id.editMatKhau);
        matKhauTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public void themThongTinCongViecKhac() { // Thêm công việc
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_cong_viec_phu, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Đối tượng làm việc")
                .setIcon(R.drawable.logo)
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

    public void themCongViec(View view) { //Mở menu thêm công việc
        PopupMenu popup = new PopupMenu(this, view);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup_filter, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.item_chinh:
                        congViecNew = null;
                        themCongViecChinh();
                        return true;
                    case R.id.item_phu:
                        congViecNew =null;
                        themCongViecPhu();
                        return true;
                    case R.id.item_thongbaohong:
                        congViecNew =null;
                        congViecNew = new CongViec();
                        congViecNew.PhanLoai = 3;
                        congViecNew.ChiTiet = "Thông báo máy hỏng";
                        new ChonMayTask().execute();
                        return true;
                    case R.id.item_thongbaottm:
                        congViecNew =null;
                        congViecNew = new CongViec();
                        congViecNew.PhanLoai = 3;
                        congViecNew.ChiTiet = "Thông báo máy chờ";
                        new ChonMayTask().execute();
                        return true;
                    case R.id.item_nhancong:
                        congViecNew =null;
                        Intent intent = new Intent(CongViecActivity.this,ThoiGianNguyenCongActivity.class);
                        intent.putExtra("NhanVien",nhanVien);
                        startActivityForResult(intent, NHAN_VIEN);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Show the menu
        popup.show();
    }

    private void themCongViecChinh(){
        //state = THEM_CONG_VIEC_CHI_TIET; // them
        scan("Quét mã chi tiết");

    }

    private void themCongViecPhu() {
        //state = THEM_CONG_VIEC_PHU;
        new ChonCvPhuTask().execute();
    }

    private void scan(String prompt) {
        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class)
                .setPrompt(prompt)
                .initiateScan();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 1 đống thứ trả về cần xử lí, đây là màn hình xử lí chính
        if (requestCode == CONG_VIEC){
            handleCongViec2Activity(requestCode, resultCode, data);
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE ){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                DialogChupHinh(bitmap);
            }
            return;
        }

        if (requestCode == NHAN_VIEN && resultCode == RESULT_OK ){
            NhanVien nvnew = (NhanVien) data.getSerializableExtra("NhanVien");
            if (nhanVien.congViecs.size() < nvnew.congViecs.size()){
                for (int i = nhanVien.congViecs.size(); i<nvnew.congViecs.size();i++){
                    nhanVien.congViecs.add(nvnew.congViecs.get(i));
                }
            }
            for (int i=0;i<nhanVien.congViecs.size();i++){
                nhanVien.congViecs.get(i).GhiChu = nvnew.congViecs.get(i).GhiChu;
                nhanVien.congViecs.get(i).GioNguyenCong = nvnew.congViecs.get(i).GioNguyenCong;
            }
            try {
                nhanVien.caLamViec = nvnew.caLamViec;
            } catch (Exception ex){
            }
            adapter.notifyDataSetChanged();
            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else            {

                String ma = result.getContents();
                new  ChonMaTask().execute(ma);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleCongViec2Activity(int requestCode, int resultCode, Intent data) { // Dữ liệu từ màn hình công việc gia công trả về
        if (resultCode == RESULT_OK) {
            CongViec cv = (CongViec) data.getSerializableExtra("CongViec");
            nhanVien.congViecs.set(tempPos, cv);
            if (cv == null)
                nhanVien.congViecs.remove(tempPos);
            adapter.notifyDataSetChanged();
        }
    }

    public void OnBtnThemSLClick(final int position) { // Thêm số lượng đạt, không đạt và số lượng đang làm lở dở để người khác còn biết

        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_so_luong, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setIcon(R.drawable.logo)
                .setTitle("Thêm số lượng")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int slGood =0;
                        int slDoDang = 0;
                        int slNoGood = 0;

                        EditText good = (EditText) ((AlertDialog)dialog).findViewById(R.id.editGood);
                        EditText doDang =(EditText) ((AlertDialog)dialog).findViewById(R.id.editDoDang);
                        EditText noGood = (EditText) ((AlertDialog)dialog).findViewById(R.id.editNoGood);

                        try {
                            slGood = Integer.parseInt(good.getText().toString());
                        } catch(Exception ex){ }

                        try {
                            slDoDang = Integer.parseInt(doDang.getText().toString());
                        } catch(Exception ex){ }

                        try {
                            slNoGood = Integer.parseInt(noGood.getText().toString());
                        } catch (Exception ex){ }

                        CongViecActivity.this.ThemSoLuong(position, slGood,slDoDang, slNoGood);

                        dialog.dismiss();
                    }

                })
                // Set the negative/no button click listener
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .create();

        dialog.show();
        if (nhanVien.congViecs.get(position).TongDoDang!=null){
            ((EditText) dialog.findViewById(R.id.editDoDang)).setText(nhanVien.congViecs.get(position).TongDoDang==0?"":nhanVien.congViecs.get(position).TongDoDang+"");
        }
        if (nhanVien.congViecs.get(position).GioKT == null){
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.lineedtDoDang);
            linearLayout.setVisibility(View.GONE);
        }
        EditText good = (EditText) dialog.findViewById(R.id.editGood);
        good.setText(Integer.toString(nhanVien.SlgMacDinh));
        good.selectAll();
        good.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

    }

    public void DungSetBDChay(int position){   // 1 nút tích hợp dùng setup và bắt đầu chạy
        tempPos = position;
        CongViec tempCongViec;
        tempCongViec = nhanVien.congViecs.get(tempPos);
        if (tempCongViec.GioKTSetup != null){
            new BatDauChayTask().execute();
        } else {
            new KetThucSetupTask().execute();
        }
    }

    public  void TamDungTiepTuc(final int position, int dem){ // nút này lại tích hợp 2 lần tạm dừng và 2 lần tiếp tục thực hiện, đang làm mà muốn uống tà tữa thì làm gì nè, dừng tí uống cho mát rồi làm chứ sao nữa.
        tempPos = position;
        switch (dem){
            case 0:
                new TamDung1Task().execute();
                break;
            case 1:
                new TiepTuc1Task().execute();
                break;
            case 2:
                new TamDung2Task().execute();
                break;
            case 3:
                new TiepTuc2Task().execute();
                break;
        }
    }

    private void ThemSoLuong(int position, Integer slGood,Integer slDoDang, Integer slNoGood) { // Liên kết đến hàm đẩy dữ liệu lên database thôi
        tempPos = position;
        new UpdateSoLuongTask().execute(slGood,slDoDang,slNoGood);
    }

    private void SuaGio(int position, String gioBD,String gioKTSetup,String gioBDChay,String gioTD1,String gioTT1,String gioTD2,String gioTT2 , String gioKT,String timeNC, String ghichu) { // Liên kết đến hàm đẩy dữ liệu lên database thôi
        tempPos = position;
        new UpdateGioTask().execute(gioBD,gioKTSetup,gioBDChay,gioTD1,gioTT1,gioTD2,gioTT2, gioKT,timeNC, ghichu);
    }

    public void OnBtnKeThucClick(int ref) { // Liên kết đến hàm đẩy dữ liệu lên database thôi
        tempPos = ref;
        new KetThucTask().execute();
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
                    EditText dtglv = (EditText) dialog.findViewById(R.id.edtDTgLV);
                    congViecNew.DoiTuongLD = dtglv.getText().toString().trim();
                    EditText ghiChu = (EditText) dialog.findViewById(R.id.editGhiChu);
                    congViecNew.GhiChu = ghiChu.getText().toString().trim();
                    new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                } catch(Exception ex){ }
                dialog.dismiss();
            }
        });

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

    public void OnBtnGhiChuClick(final int ref) { // Dialog nhập thông tin ghi chú và giờ nhân công thay đổi, giờ nhân công chỉ dc thiết lập khi công việc đã được bấm nút kết thúc
        final int ref2 =ref;
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_ghi_chu, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Ghi chú")
                .setIcon(R.drawable.logo)
                .create();

        dialog.show();
        dialog.findViewById(R.id.lineDTgLV).setVisibility(View.GONE);
        dialog.findViewById(R.id.btnCameraGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText timeNC = (EditText) dialog.findViewById(R.id.edtTimeNC);
                    EditText ghichu = (EditText) dialog.findViewById(R.id.editGhiChu);
                    tempPos = ref2;
                    new GhiChuTask().execute(ghichu.getText().toString().trim(),timeNC.getText().toString());
                } catch(Exception ex){ }
                OnBtnChupHinhClick(ref2);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnOkGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText timeNC = (EditText) dialog.findViewById(R.id.edtTimeNC);
                    EditText ghichu = (EditText) dialog.findViewById(R.id.editGhiChu);
                    tempPos = ref2;
                    new GhiChuTask().execute(ghichu.getText().toString().trim(),timeNC.getText().toString());

                } catch(Exception ex){ }
                dialog.dismiss();
            }
        });

        if (nhanVien.congViecs.get(ref).GioKT == null){
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lineTimeNC);
            a.setVisibility(View.GONE);
        }

        dialog.findViewById(R.id.btnHuyGhiChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        EditText timeNC = (EditText) dialog.findViewById(R.id.edtTimeNC);
        EditText good = (EditText) dialog.findViewById(R.id.editGhiChu);
        timeNC.setText(nhanVien.congViecs.get(ref).GioNguyenCong==0?"":nhanVien.congViecs.get(ref).GioNguyenCong+"");
        good.setText(nhanVien.congViecs.get(ref).GhiChu);

        good.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    public void OnBtnChupHinhClick(int ref) {
        tempPos = ref;
        ActivityCompat.requestPermissions(CongViecActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
    }

    public void OnBtnDungClick(int ref) { // Dialog nhắc nhở nhấn Ok là nó kết thúc công việc thôi
        final int pos = ref;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        tempPos = pos;
                        new KetThucTask().execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn dừng?").setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
    }

    public void OnBtnSuaGioClick(int ref) { // sửa giờ lại thôi, và đây chỉ là nhắc nhở không nên sửa quá nhiều lần

        final int position = ref;
        AlertDialog d = new AlertDialog.Builder(this)
                .setMessage("Bạn nên nhập dữ liệu ngay khi bắt đầu hoặc kết thúc một công việc\nNếu sửa giờ quá nhiều bạn sẽ bị phạt :)")
                .setTitle("Cảnh báo")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CongViec congViec = nhanVien.congViecs.get(position);
                        if (congViec.PhanLoai == 1){
                            chonGioSua(position);
                        } else {
                            suaGio(position,true,true,true,true,true,true,true,true);
                        }
                    }
                }).create();

        d.show();
    }

    void chonGioSua(int ref){ // trước khi sửa giờ thì mình phải chọn cái gì mình muốn sữa chứ không là dài lắm à nghen
        final int position = ref;

        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_chon_gio_sua,null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Chọn thông tin muốn sửa: ")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CheckBox cbBD       = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxBD);
                        CheckBox cbKTSetup  = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxKTSetup);
                        CheckBox cbBDChay   = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxBDChay);
                        CheckBox cbTD1      = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxTD1);
                        CheckBox cbTT1      = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxTT1);
                        CheckBox cbTD2      = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxTD2);
                        CheckBox cbTT2      = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxTT2);
                        CheckBox cbKT       = (CheckBox) ((AlertDialog)dialogInterface).findViewById(R.id.checkboxKT);
                        suaGio(position,
                                cbBD.isChecked(),
                                cbKTSetup.isChecked(),
                                cbBDChay.isChecked(),
                                cbTD1.isChecked(),
                                cbTT1.isChecked(),
                                cbTD2.isChecked(),
                                cbTT2.isChecked(),
                                cbKT.isChecked());
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
        final CheckBox cbAll = (CheckBox) dialog.findViewById(R.id.checkboxAll);
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ((CheckBox) dialog.findViewById(R.id.checkboxBD)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxKTSetup)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxBDChay)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxTD1)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxTT1)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxTD2)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxTT2)).setChecked(true);
                    ((CheckBox) dialog.findViewById(R.id.checkboxKT)).setChecked(true);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxBD)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxKTSetup)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxBDChay)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxTD1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxTT1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxTD2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxTT2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        ((CheckBox) dialog.findViewById(R.id.checkboxKT)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    cbAll.setChecked(false);
                }
            }
        });
        CongViec cv = nhanVien.congViecs.get(position);
        if (cv.GioKTSetup == null) {
            ((CheckBox) dialog.findViewById(R.id.checkboxKTSetup)).setVisibility(View.GONE);
        }
        if (cv.GioBDChay == null) {
            CheckBox a = (CheckBox) dialog.findViewById(R.id.checkboxBDChay);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTamDung1 == null) {
            CheckBox a = (CheckBox) dialog.findViewById(R.id.checkboxTD1);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTiepTuc1 == null) {
            CheckBox a = (CheckBox) dialog.findViewById(R.id.checkboxTT1);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTamDung2 == null) {
            CheckBox a = (CheckBox) dialog.findViewById(R.id.checkboxTD2);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTiepTuc2 == null) {
            CheckBox a = (CheckBox) dialog.findViewById(R.id.checkboxTT2);
            a.setVisibility(View.GONE);
        }
        if (cv.GioKT == null) {
            CheckBox a = (CheckBox) dialog.findViewById(R.id.checkboxKT);
            a.setVisibility(View.GONE);
        }
    }

    void DialogChupHinh(Bitmap bitmap){
        LayoutInflater inflater = (LayoutInflater) CongViecActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_hinh_dinh_kem, null);
        final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
                .setView(v)
                .create();
        builder.show();
        ((ImageView) builder.findViewById(R.id.imageViewHinh)).setImageBitmap(bitmap);
        builder.findViewById(R.id.buttonChupLai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnBtnChupHinhClick(tempPos);
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
                byteArray = byteArrayOutputStream.toByteArray();
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

    void suaGio(int ref,Boolean... params){ // giờ mình nhập cái giờ mình cần sửa vào thôi, mà nhớ là phải đọc kĩ cái ghi chú ở đầu
        // à, nếu nhấn kết thúc thì ta lại thêm giờ nhân công vào luôn chứ, cái này thì cứ nhập số phút mà bạn tiêu tốn cho cái công việc này
        final int position = ref;

        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_sua_gio, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Sửa giờ")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final EditText bd = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioBD);
                        final EditText ktSetup = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioKTSetup);
                        final EditText bdChay = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioBDChay);
                        final EditText td1 = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioTD1);
                        final EditText tt1 = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioTT1);
                        final EditText td2 = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioTD2);
                        final EditText tt2 = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioTT2);
                        final EditText kt = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioKT);
                        final EditText gioNC = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGioNC);
                        final EditText ghichu = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGhiChu);

                        String gioBD = bd.getText().toString();
                        String gioKTSetup = ktSetup.getText().toString();
                        String gioBDChay = bdChay.getText().toString();
                        String gioTD1 = td1.getText().toString();
                        String gioTT1 = tt1.getText().toString();
                        String gioTD2 = td2.getText().toString();
                        String gioTT2 = tt2.getText().toString();
                        String gioKT = kt.getText().toString();
                        String thoiGianNC = gioNC.getText().toString();

                        CongViecActivity.this.SuaGio(position, gioBD, gioKTSetup , gioBDChay,gioTD1,gioTT1,gioTD2,gioTT2, gioKT,thoiGianNC, ghichu.getText().toString());

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

        CongViec cv = nhanVien.congViecs.get(position);
        final EditText bd = (EditText) dialog.findViewById(R.id.editGioBD);
        final EditText ktSetup = (EditText) dialog.findViewById(R.id.editGioKTSetup);
        final EditText bdChay = (EditText) dialog.findViewById(R.id.editGioBDChay);
        final EditText td1 = (EditText) dialog.findViewById(R.id.editGioTD1);
        final EditText tt1 = (EditText) dialog.findViewById(R.id.editGioTT1);
        final EditText td2 = (EditText) dialog.findViewById(R.id.editGioTD2);
        final EditText tt2 = (EditText) dialog.findViewById(R.id.editGioTT2);
        final EditText kt = (EditText) dialog.findViewById(R.id.editGioKT);
        final EditText gioNC = (EditText) dialog.findViewById(R.id.editGioNC);
        final EditText ghichu = (EditText) dialog.findViewById(R.id.editGhiChu);
        bd.setText(cv.GioBD_BS != null ? cv.GioBD_BS.toString("HH:mm"):"");
        ktSetup.setText(cv.GioKTSetup != null ? cv.GioKTSetup.toString("HH:mm"):"");
        bdChay.setText(cv.GioBDChay != null ? cv.GioBDChay.toString("HH:mm"):"");
        td1.setText(cv.GioTamDung1 != null ? cv.GioTamDung1.toString("HH:mm"):"");
        tt1.setText(cv.GioTiepTuc1 != null ? cv.GioTiepTuc1.toString("HH:mm"):"");
        td2.setText(cv.GioTamDung2 != null ? cv.GioTamDung2.toString("HH:mm"):"");
        tt2.setText(cv.GioTiepTuc2 != null ? cv.GioTiepTuc2.toString("HH:mm"):"");
        kt.setText(cv.GioKT_BS != null ? cv.GioKT_BS.toString("HH:mm") : "");
        gioNC.setText(cv.GioNguyenCong==0?"":+cv.GioNguyenCong+"");
        ghichu.setText(cv.GhiChu);
        if (!params[0]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextBD);
            a.setVisibility(View.GONE);
        }
        if (cv.GioKTSetup == null || !params[1]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextKTSetup);
            a.setVisibility(View.GONE);
        }
        if (cv.GioBDChay == null || !params[2]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextBDChay);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTamDung1 == null || !params[3]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextTD1);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTiepTuc1 == null || !params[4]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextTT1);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTamDung2 == null || !params[5]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextTD2);
            a.setVisibility(View.GONE);
        }
        if (cv.GioTiepTuc2 == null || !params[6]) {
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextTT2);
            a.setVisibility(View.GONE);
        }
        if (cv.GioKT == null || !params[7]) {
//            kt.setHint("Bạn chưa nhấn kết thúc");
//            kt.setEnabled(false);
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextKT);
            a.setVisibility(View.GONE);
        }
        if (cv.GioKT == null){
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextNC);
            a.setVisibility(View.GONE);
        }
        if (cv.PhanLoai == 2){
            LinearLayout a = (LinearLayout) dialog.findViewById(R.id.lnTextKTSetup);
            a.setVisibility(View.GONE);
            LinearLayout b = (LinearLayout) dialog.findViewById(R.id.lnTextBDChay);
            b.setVisibility(View.GONE);
        }

        bd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        //dialog.show();
    }

    public void OnBtnCopyClick(int ref) { // coppy 1 công việc i chang bạn đang chọn, nhưng trước hết phải nhắc nhở tránh bị ấn nhầm
        final int pos = ref;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        tempPos = pos;
                        CongViec curCongViec = nhanVien.congViecs.get(pos);

                        CongViec newCongViec = new CongViec();
                        newCongViec.ChiTiet = curCongViec.ChiTiet;
                        newCongViec.NguyenCong = curCongViec.NguyenCong;
                        newCongViec.Buocs = curCongViec.Buocs;
                        newCongViec.May = curCongViec.May;
                        newCongViec.PhanLoai = 1;
                        new CongViecActivity.ThemCongViecTask().execute(newCongViec);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn copy?").setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
    }

    public static boolean LaChiTietHL(String ma){
        return Character.isLetter(ma.charAt(0));
    }

    void OnBtnTrangThai(int ref){
        if (ref != -1) {
            tempPos = ref;
            congViecNew = new CongViec();
            congViecNew.ChiTiet = nhanVien.congViecs.get(ref).ChiTiet;
            congViecNew.DoiTuongLD = nhanVien.congViecs.get(ref).DoiTuongLD;
            congViecNew.PhanLoai = nhanVien.congViecs.get(ref).PhanLoai;
            congViecNew.Id = nhanVien.congViecs.get(ref).Id;
            congViecNew.NoiDung = nhanVien.congViecs.get(ref).NoiDung;
            congViecNew.GhiChu = nhanVien.congViecs.get(ref).GhiChu;
        }
        if (congViecNew.ChiTiet.equals("Thông báo máy hỏng")|| congViecNew.Id != 0) {
            OnBtnTinhTrangMay();
        } else if (congViecNew.ChiTiet.equals("Thông báo máy chờ")) {
            new ChonTrangThaiMayTask().execute();
        }
    }

    void OnBtnTinhTrangMay(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(congViecNew.ChiTiet.equals("Thông báo máy hỏng")?"Nhập tình trạng máy: ":"Nhập Mô Tả:");
        final EditText input = new EditText(this);
        input.setLines(3);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (congViecNew.ChiTiet.equals("Thông báo máy hỏng")){
                    congViecNew.NoiDung = input.getText().toString();
                } else if (congViecNew.ChiTiet.equals("Thông báo máy chờ")){
                    congViecNew.GhiChu = input.getText().toString();
                }
                dialog.dismiss();
                if (congViecNew.Id ==0) {
                    new ThemCongViecTask().execute(congViecNew);
                } else {
                    new UpdateFullCongViecTask().execute(congViecNew);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        AlertDialog dialog = alert.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    public void OnBtnSuaMayClick(int ref) {
        tempPos = ref;
        congViecNew = new CongViec();
        congViecNew.ChiTiet = nhanVien.congViecs.get(ref).ChiTiet;
        congViecNew.DoiTuongLD = nhanVien.congViecs.get(ref).DoiTuongLD;
        congViecNew.PhanLoai = nhanVien.congViecs.get(ref).PhanLoai;
        congViecNew.Id = nhanVien.congViecs.get(ref).Id;
        congViecNew.NoiDung = nhanVien.congViecs.get(ref).NoiDung;
        congViecNew.GhiChu = nhanVien.congViecs.get(ref).GhiChu;
        new ChonMayTask().execute();
    }

    public void OnBtnTrangThaiClick(int ref) {
        tempPos = ref;
        congViecNew = new CongViec();
        congViecNew.ChiTiet = nhanVien.congViecs.get(ref).ChiTiet;
        congViecNew.DoiTuongLD = nhanVien.congViecs.get(ref).DoiTuongLD;
        congViecNew.PhanLoai = nhanVien.congViecs.get(ref).PhanLoai;
        congViecNew.Id = nhanVien.congViecs.get(ref).Id;
        congViecNew.NoiDung = nhanVien.congViecs.get(ref).NoiDung;
        congViecNew.GhiChu = nhanVien.congViecs.get(ref).GhiChu;
        new ChonTrangThaiMayTask().execute();
    }

    class ChonMaTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<May> choiceList = new ArrayList<May>();
        String maExtra;

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                for (OEntity ct : consumer.getEntities("ChiTiets")
                        .filter("Id eq " + Integer.parseInt(params[0]))
                        .execute()) {
                    String ma = ct.getProperty("MaSo", String.class).getValue();
                    tempChiTiet = ma;
                    maExtra = "10";
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
                    maExtra = "abc";
                } else {
                    for (OEntity ct : consumer.getEntities("ChiTiets")
                            .filter("startswith(MaSo, '"+params[0]+"')")
                            .execute()) {

                        String ma = ct.getProperty("MaSo", String.class).getValue();

                        choiceList.add(new May(0, ma, 0));
                    }
                    maExtra = "10";
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
                    LayoutInflater inflater = (LayoutInflater) CongViecActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.dialog_chon_may, null);
                    final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
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
                    final MayAdapter adt = new MayAdapter(CongViecActivity.this, R.layout.item_may, choiceList);
                    ListView lv = (ListView) builder.findViewById(R.id.list_may);
                    lv.setAdapter(adt);
                    // Capture Text in EditText

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
                            May may = adt.getItem(which);
                            tempChiTiet = may.MaSo;
                            if (congViecNew == null){
                                new ChonNguyenCongTask().execute(tempChiTiet,maExtra);
                            } else {
                                if (congViecNew.PhanLoai == 2) {
                                    congViecNew.DoiTuongLD = tempChiTiet;
                                    new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                                }
                            }
                            Toast.makeText(CongViecActivity.this, "Chi tiết: "+tempChiTiet + "", Toast.LENGTH_SHORT).show();
                            builder.dismiss();
                        }
                    });
                } else {
                    if (choiceList.size() ==1){
                        Toast.makeText(CongViecActivity.this,"Scanned "+ choiceList.get(0).MaSo, Toast.LENGTH_SHORT).show();
                        if (congViecNew == null){
                            tempChiTiet = choiceList.get(0).MaSo;
                            new ChonNguyenCongTask().execute(tempChiTiet,maExtra);
                        } else {
                            if (congViecNew.PhanLoai == 2) {
                                congViecNew.DoiTuongLD = choiceList.get(0).MaSo;
                                new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                            }
                        }
                    } else {
                        Toast.makeText(CongViecActivity.this, "Không có mã", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                if ( result == 2) {
                    if (tempChiTiet == null) {
                        Toast.makeText(CongViecActivity.this, "Không có mã", Toast.LENGTH_SHORT).show();
                    } else {
                        if (congViecNew == null) {
                            new ChonNguyenCongTask().execute(tempChiTiet, maExtra);
                        } else {
                            if (congViecNew.PhanLoai ==2){
                                congViecNew.DoiTuongLD = tempChiTiet;
                                new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                            }
                        }
                        Toast.makeText(CongViecActivity.this, "Scanned " + tempChiTiet, Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.dismiss();
        }
    }

    private class ChonNguyenCongTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<NguyenCong> choiceList = new ArrayList<NguyenCong>();
        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }



        @Override
        protected Integer doInBackground(String... params) {
            try {
                String ma = params[0];

                if (LaChiTietHL(params[1]))
                {// chi tiết hàng loạt
                    for (OEntity ct : consumer.getEntities("ChiTietHLs")
                            .filter("TenNgan eq '" + ma + "'")
                            .expand("NguyenCongHLs").execute())
                    {
                        ORelatedEntitiesLink l = ct.getLink("NguyenCongHLs", ORelatedEntitiesLinkInline.class);
                        for (OEntity nc : l.getRelatedEntities()) {
                            NguyenCong newNc = new NguyenCong(  );
                            newNc.Id =nc.getProperty("Id", Integer.class).getValue();
                            newNc.NoiDung =nc.getProperty("NoiDung", String.class).getValue();
                            choiceList.add(newNc);
                        }
                    }
                } else { // chi tiet le
                    for (OEntity ct : consumer.getEntities("ChiTiets")
                            .filter("MaSo eq '" + ma + "'")
                            .expand("NguyenCongs").execute())
                    {
                        ORelatedEntitiesLink l = ct.getLink("NguyenCongs", ORelatedEntitiesLinkInline.class);
                        for (OEntity nc : l.getRelatedEntities()) {
                            NguyenCong newNc = new NguyenCong();
                            newNc.Id = nc.getProperty("Id", Integer.class).getValue();
                            OEntity toSX = consumer.getEntity("ToSXes",nc.getProperty("NguyenCong_ToSX", Integer.class)).execute();
                            newNc.NoiDung =
                                    toSX.getProperty("Ten", String.class).getValue()
                                            + "-" +
                                            nc.getProperty("NoiDung", String.class).getValue();
                            try {
                                newNc.NguyenCong = nc.getProperty("NhanVienKH", Integer.class).getValue() != nhanVien.Id ? true : false;
                            } catch (Exception e) {
                                newNc.NguyenCong = false;
                            }
                            try {
                                newNc.To = nc.getProperty("NhanVien_ToSX", Integer.class).getValue() != nhanVien.ToSX_Id ? true : false;
                            } catch (Exception e) {
                                newNc.To = false;
                            }
                            newNc.CoPhoi = nc.getProperty("NgayCoPhoi", LocalDateTime.class).getValue() != null ? true : false;
                            newNc.HoanThanh = nc.getProperty("NgayHTTT", LocalDateTime.class).getValue() != null ? true : false;
                            choiceList.add(newNc);
                        }
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
                if (!tempChiTiet.equals("0000")) {
                    if (choiceList.isEmpty()){
                        NguyenCong n = new NguyenCong();
                        n.Id = 0;
                        n.NoiDung = "Không có - chọn để tiếp tục";
                        n.CoPhoi = true;
                        choiceList.add(n);
                    }

                    LayoutInflater inflater = (LayoutInflater) CongViecActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.dialog_chon_nguyen_cong, null);
                    final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
                            .setTitle("Chọn nguyên công")
                            .setIcon(R.drawable.logo)
                            .setView(v)
                            .setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();

                    builder.show();

                    final CheckBox checkBoxAll = (CheckBox) builder.findViewById(R.id.allNcCheckBox);
                    final CheckBox checkBoxCaNhan = (CheckBox) builder.findViewById(R.id.caNhanNcCheckBox);
                    final CheckBox checkBoxTo = (CheckBox) builder.findViewById(R.id.toNcCheckBox);
                    final NguyenCongAdapter adt = new NguyenCongAdapter(CongViecActivity.this,
                            R.layout.item_nguyen_cong, choiceList);

                    checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked == false)
                                adt.getFilter().filter("1");
                            else{ // tat ca nguyen cong
                                checkBoxCaNhan.setChecked(false);
                                checkBoxTo.setChecked(false);
                                adt.getFilter().filter("");
//                                adt.notifyDataSetChanged();
                            }
                        }
                    });
                    checkBoxTo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b == false){
                                adt.getFilter().filter("1");
                                //nothing
                            } else {
                                checkBoxAll.setChecked(false);
                                checkBoxCaNhan.setChecked(false);
                                adt.getFilter().filter("2");
                            }
                        }
                    });
                    checkBoxCaNhan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b == false){
                                adt.getFilter().filter("1");
                                //Nothing
                            } else {
                                checkBoxAll.setChecked(false);
                                checkBoxTo.setChecked(false);
                                adt.getFilter().filter("3");
                            }
                        }
                    });

                    ListView lv = (ListView) builder.findViewById(R.id.list_nc);
                    lv.setAdapter(adt);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
                            tempNguyenCong = adt.getItem(which);
                            Toast.makeText(CongViecActivity.this, "Nguyên công "+ tempNguyenCong.NoiDung, Toast.LENGTH_SHORT).show();
                            if (tempNguyenCong.Id == 0) {
                                tempNguyenCong = null;
                                new ChonMayTask().execute();
                            } else
                                new ChonBuocTask().execute(tempNguyenCong.Id);
                            builder.dismiss();
                        }
                    });
                    checkBoxAll.setChecked(false);
                }
                else  {
                    tempNguyenCong = null;
                    tempBuoc = null;
                    new ChonMayTask().execute();
                }
            } else {
                Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }
            progressBar.dismiss();
        }
    }

    class UpdateSoLuongTask extends AsyncTask<Integer, Long, Integer> { // cập nhật số lượng đạt, không đạt, đang làm lở dở
        MyProgressDialog progressBar;
        CongViec2 tempCongViec2;
        CongViec tempCongViec;
        int good,doDang,nogood;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            try {
                good    = params[0];
                doDang  = params[1];
                nogood  = params[2];

                tempCongViec2           = new CongViec2();
                tempCongViec2.Good      = good;
                tempCongViec2.NoGood    = nogood;
                tempCongViec2.Gio       = new LocalDateTime();

                tempCongViec = nhanVien.congViecs.get(tempPos);
                if (good !=0 || nogood != 0) {
                    OEntity newCongViec2 = consumer.createEntity("CongViec2Set")
                            .properties(OProperties.int32("CongViec2_CongViec", tempCongViec.Id))
                            .properties(OProperties.datetime("Gio", tempCongViec2.Gio))
                            .properties(OProperties.int32("SoLuongG", tempCongViec2.Good))
                            .properties(OProperties.int32("SoLuongNG", tempCongViec2.NoGood))
                            .execute();
                    tempCongViec2.Id = newCongViec2.getProperty("Id", Integer.class).getValue();
                }
                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.int32("SLDoDang", doDang))
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
                    if (good !=0 || nogood != 0) {
                        tempCongViec.CongViec2Set.add(tempCongViec2);
                        tempCongViec.TongGood += tempCongViec2.Good;
                        tempCongViec.TongNoGood += tempCongViec2.NoGood;
                    }
                    tempCongViec.TongDoDang = doDang;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }

            progressBar.dismiss();
        }
    }

    class UpdateGioTask extends AsyncTask<String, Long, Integer> { //cập nhật thời gian thôi à, up lên database
        MyProgressDialog progressBar;
        CongViec tempCongViec;

        LocalDateTime gioBD = null;
        LocalDateTime gioKTSetup = null;
        LocalDateTime gioBDChay = null;
        LocalDateTime gioTD1 = null;
        LocalDateTime gioTT1 = null;
        LocalDateTime gioTD2 = null;
        LocalDateTime gioTT2 = null;
        LocalDateTime gioKT = null;
        int gioNguyenCong = 0;
        String ghichu;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                try {
                    String s = tempCongViec.GioBD.toString("dd/MM/yyyy") + " " + params[0];
                    Date date = timeFormat.parse(s);
                    gioBD = LocalDateTime.fromDateFields(date);
//                    if (gioBD.compareTo(tempCongViec.GioBD) > 0)
//                        gioBD = gioBD.minusHours(24);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioKTSetup.toString("dd/MM/yyyy") + " " + params[1];
                    Date date = timeFormat.parse(s);
                    gioKTSetup = LocalDateTime.fromDateFields(date);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioBDChay.toString("dd/MM/yyyy") + " " + params[2];
                    Date date = timeFormat.parse(s);
                    gioBDChay = LocalDateTime.fromDateFields(date);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioTamDung1.toString("dd/MM/yyyy") + " " + params[3];
                    Date date = timeFormat.parse(s);
                    gioTD1 = LocalDateTime.fromDateFields(date);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioTiepTuc1.toString("dd/MM/yyyy") + " " + params[4];
                    Date date = timeFormat.parse(s);
                    gioTT1 = LocalDateTime.fromDateFields(date);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioTamDung2.toString("dd/MM/yyyy") + " " + params[5];
                    Date date = timeFormat.parse(s);
                    gioTD2 = LocalDateTime.fromDateFields(date);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioTiepTuc2.toString("dd/MM/yyyy") + " " + params[6];
                    Date date = timeFormat.parse(s);
                    gioTT2 = LocalDateTime.fromDateFields(date);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    String s = tempCongViec.GioKT.toString("dd/MM/yyyy") + " " + params[7];
                    Date date = timeFormat.parse(s);
                    gioKT = LocalDateTime.fromDateFields(date);
//                    if (gioKT.compareTo(tempCongViec.GioKT) > 0)
//                        gioKT = gioKT.minusHours(24);
                } catch (ParseException e) {
                } catch (NullPointerException e){ }

                try {
                    gioNguyenCong = Integer.parseInt( params[8]);
                } catch (Exception ex) { }

                if (gioNguyenCong == 0){
                    try {
                        gioNguyenCong = ( tempCongViec.PhanLoai ==1?gioNguyenCong: Minutes.minutesBetween((gioBD != null? gioBD:tempCongViec.GioBD),(gioKT != null? gioKT:tempCongViec.GioKT)).getMinutes());
                    } catch (Exception ex1) { }
                }

                ghichu = params[9];

                if(gioBD == null){
                    gioBD = tempCongViec.GioBD_BS;
                }
                if (gioKTSetup == null){
                    gioKTSetup = tempCongViec.GioKTSetup;
                }
                if (gioBDChay == null){
                    gioBDChay = tempCongViec.GioBDChay ;
                }
                if (gioTD1 == null){
                    gioTD1 = tempCongViec.GioTamDung1 ;
                }
                if (gioTT1 == null){
                    gioTT1 = tempCongViec.GioTiepTuc1;
                }
                if (gioTD2 == null){
                    gioTD2 = tempCongViec.GioTamDung2;
                }
                if (gioTT2 == null){
                    gioTT2 = tempCongViec.GioTiepTuc2;
                }
                if (gioKT == null){
                    gioKT = tempCongViec.GioKT_BS;
                }
                try {
                    if (Minutes.minutesBetween(gioKTSetup,gioBDChay).getMinutes()<0){
                        gioBDChay = gioKTSetup;
                    }
                } catch (Exception e){}
                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();

                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioBD_BS", gioBD))
                        .properties(OProperties.datetime("GioSetupXong", gioKTSetup))
                        .properties(OProperties.datetime("GioBDChayMay", gioBDChay))
                        .properties(OProperties.datetime("GioTamDungLan1", gioTD1))
                        .properties(OProperties.datetime("GioBatDauLai1", gioTT1))
                        .properties(OProperties.datetime("GioTamDungLan2", gioTD2))
                        .properties(OProperties.datetime("GioBatDauLai2", gioTT2))
                        .properties(OProperties.datetime("GioKT_BS", gioKT))
                        .properties(OProperties.int32("ThoiGianNhanCong",gioNguyenCong))
                        .properties(OProperties.string("GhiChu", ghichu))
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
                    if (tempCongViec.PhanLoai == 1) {
                        tempCongViec.GioBD_BS = gioBD;
                        tempCongViec.GioKTSetup = gioKTSetup;
                        tempCongViec.GioBDChay = gioBDChay;
                        tempCongViec.GioTamDung1 = gioTD1;
                        tempCongViec.GioTiepTuc1 = gioTT1;
                        tempCongViec.GioTamDung2 = gioTD2;
                        tempCongViec.GioTiepTuc2 = gioTT2;
                        tempCongViec.GioKT_BS = gioKT;
                        tempCongViec.GioNguyenCong = gioNguyenCong;
                        tempCongViec.GhiChu = ghichu;
                    } else {
                        tempCongViec.GioBD_BS = gioBD;
                        tempCongViec.GioKT_BS = gioKT;
                        tempCongViec.GioNguyenCong = gioNguyenCong;
                        tempCongViec.GhiChu = ghichu;
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class ThemCongViecTask extends AsyncTask<CongViec, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }
        @Override
        protected Integer doInBackground(CongViec... params) {
            try {
                tempCongViec = params[0];
                tempCongViec.NhanVien = CongViecActivity.this.nhanVien;
                tempCongViec.GioBD = new LocalDateTime();

                if (tempCongViec.PhanLoai == 1) {// Thêm công việc gia công
                    String temp = "";
                    if (LaChiTietHL(tempCongViec.ChiTiet)){
                        temp = "CongViec_NguyenCongHL1";
                    }
                    else{
                        temp = "CongViec_NguyenCong";
                    }
                    OEntity newCongViec = consumer.createEntity("CongViecs")
                            .properties(OProperties.int32("CongViec_NhanVien", tempCongViec.NhanVien.Id))
                            .properties(OProperties.string("MaChiTiet", tempCongViec.ChiTiet))
                            .properties(OProperties.int32("CongViec_May1", tempCongViec.May != null ? tempCongViec.May.Id : null))
                            .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                            .properties(OProperties.int32(temp, tempCongViec.NguyenCong !=null? tempCongViec.NguyenCong.Id : null))
                            .properties(OProperties.string("Buocs", tempCongViec.Buocs))
                            .execute();
                    tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                }
                else if (tempCongViec.PhanLoai == 2) { // Thêm công việc khác
                    OEntity newCongViec = consumer.createEntity("CongViecs")
                            .properties(OProperties.int32("CongViec_NhanVien", tempCongViec.NhanVien.Id))
                            .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                            .properties(OProperties.string("GhiChu", tempCongViec.GhiChu))
                            .properties(OProperties.string("DoiTuongLamViec", tempCongViec.DoiTuongLD))
                            .properties(OProperties.int32("CongViec_CongViecPhu", tempCongViec.CongViecPhu.Id))
                            .execute();
                    tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                } else if (tempCongViec.PhanLoai == 3) { // Thêm báo trạng thái máy
                    OEntity newCongViec = consumer.createEntity("CongViecs")
                            .properties(OProperties.int32("CongViec_NhanVien", tempCongViec.NhanVien.Id))
                            .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                            .properties(OProperties.string("MaChiTiet", tempCongViec.ChiTiet))
                            .properties(OProperties.string("GhiChu", tempCongViec.GhiChu))
                            .properties(OProperties.string("NoiDung", tempCongViec.NoiDung))
                            .properties(OProperties.string("DoiTuongLamViec", tempCongViec.DoiTuongLD))
                            .execute();
                    tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                }
                return 1;
            } catch (Exception ex) {
                return -1;
            }
        }
        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    nhanVien.congViecs.add(tempCongViec);
                    scrollEnd();
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class ChonTrangThaiMayTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<TrangThaiMay> choiceList = new ArrayList<TrangThaiMay>();

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                for (OEntity ttm : consumer.getEntities("BaoTrangThaiMays").execute()) {
                    choiceList.add(new TrangThaiMay(ttm.getProperty("Id", Integer.class).getValue(),ttm.getProperty("NoiDung",String.class).getValue()));
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                LayoutInflater inflater = (LayoutInflater)CongViecActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
                        .setTitle("Chọn trạng thái")
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
                final TrangThaiMayAdapter adt = new TrangThaiMayAdapter(CongViecActivity.this, R.layout.item_buoc, choiceList);
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
                        congViecNew.NoiDung = adt.getItem(i).NoiDung;
                        if (congViecNew.Id == 0) {
                            OnBtnTinhTrangMay();
                        } else {
                            new UpdateFullCongViecTask().execute(congViecNew);
                        }
                        builder.dismiss();
                    }
                });

            } else {
                Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
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
            progressBar.show(CongViecActivity.this);
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
                LayoutInflater inflater = (LayoutInflater)CongViecActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
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
                final CongViecPhuAdapter adt = new CongViecPhuAdapter(CongViecActivity.this, R.layout.item_cong_viec_phu, choiceList);
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
                        tempCvPhu = adt.getItem(i);

                        CongViec newCv = new CongViec();
                        newCv.CongViecPhu = tempCvPhu;
                        newCv.PhanLoai = 2;
                        congViecNew = newCv;
                        builder.dismiss();
                        themThongTinCongViecKhac();
                    }
                });

            } else {
                Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }
            progressBar.dismiss();
        }
    }

    class BatDauChayTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);

        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioBDChayMay", updateGio))
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
                    tempCongViec.GioBDChay = updateGio;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class KetThucSetupTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);

        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioSetupXong", updateGio))
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
                    tempCongViec.GioKTSetup = updateGio;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class TamDung1Task extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioTamDungLan1", updateGio))
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
                    tempCongViec.GioTamDung1 = updateGio;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class TiepTuc1Task extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);

        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioBatDauLai1", updateGio))
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
                    tempCongViec.GioTiepTuc1 = updateGio;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class TamDung2Task extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);

        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioTamDungLan2", updateGio))
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
                    tempCongViec.GioTamDung2 = updateGio;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class TiepTuc2Task extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);

        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.datetime("GioBatDauLai2", updateGio))
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
                    tempCongViec.GioTiepTuc2 = updateGio;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
            progressBar.dismiss();
        }
    }

    class KetThucTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        LocalDateTime updateGio;
        LocalDateTime updateGioKTSetup;
        LocalDateTime updateBDChay;
        int updateTimeNC;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);

        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGio = new LocalDateTime();

                updateGioKTSetup = (tempCongViec.GioKTSetup == null && tempCongViec.PhanLoai == 1) ?  tempCongViec.GioBD : tempCongViec.GioKTSetup;
                updateBDChay = (tempCongViec.GioBDChay == null && tempCongViec.PhanLoai == 1) ? updateGioKTSetup : tempCongViec.GioBDChay;
                updateTimeNC =( tempCongViec.PhanLoai ==1?0: Minutes.minutesBetween((tempCongViec.GioBD_BS == null? tempCongViec.GioBD:tempCongViec.GioBD_BS),updateGio).getMinutes());

                OEntity CongViecs = consumer.getEntity("CongViecs",tempCongViec.Id).execute();
                if (tempCongViec.PhanLoai == 1){
                    consumer.updateEntity(CongViecs)
                            .properties(OProperties.datetime("GioKT", updateGio))
                            .properties(OProperties.datetime("GioSetupXong", updateGioKTSetup))
                            .properties(OProperties.datetime("GioBDChayMay", updateBDChay))
                            .properties(OProperties.int32("ThoiGianNhanCong",updateTimeNC))
                            .execute();
                } else {
                    consumer.updateEntity(CongViecs)
                            .properties(OProperties.datetime("GioKT", updateGio))
                            .properties(OProperties.int32("ThoiGianNhanCong",updateTimeNC))
                            .execute();
                }

                return 1;
            } catch (Exception ex) {
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    tempCongViec.GioKT = updateGio;
                    if (tempCongViec.PhanLoai == 1){
                        tempCongViec.GioKTSetup = updateGioKTSetup;
                        tempCongViec.GioBDChay = updateBDChay;
                    }
                    tempCongViec.GioNguyenCong = updateTimeNC;
                    adapter.notifyDataSetChanged();
                    if (tempCongViec.PhanLoai == 1){
                        OnBtnThemSLClick(tempPos);
                    }
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class GhiChuTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        String updateGhiChu;
        int timeNC = 0;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                updateGhiChu = params[0];
                try {
                    timeNC = Integer.parseInt(params[1]);
                } catch (Exception ex1) { }

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
                    tempCongViec.GhiChu = updateGhiChu;
                    tempCongViec.GioNguyenCong = timeNC;
                    adapter.notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class UpdateHinhTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        HinhDinhKem tempHinh;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                tempCongViec = nhanVien.congViecs.get(tempPos);
                int id = tempCongViec.Id;
                tempHinh = new HinhDinhKem();
                tempHinh.Data = byteArray;

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
                    nhanVien.congViecs.get(tempPos).HinhDinhKems.add(tempHinh);
                    Toast.makeText(CongViecActivity.this, "Đã Update hình ảnh thành công.", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
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
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                if (params[0] !=null){
                    for (OEntity lsx : consumer.getEntities("LenhSanXuats")
                            .filter("startswith(MaSo, '"+params[0]+"')")
                            .execute()){
                        String maSo = lsx.getProperty("MaSo", String.class).getValue();
                        boolean ngayHTGC = lsx.getProperty("NgayHTGC", LocalDateTime.class).getValue() != null?true:false;
                        if (ngayHTGC == false){
                            choiceList.add(new May(1,maSo,1));
                        }
                    }
                } else {
                    for (OEntity lsx : consumer.getEntities("LenhSanXuats")
                            .execute()){
                        String maSo = lsx.getProperty("MaSo", String.class).getValue();
                        boolean ngayHTGC = lsx.getProperty("NgayHTGC", LocalDateTime.class).getValue() != null?true:false;
                        if (ngayHTGC == false){
                            choiceList.add(new May(1,maSo,1));
                        }
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
                        LayoutInflater inflater = (LayoutInflater)CongViecActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.dialog_chon_may, null);
                        final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
                                .setTitle("Chọn Lệnh Sản Xuất")
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
                        final MayAdapter adt = new MayAdapter(CongViecActivity.this, R.layout.item_may, choiceList);

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
                                Toast.makeText(CongViecActivity.this, adt.getItem(i).MaSo + "", Toast.LENGTH_SHORT).show();
                                congViecNew.DoiTuongLD = adt.getItem(i).MaSo;
                                new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                                builder.dismiss();
                            }
                        });
                    } else {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CongViecActivity.this);
                        builder.setMessage("Không có Lệnh Sản Xuất, bạn có muốn thêm công việc khác?")
                                .setPositiveButton("Có", dialogClickListener)
                                .setNegativeButton("Không", dialogClickListener).show();

                    }
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class ChonBuocTask extends AsyncTask<Integer, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<String> choiceList = new ArrayList<String>();


        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                Integer ncId = params[0];

                OEntity nc = consumer.getEntity("NguyenCongHLs", ncId).expand("BuocHLs").execute() ;
                ORelatedEntitiesLink l = nc.getLink("BuocHLs", ORelatedEntitiesLinkInline.class);
                for (OEntity buoc : l.getRelatedEntities()) {
                    //Buoc newBuoc = new Buoc(  );
                    //newBuoc.Id =buoc.getProperty("Id", Integer.class).getValue();
                    String ten =buoc.getProperty("Ten", String.class).getValue();
                    choiceList.add(ten);
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            tempBuoc = null;
            if (!choiceList.isEmpty()) {
                if (result == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CongViecActivity.this);
                    builder.setTitle("Chọn bước");

                    final boolean[] checkedBuocs = new boolean[choiceList.size()];

                    String[] arr = new String[choiceList.size()];
                    arr = choiceList.toArray(arr);

                    builder.setMultiChoiceItems(arr, checkedBuocs, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedBuocs[which] = isChecked;
                        }
                    });
                    // Specify the dialog is not cancelable
                    builder.setCancelable(false);
                    // Set the positive/yes button click listener
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            tempBuoc = "";
                            for (int i = 0; i<checkedBuocs.length; i++){
                                boolean checked = checkedBuocs[i];
                                if (checked) {
                                    tempBuoc += choiceList.get(i) + " ";
                                }
                            }

                            //state = THEM_CONG_VIEC_MAY;
                            new ChonMayTask().execute( );
                        }
                    });

                    // Set the negative/no button click listener
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when click the negative button
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                }
            } else {
                //state = THEM_CONG_VIEC_MAY;
                new ChonMayTask().execute( );
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
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {

                for (OEntity may : consumer.getEntities("Mays")
                        //.filter("May_ToSX eq " + CongViecActivity.this.nhanVien.ToSX_Id)
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
                if (congViecNew == null) {
                    May emtpy = new May(0, "Không có máy", CongViecActivity.this.nhanVien.ToSX_Id);
                    choiceList.add(0, emtpy);
                }
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                LayoutInflater inflater = (LayoutInflater)CongViecActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(CongViecActivity.this)
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
                final MayAdapter adt = new MayAdapter(CongViecActivity.this, R.layout.item_may, choiceList);

                // Capture Text in EditText
                editsearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                        adt.getFilter().filter(
                                (checkBox.isChecked()? " " : nhanVien.ToSX_Id ) + ": " + text);
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

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                        if (isChecked == false)
                            adt.getFilter().filter(nhanVien.ToSX_Id + ": " + text);
                        else
                            adt.getFilter().filter(" : " + text);
                        adt.notifyDataSetChanged();
                    }
                });

                ListView lv = (ListView) builder.findViewById(R.id.list_may);
                lv.setAdapter(adt);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,int which, long arg3)
                    {
                        tempMay = adt.getItem(which);
                        Toast.makeText(CongViecActivity.this, "Máy "+tempMay.MaSo, Toast.LENGTH_SHORT).show();
                        if (congViecNew!=null) {
                            if (congViecNew.PhanLoai == 2){
                                congViecNew.DoiTuongLD = tempMay.MaSo;
                                new CongViecActivity.ThemCongViecTask().execute(congViecNew);
                                builder.dismiss();
                            } else if (congViecNew.PhanLoai == 3){
                                congViecNew.DoiTuongLD = tempMay.MaSo;
                                if (congViecNew.Id == 0) {
                                    OnBtnTrangThai(-1);
                                    builder.dismiss();
                                } else {
                                    new UpdateFullCongViecTask().execute(congViecNew);
                                    builder.dismiss();
                                }
                            }
                        } else {
                            if (tempMay.Id == 0)
                                tempMay = null;
                            CongViec newCongViec = new CongViec();
                            newCongViec.ChiTiet = tempChiTiet;
                            newCongViec.NguyenCong = tempNguyenCong;
                            newCongViec.Buocs = tempBuoc;
                            newCongViec.May = tempMay;
                            newCongViec.PhanLoai = 1;
                            new CongViecActivity.ThemCongViecTask().execute(newCongViec);
                            builder.dismiss();
                        }
                    }
                });
                if ( congViecNew != null){
                    if (congViecNew.PhanLoai == 2 || congViecNew.PhanLoai == 3) {
                        checkBox.setChecked(true);
                        checkBox.setVisibility(View.GONE);
                    }
                } else {
                    checkBox.setChecked(false);
                }

            } else {
                Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }

            progressBar.dismiss();
        }
    }

    class DoiMatKhauTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        String matKhauMoi;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                matKhauMoi = params[0];

                OEntity CongViecs = consumer.getEntity("NhanViens",nhanVien.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.string("MatKhau", matKhauMoi))
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
                    nhanVien.MatKhau = matKhauMoi;
                    Toast.makeText(CongViecActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class UpdateFullCongViecTask extends AsyncTask<CongViec, Long, Integer> {
        MyProgressDialog progressBar;
        CongViec tempCongViec;
        boolean taomoi;
        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViecActivity.this);
        }
        @Override
        protected Integer doInBackground(CongViec... params) {
            try {
                tempCongViec = params[0];
                tempCongViec.NhanVien = CongViecActivity.this.nhanVien;
                tempCongViec.GioBD = new LocalDateTime();
                if (tempCongViec.Id == 0) {
                    taomoi = true;
                    if (tempCongViec.PhanLoai == 1) {// Thêm công việc gia công
                        String temp = "";
                        if (LaChiTietHL(tempCongViec.ChiTiet)) {
                            temp = "CongViec_NguyenCongHL1";
                        } else {
                            temp = "CongViec_NguyenCong";
                        }
                        OEntity newCongViec = consumer.createEntity("CongViecs")
                                .properties(OProperties.int32("CongViec_NhanVien", tempCongViec.NhanVien.Id))
                                .properties(OProperties.string("MaChiTiet", tempCongViec.ChiTiet))
                                .properties(OProperties.int32("CongViec_May1", tempCongViec.May != null ? tempCongViec.May.Id : null))
                                .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                                .properties(OProperties.int32(temp, tempCongViec.NguyenCong != null ? tempCongViec.NguyenCong.Id : null))
                                .properties(OProperties.string("Buocs", tempCongViec.Buocs))
                                .execute();
                        tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                    } else if (tempCongViec.PhanLoai == 2) { // Thêm công việc khác
                        OEntity newCongViec = consumer.createEntity("CongViecs")
                                .properties(OProperties.int32("CongViec_NhanVien", tempCongViec.NhanVien.Id))
                                .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                                .properties(OProperties.string("GhiChu", tempCongViec.GhiChu))
                                .properties(OProperties.string("DoiTuongLamViec", tempCongViec.DoiTuongLD))
                                .properties(OProperties.int32("CongViec_CongViecPhu", tempCongViec.CongViecPhu.Id))
                                .execute();
                        tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                    } else if (tempCongViec.PhanLoai == 3) { // Thêm báo trạng thái máy
                        OEntity newCongViec = consumer.createEntity("CongViecs")
                                .properties(OProperties.int32("CongViec_NhanVien", tempCongViec.NhanVien.Id))
                                .properties(OProperties.datetime("GioBD", tempCongViec.GioBD))
                                .properties(OProperties.string("MaChiTiet", tempCongViec.ChiTiet))
                                .properties(OProperties.string("GhiChu", tempCongViec.GhiChu))
                                .properties(OProperties.string("NoiDung", tempCongViec.NoiDung))
                                .properties(OProperties.string("DoiTuongLamViec", tempCongViec.DoiTuongLD))
                                .execute();
                        tempCongViec.Id = newCongViec.getProperty("Id", Integer.class).getValue();
                    }
                } else {
                    taomoi = false;
                    if (tempCongViec.PhanLoai == 1) {// Thêm công việc gia công
                        String temp = "";
                        if (LaChiTietHL(tempCongViec.ChiTiet)) {
                            temp = "CongViec_NguyenCongHL1";
                        } else {
                            temp = "CongViec_NguyenCong";
                        }
                    } else if (tempCongViec.PhanLoai == 2) { // Thêm công việc khác

                    } else if (tempCongViec.PhanLoai == 3) { // Thêm báo trạng thái máy
                        OEntity CongViecs = consumer.getEntity("CongViecs", tempCongViec.Id).execute();
                        consumer.updateEntity(CongViecs)
                                .properties(OProperties.string("GhiChu", tempCongViec.GhiChu))
                                .properties(OProperties.string("NoiDung", tempCongViec.NoiDung))
                                .properties(OProperties.string("DoiTuongLamViec", tempCongViec.DoiTuongLD))
                                .execute();
                    }
                }
                return 1;
            } catch (Exception ex) {
                return -1;
            }
        }
        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    if (taomoi){
                        nhanVien.congViecs.add(tempCongViec);
                        scrollEnd();
                    } else {
                        nhanVien.congViecs.get(tempPos).DoiTuongLD = tempCongViec.DoiTuongLD;
                        nhanVien.congViecs.get(tempPos).NoiDung = tempCongViec.NoiDung;
                        nhanVien.congViecs.get(tempPos).GhiChu = tempCongViec.GhiChu;
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case -1:
                    Toast.makeText(CongViecActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("NhanVien", nhanVien);
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
