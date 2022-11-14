package com.harish.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;

public class Info extends AppCompatActivity {
    private EditText mAboutMe;
    private EditText mInterests;
    private EditText mJob;
    private EditText mCompany;
    private EditText mSchool;
    private EditText mLivingIn;
    private RadioGroup mRadioGroup;
    private Button mSaveChanges;

    private String aboutMe,interests,job,company,school,livingIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mAboutMe = (EditText) findViewById(R.id.editTextTextPersonName3);
        mInterests = (EditText) findViewById(R.id.editTextTextPersonName4);
        mJob = (EditText) findViewById(R.id.editTextTextPersonName5);
        mCompany = (EditText) findViewById(R.id.editTextTextPersonName6);
        mSchool = (EditText) findViewById(R.id.editTextTextPersonName7);
        mLivingIn = (EditText) findViewById(R.id.editTextTextPersonName8);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        mSaveChanges = (Button) findViewById(R.id.submit);
        mSaveChanges.setOnClickListener(view -> {
                    int selectId = mRadioGroup.getCheckedRadioButtonId();
                    aboutMe = mAboutMe.getText().toString();
                    interests = mInterests.getText().toString();
                    job = mJob.getText().toString();
                    company = mCompany.getText().toString();
                    school = mSchool.getText().toString();
                    livingIn = mLivingIn.getText().toString();
                    View rootLayout = null;
            if (selectId == -1) {
                Snackbar.make(rootLayout, " Gender selected", Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(rootLayout, " Gender not selected", Snackbar.LENGTH_LONG).show();
            }
                    Intent intent = new Intent(Info.this, MainActivity.class);
                    startActivity(intent);
                    return;
                }
                );


    }
}