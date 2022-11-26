package com.harish.tinder.material_ui;

import static com.harish.tinder.model.FirebaseConstants.TERMS_AGREED;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.tinder.R;
import com.harish.tinder.model.FirebaseConstants;

public class HouseRulesActivity extends AppCompatActivity {

    Button agreeButton;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_rules);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS).child(current_uid);
        mUserDatabase.keepSynced(true);
        agreeButton = findViewById(R.id.agree_button);
        agreeButton.setOnClickListener(v -> mUserDatabase.child(TERMS_AGREED).setValue(true).addOnCompleteListener(task -> {
            Intent startIntent = new Intent(getApplicationContext(), UploadImageActivity.class);
            startActivity(startIntent);
            finish();
        }));
    }
}