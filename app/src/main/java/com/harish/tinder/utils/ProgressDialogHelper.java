package com.harish.tinder.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogHelper {
    public static ProgressDialog getProgressDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching profiles data");
        progressDialog.setCancelable(false);
        return progressDialog;
    }
}
