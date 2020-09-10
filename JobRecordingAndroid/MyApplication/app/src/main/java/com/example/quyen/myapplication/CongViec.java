package com.example.quyen.myapplication;

import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Quyen on 12/21/2016.
 */

public class CongViec implements Serializable {
    public int Id;
    public NhanVien NhanVien;
    public String ChiTiet;
    public NguyenCong NguyenCong;
    public String Buocs;
    public May May;

    public LocalDateTime GioBD;
    public LocalDateTime GioKTSetup;
    public LocalDateTime GioBDChay;
    public LocalDateTime GioTamDung1;
    public LocalDateTime GioTiepTuc1;
    public LocalDateTime GioTamDung2;
    public LocalDateTime GioTiepTuc2;
    public LocalDateTime GioKT;
    public LocalDateTime GioBD_BS;
    public LocalDateTime GioKTSetup_BS;
    public LocalDateTime GioBDChay_BS;
    public LocalDateTime GioTamDung1_BS;
    public LocalDateTime GioTiepTuc1_BS;
    public LocalDateTime GioTamDung2_BS;
    public LocalDateTime GioTiepTuc2_BS;
    public LocalDateTime GioKT_BS;
    public int GioNguyenCong;
    public String DoiTuongLD;
    public ArrayList<CongViec2> CongViec2Set;
    public ArrayList<HinhDinhKem> HinhDinhKems;
    public Integer TongGood;
    public Integer TongDoDang;
    public Integer TongNoGood;
    public CongViecPhu CongViecPhu;
    public String GhiChu;
    public String NoiDung;

    public int PhanLoai;

    public  CongViec(){
        CongViec2Set = new ArrayList<CongViec2>();
        HinhDinhKems = new ArrayList<HinhDinhKem>();
        TongGood = 0;
        TongNoGood = 0;
    }

    public void CapNhatSlg() {
        TongGood = 0;
        TongNoGood = 0;
        for (CongViec2 c : CongViec2Set){
            TongGood    += c.Good;
            TongNoGood  += c.NoGood;
        }
    }
}
