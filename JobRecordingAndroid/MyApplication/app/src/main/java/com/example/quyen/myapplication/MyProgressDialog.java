package com.example.quyen.myapplication;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Quyen on 12/29/2016.
 */

public class MyProgressDialog {

    ProgressDialog progressBar;
    public  static boolean CanBackPress = true;

    public void show(Context context){
        CanBackPress = false;

        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(false);
        //progressBar.setMessage("Vui lòng chờ ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
    }

    public void dismiss(){
        progressBar.dismiss();
        CanBackPress = true;
    }
}
