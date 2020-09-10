package com.example.quyen.myapplication;

import java.io.Serializable;

public class CaLamViec implements Serializable {
    public int Id;
    public String MaCaLV;
    public String TenCa;
    public int GioNghi;

    public CaLamViec(int id, String maCaLV, String tenCa, int gioNghi) {
        Id = id;
        MaCaLV = maCaLV;
        TenCa = tenCa;
        GioNghi = gioNghi;
    }
}
