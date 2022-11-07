package com.harish.tinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainPageActivity extends AppCompatActivity {
   
    private Button iAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        iAgree = (Button) findViewById(R.id.button);
        iAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPageActivity.this, ChooseLoginRegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }


}
