package com.harish.tinder.utils;

import android.content.Context;
import android.content.res.Resources;

import com.harish.tinder.R;

public class StringResourceHelper {
    public static String getString(Context context, int id){
        Resources res = context.getResources();
        return res.getString(id);
    };
}
