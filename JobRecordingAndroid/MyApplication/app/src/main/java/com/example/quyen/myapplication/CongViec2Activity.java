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
import android.view.Menu;
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
import java.util.ArrayList;
import java.util.Locale;

import static com.example.quyen.myapplication.CongViecActivity.LaChiTietHL;

public class CongViec2Activity extends AppCompatActivity {

    ListView lvCongViec2;
    CongViec2Adapter adapter;
    ListView lvHinh;
    HinhDinhKemAdapter adapterHinh;
    CongViec congViec;
    ODataConsumer consumer;
    String tempChiTiet;
    NguyenCong tempNguyenCong;
    String tempBuoc;
    May tempMay;
    byte[] arrayHinh;

    int state;

    static final int SUA_CONG_VIEC_CHI_TIET = 20;
    static final int SUA_CONG_VIEC_CHI_TIET_NGUYEN_CONG = 21;
    static final int SUA_CONG_VIEC_MAY = 30;
    static final int SUA_CONG_VIEC_NGUYEN_CONG = 40;
    static final int REQUEST_IMAGE_CAPTURE = 123;
    private BroadcastReceiver mReceiver;

    public static final String TAG = CongViec2Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cong_viec2);

        Intent intent = getIntent();
        congViec = (CongViec) intent.getSerializableExtra("CongViec");

        lvCongViec2 = (ListView) findViewById(R.id.lvCongViec2);
        adapter     = new CongViec2Adapter(this, R.layout.item_cong_viec2, congViec.CongViec2Set);
        lvCongViec2.setAdapter(adapter);

        lvHinh      = (ListView) findViewById(R.id.lvHinh);
        adapterHinh = new HinhDinhKemAdapter(this,R.layout.item_hinh_dinh_kem,congViec.HinhDinhKems);
        lvHinh.setAdapter(adapterHinh);
        UpdateAdapter();
        HienThi();
        lvHinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hienthihinh(adapterHinh.getItem(i));
            }
        });

        updateCongViec();
        String serviceUrl= Setting.ServerUrl;
        OClientBehavior basicAuth = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(basicAuth).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btnMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // Tạo menu chức năng: Sửa chi tiết/nguyên công, Sửa máy, Xóa công việc.

                PopupMenu popup = new PopupMenu(CongViec2Activity.this, v);
                // Inflate the menu from xml
                popup.getMenuInflater().inflate(R.menu.popup_cong_viec_detail, popup.getMenu());
                LocalDateTime gioBD = CongViec2Activity.this.congViec.GioBD;
                LocalDateTime now = new LocalDateTime();
                int x = Minutes.minutesBetween(gioBD, now).getMinutes();
                if ( x > 15) {
                    Menu menu = popup.getMenu();
                    popup.getMenu().removeItem(R.id.item_sua_chi_tiet);
                }
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_sua_chi_tiet:
                                OnBtnSuaChiTietClick();
                                return true;
                            case R.id.item_sua_may:
                                OnBtnSuaMayClick();
                                return true;
                            case R.id.item_chup_hinh:
                                ActivityCompat.requestPermissions(CongViec2Activity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                                return  true;
                            case R.id.item_xoa:
                                OnBtnXoaClick();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

    }

    void UpdateAdapter(){
        adapter.notifyDataSetChanged();
        adapterHinh.notifyDataSetChanged();
        if (congViec.CongViec2Set.size()!= 0){
            lvCongViec2.setVisibility(View.VISIBLE);
        } else {
            lvCongViec2.setVisibility(View.GONE);
        }
//        if (congViec.HinhDinhKems.size()!= 0){
//            lvHinh.setVisibility(View.VISIBLE);
//        } else {
//            lvHinh.setVisibility(View.GONE);
//        }
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

    void HienThi(){
        LocalDateTime gioBD = congViec.GioBD_BS != null? congViec.GioBD_BS:congViec.GioBD;
        LocalDateTime gioKTSetup = congViec.GioKTSetup_BS != null? congViec.GioKTSetup_BS:congViec.GioKTSetup;
        LocalDateTime gioBDChay = congViec.GioBDChay_BS != null? congViec.GioBDChay_BS:congViec.GioBDChay;
        LocalDateTime gioTD1 = congViec.GioTamDung1_BS != null? congViec.GioTamDung1_BS:congViec.GioTamDung1;
        LocalDateTime gioTT1 = congViec.GioTiepTuc1_BS != null? congViec.GioTiepTuc1_BS:congViec.GioTiepTuc1;
        LocalDateTime gioTD2 = congViec.GioTamDung2_BS != null? congViec.GioTamDung2_BS:congViec.GioTamDung2;
        LocalDateTime gioTT2 = congViec.GioTiepTuc2_BS != null? congViec.GioTiepTuc2_BS:congViec.GioTiepTuc2;
        LocalDateTime gioKT = congViec.GioKT_BS != null? congViec.GioKT_BS:congViec.GioKT;
        int giosetmay = 0;
        int giochaymay = 0;
        int gioTamDung1;
        int gioTamDung2;
        try {
            giosetmay = Minutes.minutesBetween(gioBD,gioKTSetup).getMinutes();
            giochaymay = Minutes.minutesBetween(gioBDChay,gioKT).getMinutes();
        } catch (Exception ex){
            try {
                giochaymay = Minutes.minutesBetween(gioBD, gioKT).getMinutes();
            } catch (Exception e){}
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
        ((TextView) findViewById(R.id.tvGioSetup)).setText(giosetmay == 0?"":giosetmay+"'");
        ((TextView) findViewById(R.id.tvGioChay)).setText(giochaymay == 0?"":giochaymay+"'");
        ((TextView) findViewById(R.id.tvGioNC)).setText(congViec.GioNguyenCong == 0 ?"":congViec.GioNguyenCong+"'");
        ((TextView) findViewById(R.id.tvSLDD)).setText(congViec.TongDoDang+"");
        ((TextView) findViewById(R.id.tvSLGood)).setText(congViec.TongGood+"");
        ((TextView) findViewById(R.id.tvSLNoGood)).setText(congViec.TongNoGood+"");
        ((TextView) findViewById(R.id.tvGhiChu)).setText(congViec.GhiChu);
        if (congViec.GioKT == null) {
            findViewById(R.id.linearLayoutAll).setVisibility(View.GONE);
        }
    }

    private void OnBtnXoaClick() { // Lại là nhắc nhở thôi
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
        intent.putExtra("CongViec", congViec);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateCongViec(){
        TextView tvChiTiet = (TextView) findViewById(R.id.tvChiTiet);
        TextView tvMay = (TextView) findViewById(R.id.tvMay);
        TextView tvNguyenCong = (TextView) findViewById(R.id.tvNguyenCong);
        TextView tvBuocs = (TextView) findViewById(R.id.tvBuocs);

        tvChiTiet.setText(congViec.ChiTiet);
        if (congViec.May != null)
            tvMay.setText(congViec.May.MaSo);
        else
            tvMay.setText("");
        tvBuocs.setText(congViec.Buocs);
        if (congViec.NguyenCong != null)
            tvNguyenCong.setText(congViec.NguyenCong.NoiDung);
        else
            tvNguyenCong.setText(null);
    }

    int tempPos;

    public void OnBtnSuaCv2Click(int ref) { // Dialog để nhập thông tin những cái mà bạn muốn sửa, cái nào đúng thì kệ nó đi.

        final int pos = ref;
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_so_luong, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(v)
            .setTitle("Sửa số lượng")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int slGood =0;
                        int slNoGood = 0;

                        try {
                            final EditText good = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editGood);
                            final EditText noGood = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.editNoGood);

                            slGood = Integer.parseInt(good.getText().toString());
                            slNoGood = Integer.parseInt(noGood.getText().toString());

                        } catch(Exception ex){

                        }

                        CongViec2Activity.this.SuaSoLuong(pos, slGood,slNoGood);

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

        final EditText good = (EditText) dialog.findViewById(R.id.editGood);
        final EditText noGood = (EditText) dialog.findViewById(R.id.editNoGood);


        good.setText(congViec.CongViec2Set.get(ref).Good.toString());
        noGood.setText(congViec.CongViec2Set.get(ref).NoGood.toString());
        LinearLayout doDang = (LinearLayout) dialog.findViewById(R.id.lineedtDoDang);
        doDang.setVisibility(View.GONE);

        good.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private void SuaSoLuong(int ref, Integer slGood ,Integer slNoGood) { // 1 hàm có lẽ hơi dư tí, nhưung không sao, code thêm dài, đọc thêm mệt tí
        tempPos = ref;
        new CongViec2Activity.UpdateSoLuongTask().execute(slGood, slNoGood);
    }

    public void OnBtnXoaHinhClick(int ref) {  // Cái này để nhắc nhở thôi
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

    class UpdateSoLuongTask extends AsyncTask<Integer, Long, Integer> { // Thay đổi số lượng đạt với không đạt nếu nhập nhầm thôi
        MyProgressDialog progressBar;
        CongViec2 tempCongViec2;
        int good;
        int nogood;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            try {
                good    = params[0];
                nogood  = params[1];

                tempCongViec2 = congViec.CongViec2Set.get(tempPos);

                OEntity CongViecs = consumer.getEntity("CongViec2Set",tempCongViec2.Id).execute();
                consumer.updateEntity(CongViecs)
                        .properties(OProperties.int32("SoLuongG", good))
                        .properties(OProperties.int32("SoLuongNG", nogood))
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
                    tempCongViec2.Good = good;
                    tempCongViec2.NoGood = nogood;
                    congViec.CapNhatSlg();
                    HienThi();
                    UpdateAdapter();
                    break;
                case -1:
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }

            progressBar.dismiss();
        }
    }


    public void OnBtnXoaCv2Click(int ref) { // Dialog nhắc bạn có chắc chắn xóa không, xóa rồi đừng hỏi nha, vì đã có nhắc nhở rồi

        final int pos = ref;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        tempPos = pos;
                        new CongViec2Activity.XoaCv2Task().execute();
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

    class XoaCv2Task extends AsyncTask<Integer, Long, Integer> { // Xóa 1 thông tin số lượng
        MyProgressDialog progressBar;
        CongViec2 tempCongViec2;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                tempCongViec2 = congViec.CongViec2Set.get(tempPos);

                consumer.deleteEntity("CongViec2Set", tempCongViec2.Id)
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
                    congViec.CongViec2Set.remove(tempCongViec2);
                    congViec.CapNhatSlg();
                    UpdateAdapter();
                    break;
                case -1:
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }

            progressBar.dismiss();
        }
    }

    class XoaHinhTask extends AsyncTask<Integer, Long, Integer> {  // xóa hình được chọn thôi.
        MyProgressDialog progressBar;
        HinhDinhKem hinh;
        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
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
                    UpdateAdapter();
                    break;
                case -1:
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }

            progressBar.dismiss();
        }
    }

    public void OnBtnSuaChiTietClick() {
        state = SUA_CONG_VIEC_CHI_TIET;
        scan("Quét mã chi tiết");
    }

    public void OnBtnSuaMayClick() {
        state = SUA_CONG_VIEC_MAY;
        new ChonMayTask().execute();
    }

    class ChonMayTask extends AsyncTask<Integer, Long, Integer> { // nhiều quá đi
        MyProgressDialog progressBar;
        ArrayList<May> choiceList = new ArrayList<May>();


        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
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
                            temp != null ? temp: 0
                    );
                    choiceList.add(m);
                }
                May emtpy = new May(0, "Không có máy", CongViec2Activity.this.congViec.NhanVien.ToSX_Id);
                choiceList.add(0, emtpy);
                return 1;
            } catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                LayoutInflater inflater = (LayoutInflater)CongViec2Activity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.dialog_chon_may, null);
                final AlertDialog builder = new AlertDialog.Builder(CongViec2Activity.this)
                        .setTitle("Chọn máy")
                        .setIcon(R.drawable.logo)
                        .setView(v)
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                builder.show();

                final EditText editsearch = (EditText) builder.findViewById(R.id.editSearch);
                final CheckBox checkBox = (CheckBox)builder.findViewById(R.id.allMayCheckBox);
                final MayAdapter adt = new MayAdapter(CongViec2Activity.this,
                        R.layout.item_may, choiceList);

                // Capture Text in EditText
                editsearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                        adt.getFilter().filter(
                                (checkBox.isChecked() ? " " : congViec.NhanVien.ToSX_Id ) + ": " + text
                        );
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
                            adt.getFilter().filter(congViec.NhanVien.ToSX_Id + ": " + text);
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
                        if (tempMay.Id == 0)
                            tempMay = null;

                        new CongViec2Activity.SuaMayTask().execute();
                        builder.dismiss();
                    }
                });
                checkBox.setChecked(false);

            } else {
                Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }

            progressBar.dismiss();
        }


    }


    class SuaMayTask extends AsyncTask<String, Long, Integer> { // sửa lại thông tin là đang sử dụng máy nào
        MyProgressDialog progressBar;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {

                OEntity CongViecs = consumer.getEntity("CongViecs",congViec.Id).execute();

                consumer.updateEntity(CongViecs)
                        .properties(OProperties.int32("CongViec_May1", tempMay!=null ? tempMay.Id:null))
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
                    congViec.May = tempMay;
                    updateCongViec();
                    break;
                case -1:
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }


    class XoaCvTask extends AsyncTask<String, Long, Integer> {  // Xóa công việc hiện đang được chọn, thoát màn hình này luôn, trả về null
        MyProgressDialog progressBar;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                int id = congViec.Id;

                consumer.deleteEntity("CongViecs", id)
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
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    void Hienthihinh(HinhDinhKem hinhDinhKem){
        LayoutInflater inflater = (LayoutInflater) CongViec2Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_hinh_dinh_kem, null);
        final AlertDialog builder = new AlertDialog.Builder(CongViec2Activity.this)
                .setView(v)
                .create();
        builder.show();

        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhDinhKem.Data, 0, hinhDinhKem.Data.length);
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
        LayoutInflater inflater = (LayoutInflater) CongViec2Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_hinh_dinh_kem, null);
        final AlertDialog builder = new AlertDialog.Builder(CongViec2Activity.this)
                .setView(v)
                .create();
        builder.show();
        ((ImageView) builder.findViewById(R.id.imageViewHinh)).setImageBitmap(bitmap);
        builder.findViewById(R.id.buttonChupLai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(CongViec2Activity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
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

    private void scan(String prompt) {  // chuyển màn hình quét mã.
        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class)
                .setPrompt(prompt)
                .initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  // Nhảy vào khi quay trở lại màn hình này, hiểu nôm na là như vậy
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
                switch (state) {
                    case SUA_CONG_VIEC_CHI_TIET:
                        tempChiTiet = ma;
                        new ChonMaTask().execute(ma);
                        break;
                    case SUA_CONG_VIEC_MAY:
                        new SuaMayTask().execute(ma);
                        break;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class UpdateHinhTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        HinhDinhKem tempHinh;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
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
                    UpdateAdapter();
                    Toast.makeText(CongViec2Activity.this, "Đã Update hình ảnh thành công.", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
    }

    class ChonMaTask extends AsyncTask<String, Long, Integer> {
        MyProgressDialog progressBar;
        ArrayList<May> choiceList = new ArrayList<May>();
        String maExtra;
        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
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
                    LayoutInflater inflater = (LayoutInflater) CongViec2Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.dialog_chon_may, null);
                    final AlertDialog builder = new AlertDialog.Builder(CongViec2Activity.this)
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
                    final MayAdapter adt = new MayAdapter(CongViec2Activity.this, R.layout.item_may, choiceList);
                    ListView lv = (ListView) builder.findViewById(R.id.list_may);
                    lv.setAdapter(adt);
                    // Capture Text in EditText

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
                            May may = adt.getItem(which);
                            tempChiTiet = may.MaSo;
                            new ChonNguyenCongTask().execute(tempChiTiet,maExtra);
                            Toast.makeText(CongViec2Activity.this, may.MaSo + "", Toast.LENGTH_SHORT).show();
                            builder.dismiss();
                        }
                    });
                } else {
                    if (choiceList.size() ==1){
                        tempChiTiet= choiceList.get(0).MaSo;
                        new ChonNguyenCongTask().execute(tempChiTiet,maExtra);
                        Toast.makeText(CongViec2Activity.this,"Scanned "+ choiceList.get(0).MaSo, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CongViec2Activity.this, "Không có mã", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                if ( result == 2) {
                   new ChonNguyenCongTask().execute(tempChiTiet,maExtra);
                    Toast.makeText(CongViec2Activity.this, "Scanned "+ tempChiTiet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.dismiss();
        }
    }

    private class ChonNguyenCongTask extends AsyncTask<String, Long, Integer> {  // Cái này hơi dài dòng lười giải thích quá à
        MyProgressDialog progressBar;
        ArrayList<NguyenCong> choiceList = new ArrayList<NguyenCong>();

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String ma = params[0];
                if (LaChiTietHL(params[1]))
                {
                    for (OEntity ct : consumer.getEntities("ChiTietHLs")
                            .filter("TenNgan eq '" + ma + "'")
                            .expand("NguyenCongHLs")
                            .execute()) {
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
                            .expand("NguyenCongs")
                            .execute()) {
                        ORelatedEntitiesLink l = ct.getLink("NguyenCongs", ORelatedEntitiesLinkInline.class);
                        for (OEntity nc : l.getRelatedEntities()) {
                            NguyenCong newNc = new NguyenCong(  );
                            newNc.Id =nc.getProperty("Id", Integer.class).getValue();
                            OEntity toSX = consumer.getEntity("ToSXes",nc.getProperty("NguyenCong_ToSX", Integer.class).getValue()).execute();
                            newNc.NoiDung =
                                    toSX.getProperty("Ten", String.class).getValue()
                                            + "-" +
                                            nc.getProperty("NoiDung", String.class).getValue();
                            try{
                                newNc.NguyenCong = nc.getProperty("NhanVienKH", Integer.class).getValue() != congViec.NhanVien.Id?true:false;
                            }catch (Exception e){
                                newNc.NguyenCong = false;
                            }
                            try{
                                newNc.To = nc.getProperty("NhanVien_ToSX", Integer.class).getValue() != congViec.NhanVien.ToSX_Id?true:false;
                            }catch (Exception e){
                                newNc.To = false;
                            }
                            newNc.CoPhoi = nc.getProperty("NgayCoPhoi", LocalDateTime.class).getValue() != null?true:false;
                            newNc.HoanThanh = nc.getProperty("NgayHTTT", LocalDateTime.class).getValue() != null?true:false;
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

                    LayoutInflater inflater = (LayoutInflater) CongViec2Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.dialog_chon_nguyen_cong, null);
                    final AlertDialog builder = new AlertDialog.Builder(CongViec2Activity.this)
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
                    final NguyenCongAdapter adt = new NguyenCongAdapter(CongViec2Activity.this,
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
                            tempNguyenCong = choiceList.get(which);
                            if (tempNguyenCong.Id == 0)
                            {
                                tempNguyenCong = null;
                                tempBuoc = null;
                                new SuaCongViecTask().execute();
                            }else {
                                new ChonBuocTask().execute(tempNguyenCong.Id);
                            }
                            builder.dismiss();
                        }
                    });
                    checkBoxAll.setChecked(false);
                }
                else  {
                    tempNguyenCong = null;
                    tempBuoc = null;
                    new SuaCongViecTask().execute();
                }
            } else {
                Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
            }
            progressBar.dismiss();
        }
    }

    class ChonBuocTask extends AsyncTask<Integer, Long, Integer> {  // Sửa đổi lại ở bước thực hiện nào nếu lúc đầu tạo công việc bị sai
        MyProgressDialog progressBar;
        ArrayList<String> choiceList = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                Integer ncId = params[0];

                OEntity nc = consumer.getEntity("NguyenCongHLs", ncId).expand("BuocHLs").execute() ;
                ORelatedEntitiesLink l = nc.getLink("BuocHLs", ORelatedEntitiesLinkInline.class);
                for (OEntity buoc : l.getRelatedEntities()) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(CongViec2Activity.this);
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

                            new CongViec2Activity.SuaCongViecTask().execute();
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
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                }
            } else {
                new CongViec2Activity.SuaCongViecTask().execute();
            }

            progressBar.dismiss();
        }
    }

    class SuaCongViecTask extends AsyncTask<CongViec, Long, Integer> {  // Sửa tên công việc bằng cách quét mã lại thôi à
        MyProgressDialog progressBar;

        @Override
        protected void onPreExecute(){
            progressBar = new MyProgressDialog();
            progressBar.show(CongViec2Activity.this);
        }

        @Override
        protected Integer doInBackground(CongViec... params) {
            try {
                if (congViec.PhanLoai == 1) {
                    String temp;
                    if (LaChiTietHL(tempChiTiet)){
                        temp = "CongViec_NguyenCongHL1";
                    }
                    else{
                        temp = "CongViec_NguyenCong";
                    }

                    OEntity CongViecs = consumer.getEntity("CongViecs",congViec.Id).execute();

                    consumer.updateEntity(CongViecs)
                            .properties(OProperties.string("MaChiTiet", tempChiTiet))
                            .properties(OProperties.int32(temp, tempNguyenCong!=null? tempNguyenCong.Id:null))
                            .properties(OProperties.string("Buocs", tempBuoc))
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
                    congViec.ChiTiet = tempChiTiet;
                    congViec.NguyenCong = tempNguyenCong;
                    congViec.Buocs = tempBuoc;
                    updateCongViec();

                    break;
                case -1:
                    Toast.makeText(CongViec2Activity.this, "Kiểm tra wifi hoặc server", Toast.LENGTH_SHORT).show();
                    break;
            }
            progressBar.dismiss();
        }
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
