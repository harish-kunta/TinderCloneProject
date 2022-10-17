package com.harish.tinder.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.harish.tinder.R;

public class CustomDialog {
    public Button buttonRate, buttonClose;
    public AlertDialog alertDialog;
    public void createDialog(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.coustom_dialog,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        buttonRate=view.findViewById(R.id.buttonRate);
        buttonClose=view.findViewById(R.id.buttonClose);
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(), "Please Rate this Place ", Toast.LENGTH_LONG).show();
            }

        });
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }
}
