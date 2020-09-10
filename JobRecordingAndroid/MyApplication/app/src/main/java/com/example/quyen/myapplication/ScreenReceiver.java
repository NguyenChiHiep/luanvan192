package com.example.quyen.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Quyen on 1/18/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Activity activity = (Activity)context;
            if (MyProgressDialog.CanBackPress && CustomScannerActivity.CanBackPress){
                activity.onBackPressed();
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {

        }
    }
}