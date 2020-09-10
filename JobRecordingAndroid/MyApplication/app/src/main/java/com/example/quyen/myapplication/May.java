package com.example.quyen.myapplication;

import java.io.Serializable;

/**
 * Created by Quyen on 7/27/2017.
 */

public class May implements Serializable {
    public int Id;
    public  String MaSo;
    public int ToSX_Id;
    public  May(int id, String ma, int toSX_Id){
        Id = id;
        MaSo = ma;
        ToSX_Id = toSX_Id;
    }
}
