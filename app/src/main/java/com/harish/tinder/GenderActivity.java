package com.harish.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.harish.tinder.material_ui.LoginActivity;

public class GenderActivity extends AppCompatActivity {
    private Button mWoman;
    private Button button;
    private Button mMan;
    private Button mOthers;
    private Button mContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        mWoman = (Button) findViewById(R.id.button4);
        mMan = (Button) findViewById(R.id.button5);
        mOthers = (Button) findViewById(R.id.button6);
        mContinue = (Button) findViewById(R.id.button7);
       // button.setOnClickListener(view -> startActivity(new Intent(GenderActivity.this, DOBActivity.class)));
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GenderActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

}