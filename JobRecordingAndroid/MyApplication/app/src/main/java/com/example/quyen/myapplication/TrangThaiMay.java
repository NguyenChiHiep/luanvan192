package com.example.quyen.myapplication;

public class TrangThaiMay {
    public int id;
    public String NoiDungTG;
    public String NoiDung;

    public TrangThaiMay(int id, String noiDung) {
        this.id = id;
        NoiDung = noiDung;
        String ndtg = "";
        String[] tokens = noiDung.split(" ");
        for (String a: tokens){
            ndtg +=a.charAt(0);
        }
        NoiDungTG = ndtg;
    }
}
