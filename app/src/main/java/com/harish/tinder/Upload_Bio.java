package com.harish.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.harish.tinder.material_ui.LoginActivity;

public class Upload_Bio extends AppCompatActivity {
    private EditText a;
    private Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bio);
       a=(EditText) findViewById(R.id.editTextTextPersonName2);
       b=(Button)findViewById(R.id.button8);
       b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=a.getText().toString();
                Intent intent = new Intent(Upload_Bio.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}