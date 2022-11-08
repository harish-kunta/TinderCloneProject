package com.harish.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NameActivity extends AppCompatActivity {
    private Button mContinue;
    private EditText mName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        mName = (EditText) findViewById(R.id.editTextTextPersonName);
        mContinue = (Button) findViewById(R.id.button2);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= mName.getText().toString();
                Intent intent = new Intent(NameActivity.this, DOBActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}