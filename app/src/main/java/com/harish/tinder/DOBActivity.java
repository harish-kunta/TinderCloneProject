package com.harish.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DOBActivity extends AppCompatActivity {
    private Button mContinue;
    private EditText mDOB;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dobactivity);
        mDOB = (EditText) findViewById(R.id.editTextDate);
        mContinue = (Button) findViewById(R.id.button3);
        //button.setOnClickListener(view -> startActivity(new Intent(DOBActivity.this, NameActivity.class)));
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mDOB.getText().toString();

                Intent intent = new Intent(DOBActivity.this, GenderActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}