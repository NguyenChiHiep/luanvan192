package com.example.quyen.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Quyen on 12/29/2016.
 */

public class NhanVien implements Serializable {
    public int Id;
    public  String MaSo;
    public String HoTen;
    public String Guid;
    public String Color;
    public int SlgMacDinh;
    public String MatKhau;
    public ArrayList<CongViec> congViecs;
    public boolean DoiMatKhau;
    public int ToSX_Id;
    public CaLamViec caLamViec;
    public int ThongKe_Id;

    public  NhanVien(int id, String ms, String ht, String guid, String color, int slgMacDinh, String pass, boolean doiMatKhau,
                     int toSX_Id){
        Id = id;
        MaSo = ms;
        HoTen = ht;
        Guid = guid;
        Color = color;
        SlgMacDinh = slgMacDinh;
        MatKhau = pass;
        congViecs = new ArrayList<CongViec>();
        DoiMatKhau = doiMatKhau;
        ToSX_Id = toSX_Id;
    }
}
