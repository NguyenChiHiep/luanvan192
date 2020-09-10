package com.example.quyen.myapplication;

public class NhanCong {
    public int id;
    public String TieuDe;
    public String CongViec;
    public String MaChiTiet;
    public String DTgLamViec;
    public String MayGiaCong;
    public int TimeSetup;
    public int TimeGiaCong;
    public int TimeNhanCong;
    public int TimeTangCa;
    public String GhiChu;

    public NhanCong(int id,String tieuDe, String congViec, String maChiTiet, String DTgLamViec, String mayGiaCong, int timeSetup, int timeGiaCong, int timeNhanCong,int timeTangCa, String ghiChu) {
        this.id = id;
        TieuDe = tieuDe;
        CongViec = congViec;
        MaChiTiet = maChiTiet;
        this.DTgLamViec = DTgLamViec;
        MayGiaCong = mayGiaCong;
        TimeSetup = timeSetup;
        TimeGiaCong = timeGiaCong;
        TimeNhanCong = timeNhanCong;
        TimeTangCa = timeTangCa;
        GhiChu = ghiChu;
    }
}
