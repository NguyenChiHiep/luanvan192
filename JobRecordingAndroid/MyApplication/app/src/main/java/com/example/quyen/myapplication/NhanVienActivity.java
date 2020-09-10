package com.example.quyen.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.core4j.Enumerable;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class NhanVienActivity extends AppCompatActivity {

    ListView lvNhanVien;
    ArrayList<NhanVien> arrNhanVien = new ArrayList<NhanVien>();
    NhanVienAdapter adapter;
    ODataConsumer consumer;
    NhanVien tempNhanVien;
    int position;
    String maNvSave = null;

    public  static final int NHAN_VIEN = 100;
    public static final String TAG = NhanVienActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_vien);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themNhanVien();
            }
        });

        setTitle("Danh sách nhân viên");

        lvNhanVien = (ListView) findViewById(R.id.lvNhanVien);
        adapter = new NhanVienAdapter(this, R.layout.item_nhan_vien, arrNhanVien);
        lvNhanVien.setAdapter(adapter);

        lvNhanVien.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {

                final int pos = position;
                LayoutInflater inflater = (LayoutInflater)NhanVienActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_hoi_mat_khau, null);
                final AlertDialog dialog = new AlertDialog.Builder(NhanVienActivity.this)
                        .setView(v)
                        .setIcon(R.drawable.logo)
                        .setTitle(" " +arrNhanVien.get(pos).HoTen+"\n  Nhập mật khẩu : ")
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.show();

                final EditText matKhauTxt = (EditText) dialog.findViewById(R.id.editMatKhau);

                matKhauTxt.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = matKhauTxt.getText().toString();
                        if (text.length() == 4){
                            String matKhau;
                            matKhau = matKhauTxt.getText().toString();
                            NhanVien nv = arrNhanVien.get(pos);
                            if (nv.MatKhau.equals(matKhau)) {
                                StartCongViecActivity(pos);
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(NhanVienActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                                matKhauTxt.selectAll();
                            }
                        }
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

                matKhauTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

                dialog.show();
            }
        });
        String serviceUrl= Setting.ServerUrl;
        OClientBehavior basicAuth = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(basicAuth).build();
    }

    public  void StartCongViecActivity(int position){
        NhanVienActivity.this.position = position;
        Intent intent = new Intent(NhanVienActivity.this, CongViecActivity.class);
        intent.putExtra("NhanVien", (Serializable) arrNhanVien.get(position));
        NhanVienActivity.this.startActivityForResult(intent, NHAN_VIEN);
    }

    public void themNhanVien() {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_dang_nhap, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Đăng nhập")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int x ) {

                        final EditText maNvTxt = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editMaNv);
                        final EditText matKhauTxt = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editMatKhau);

                        String maNv = maNvTxt.getText().toString();
                        String matKhau = matKhauTxt.getText().toString();

                        int pos = -1;
                        int i = 0;
                        for  (NhanVien nv : arrNhanVien){
                            if (nv.MaSo.substring(4).equals(maNv) && nv.MatKhau.equals(matKhau)){
                                pos = i;
                                break;
                            }
                            i++;
                        }
                        if (pos == -1) {
                            new MyAsyncTask().execute(maNv, matKhau);
                        }
                        else {
                            StartCongViecActivity(pos);
                        }

                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        maNvSave = null;
                        dialogInterface.dismiss();
                    }
                })
                .create();

        dialog.show();

        final EditText maNvTxt = (EditText) dialog.findViewById(R.id.editMaNv);
        maNvTxt.setText(maNvSave);
        maNvTxt.selectAll();
        maNvTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NHAN_VIEN){
            handleCongViecActivity(requestCode, resultCode, data);
        }
        else {
            handleScanResult(requestCode, resultCode, data);
        }
    }

    private void handleCongViecActivity(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            NhanVien nv = (NhanVien) data.getSerializableExtra("NhanVien");
            arrNhanVien.set(position, nv);
        }
    }

    private void handleScanResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String ma = result.getContents();
                Toast.makeText(this, "Scanned: ***", Toast.LENGTH_SHORT).show();

                boolean found = false;
                for  (NhanVien nv : arrNhanVien){
                    if (nv.Guid.equals(ma)){
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    new MyAsyncTask().execute(ma);
                }
                else {
                    Toast.makeText(this, "Nhân viên đã có trong danh sách", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void OnBtnThoatNvClick(int position) {
        final int pos = position;
        LayoutInflater inflater = (LayoutInflater)NhanVienActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_hoi_mat_khau, null);
        final AlertDialog dialog = new AlertDialog.Builder(NhanVienActivity.this)
                .setView(v)
                .setIcon(R.drawable.logo)
                .setTitle(" " +arrNhanVien.get(pos).HoTen+"\n  Nhập mật khẩu : ")
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String matKhau = ((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editMatKhau)).getText().toString();
                        NhanVien nv = arrNhanVien.get(pos);
                        if (nv.MatKhau.equals(matKhau)) {
                            arrNhanVien.remove(pos);
                            System.gc();
                            adapter.notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                        else {
                            Toast.makeText(NhanVienActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            ((EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editMatKhau)).selectAll();
                        }
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
        final EditText matKhauTxt = (EditText) dialog.findViewById(R.id.editMatKhau);
    }

    @Override
    public void onBackPressed() {
    }

    class MyAsyncTask extends AsyncTask<String, Long, Integer> {

        MyProgressDialog progressBar;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(NhanVienActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                try {
//                    Log.d(TAG, "doInBackground: "+consumer.callFunction("CURRENT_TIMESTAMP()").execute().toString());
                    OEntity CongViecs = consumer.getEntities("CongViecs").orderBy("Id desc").top(1).execute().first();
                    LocalDateTime dateTime = CongViecs.getProperty("GioBD", LocalDateTime.class).getValue();
                    LocalDateTime localDateTime = new LocalDateTime();
                    int bienTam = Days.daysBetween(localDateTime,dateTime).getDays();
                    if (bienTam>0) {
                        return 2;
                    }
                } catch (Exception e){
                    Log.d(TAG, "doInBackground: "+e);
                }
                String maNv = params[0];
                String matKhau = params[1];
                maNvSave = maNv;
                for (
                        OEntity nhanVien : consumer.getEntities("NhanViens")
                        .filter("substring(MaSo, 4) eq '" + maNv + "' and MatKhau eq '" + matKhau + "' and NghiViec eq false")
                        .execute())
                {
                    int to =0;
                    try{ to = nhanVien.getProperty("NhanVien_ToSX", Integer.class).getValue(); } catch (Exception e){ }
                    tempNhanVien = new NhanVien(
                            nhanVien.getProperty("Id", Integer.class).getValue(),
                            nhanVien.getProperty("MaSo", String.class).getValue(),
                            nhanVien.getProperty("HoTen", String.class).getValue(),
                            nhanVien.getProperty("Guid", String.class).getValue(),
                            nhanVien.getProperty("Color", String.class).getValue(),
                            nhanVien.getProperty("SlgMacDinh", Integer.class).getValue(),
                            nhanVien.getProperty("MatKhau", String.class).getValue(),
                            nhanVien.getProperty("DoiMatKhau", Boolean.class).getValue(),
                            to);
//                    tempNhanVien.ThongKe_Id = -1;
                    try{
                        OEntity calamviec = consumer.getEntity("CaLamViecs",nhanVien.getProperty("NhanVien_CaLamViec", Integer.class).getValue()).execute();
                        int id = calamviec.getProperty("Id", Integer.class).getValue();
                        String tenca = calamviec.getProperty("NoiDung", String.class).getValue();
                        int tgNghi = (int)  ((calamviec.getProperty("GioNghi",Double.class).getValue())/60);
                        String[] tokens = tenca.split(" ");
                        String maca = "";
                        for (String a: tokens){
                            maca +=a.charAt(0);
                        }
                        tempNhanVien.caLamViec = new CaLamViec(id,maca,tenca,tgNghi);
                    } catch (Exception e){}
                    try{
                        LocalDateTime now = LocalDateTime.fromDateFields(new Date());
                        now = now.minusHours(12);
                        String s =  now.toString();
                        OEntity ThongKe = consumer.getEntities("ThongKeCongViecTrongNgays").filter("Ngay gt datetime'"+ s + "' and NhanVien eq "+tempNhanVien.Id).top(1).execute().first();
                        tempNhanVien.ThongKe_Id = ThongKe.getProperty("Id", Integer.class).getValue();
                    } catch (Exception e){ }
                    return 1;
                }
                tempNhanVien = null;
                return 0;

            } catch (Exception ex){
                tempNhanVien = null;
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case -1:
                    Toast.makeText(NhanVienActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(NhanVienActivity.this, "Mã nhân viên hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    themNhanVien();
                    break;

                case 1:
                    maNvSave = null;
                    if (tempNhanVien.DoiMatKhau){
                        doiMatKhau();
                    }
                    else {
                        new LoadCongViecTask().execute();
                    }
                    break;
                case 2:
                    Toast.makeText(NhanVienActivity.this, "Vui lòng cài đặt lại thời gian trên máy", Toast.LENGTH_SHORT).show();
                    break;
            }

            progressBar.dismiss();
        }
    }

    private void doiMatKhau() {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_doi_mat_khau, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .setIcon(R.drawable.logo)
                .setTitle("Đổi mật khẩu")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final EditText matKhauTxt = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editMatKhau);
                        final EditText matKhau2Txt = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editMatKhau2);

                        String matKhau = matKhauTxt.getText().toString();
                        String matKhau2 = matKhau2Txt.getText().toString();
                        if (matKhau.length() == 4) {
                            if (matKhau.equals(tempNhanVien.MatKhau)) {
                                Toast.makeText(NhanVienActivity.this, "Mật khẩu mới phải khác mật khẩu cũ", Toast.LENGTH_SHORT).show();
                            } else if (matKhau.equals(matKhau2)) {
                                new DoiMatKhauTask().execute(matKhau);
                                dialogInterface.dismiss();
                            } else
                                Toast.makeText(NhanVienActivity.this, "Mật khẩu 2 ô phải giống nhau", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(NhanVienActivity.this, "Vui lòng nhập 4 số làm mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create( );

        dialog.show();
        dialog.findViewById(R.id.linearLayoutMatKhauCu).setVisibility(View.GONE);
        final EditText matKhauTxt = (EditText) dialog.findViewById(R.id.editMatKhau);

        matKhauTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    class DoiMatKhauTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        String matKhauMoi;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(NhanVienActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                matKhauMoi = params[0];

                OEntity CongViecs = consumer.getEntity("NhanViens", tempNhanVien.Id).execute();

                consumer.updateEntity(CongViecs)
                        .properties(OProperties.string("MatKhau", matKhauMoi))
                        .properties(OProperties.boolean_("DoiMatKhau", false))
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
                    tempNhanVien.MatKhau = matKhauMoi;
                    Toast.makeText(NhanVienActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    new LoadCongViecTask().execute();
                    break;
                case -1:
                    Toast.makeText(NhanVienActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }


    class LoadCongViecTask extends AsyncTask<String, Long, Integer> {

        MyProgressDialog progressBar;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(NhanVienActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                LocalDateTime now = LocalDateTime.fromDateFields(new Date());
                now = now.minusHours(12);
                String s =  now.toString();
                Enumerable<OEntity> congViecs = consumer.getEntities("NhanViens").nav(tempNhanVien.Id, "CongViecs")
                        .filter("GioBD gt datetime'"+ s + "'")
                        .execute();

                for (OEntity cv : congViecs) {

                    CongViec newCv = new CongViec();
                    newCv.Id = cv.getProperty("Id", Integer.class).getValue();
                    newCv.ChiTiet = cv.getProperty("MaChiTiet", String.class).getValue();
                    if (newCv.ChiTiet != null && !newCv.ChiTiet.isEmpty()) {
                        if (!newCv.ChiTiet.equals("Thông báo máy hỏng") && !newCv.ChiTiet.equals("Thông báo máy chờ")) {
                            newCv.PhanLoai = 1;
                            NguyenCong n = new NguyenCong();

                            if (CongViecActivity.LaChiTietHL(newCv.ChiTiet)) {
                                Integer temp = cv.getProperty("CongViec_NguyenCongHL1", Integer.class).getValue();
                                if (temp != null) {
                                    n.Id = temp;
                                    OEntity entity = consumer.getEntity("NguyenCongHLs", n.Id).execute();
                                    n.NoiDung = entity.getProperty("NoiDung", String.class).getValue();
                                } else {
                                    n = null;
                                }

                            } else if (!newCv.ChiTiet.equals("0000")) {
                                Integer temp = cv.getProperty("CongViec_NguyenCong", Integer.class).getValue();
                                if (temp != null) {
                                    OEntity ncEntity = consumer.getEntity("NguyenCongs", temp).execute();
                                    OEntity toSXEntity = consumer.getEntity("ToSXes", ncEntity.getProperty("NguyenCong_ToSX", Integer.class).getValue()).execute();

                                    n.Id = temp;
                                    n.NoiDung = toSXEntity.getProperty("Ten", String.class).getValue()
                                            + "-" + ncEntity.getProperty("NoiDung", String.class).getValue();
                                } else {
                                    n = null;
                                }

                            } else {
                                n = null;
                            }
                            newCv.NguyenCong = n;

                            Integer x = cv.getProperty("CongViec_May1", Integer.class).getValue();
                            if (x != null) {
                                May may = new May(0, "", 0);
                                may.Id = x;
                                OEntity entity = consumer.getEntity("Mays", may.Id).execute();
                                may.MaSo = entity.getProperty("MaSo", String.class).getValue();
                                newCv.May = may;
                            }
                            newCv.Buocs = cv.getProperty("Buocs", String.class).getValue();

                            Enumerable<OEntity> congViec2Set = consumer.getEntities("CongViecs").nav(newCv.Id, "CongViec2Set")
                                    .execute();
                            for (OEntity cv2 : congViec2Set) {
                                CongViec2 newCv2 = new CongViec2();
                                newCv2.Id = cv2.getProperty("Id", Integer.class).getValue();
                                newCv2.Good = cv2.getProperty("SoLuongG", Integer.class).getValue();
                                newCv2.NoGood = cv2.getProperty("SoLuongNG", Integer.class).getValue();
                                newCv2.Gio = cv2.getProperty("Gio", LocalDateTime.class).getValue();

                                newCv.CongViec2Set.add(newCv2);
                            }
                            newCv.CapNhatSlg();
                        } else {
                            newCv.PhanLoai = 3;
                            newCv.NoiDung = cv.getProperty("NoiDung", String.class).getValue();
                        }

                    }else {
                        newCv.PhanLoai = 2;
                        CongViecPhu p = new CongViecPhu();
                        p.Id = cv.getProperty("CongViec_CongViecPhu", Integer.class).getValue();
                        OEntity entity = consumer.getEntity("CongViecPhus", p.Id).execute();
                        p.MaSo = entity.getProperty("MaSo", String.class).getValue();
                        p.Ten = entity.getProperty("Ten", String.class).getValue();
                        newCv.CongViecPhu = p;
                    }

                    Enumerable<OEntity> hinhDK = consumer.getEntities("CongViecs").nav(newCv.Id, "HinhDinhKems")
                            .execute();
                    for (OEntity cv2 : hinhDK) {
                        HinhDinhKem hinhDinhKem = new HinhDinhKem();
                        hinhDinhKem.Id = cv2.getProperty("Id", Integer.class).getValue();
                        hinhDinhKem.Data = cv2.getProperty("Hinh", byte[].class).getValue();
                        newCv.HinhDinhKems.add(hinhDinhKem);
                    }

                    newCv.GioBD = cv.getProperty("GioBD", LocalDateTime.class).getValue();
                    newCv.GioKTSetup = cv.getProperty("GioSetupXong", LocalDateTime.class).getValue();
                    newCv.GioBDChay = cv.getProperty("GioBDChayMay", LocalDateTime.class).getValue();
                    newCv.GioTamDung1 = cv.getProperty("GioTamDungLan1", LocalDateTime.class).getValue();
                    newCv.GioTiepTuc1 = cv.getProperty("GioBatDauLai1", LocalDateTime.class).getValue();
                    newCv.GioTamDung2 = cv.getProperty("GioTamDungLan2", LocalDateTime.class).getValue();
                    newCv.GioTiepTuc2 = cv.getProperty("GioBatDauLai2", LocalDateTime.class).getValue();
                    newCv.GioKT = cv.getProperty("GioKT", LocalDateTime.class).getValue();
                    newCv.GioBD_BS = cv.getProperty("GioBD_BS", LocalDateTime.class).getValue();
                    newCv.GioKT_BS = cv.getProperty("GioKT_BS", LocalDateTime.class).getValue();
                    newCv.GhiChu = cv.getProperty("GhiChu", String.class).getValue();
                    newCv.DoiTuongLD = cv.getProperty("DoiTuongLamViec", String.class).getValue();
                    newCv.GioNguyenCong = cv.getProperty("ThoiGianNhanCong", Integer.class).getValue()==null?0:cv.getProperty("ThoiGianNhanCong", Integer.class).getValue();
                    newCv.TongDoDang = cv.getProperty("SLDoDang", Integer.class).getValue()==null?0:cv.getProperty("SLDoDang", Integer.class).getValue();
                    newCv.NhanVien = tempNhanVien;
                    tempNhanVien.congViecs.add(newCv);
                }
                return 1;

            } catch (Exception ex){
                tempNhanVien = null;
                return -1;
            }
        }

        @Override
        protected  void onPostExecute(Integer result) {
            switch (result) {
                case -1:
                    Toast.makeText(NhanVienActivity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    arrNhanVien.add(tempNhanVien);
                    adapter.notifyDataSetChanged();
                    StartCongViecActivity(arrNhanVien.size() - 1);
                    break;
            }
            progressBar.dismiss();
        }
    }
}
