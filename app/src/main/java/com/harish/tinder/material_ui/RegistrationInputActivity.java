package com.harish.tinder.material_ui;

import static com.harish.tinder.model.FirebaseConstants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.R;
import com.harish.tinder.model.FirebaseDbUser;
import com.harish.tinder.registration_input_fragments.HouseRulesFragment;
import com.harish.tinder.registration_input_fragments.SexualOrientationFragment;


public class RegistrationInputActivity extends AppCompatActivity {

    private static final String TAG = RegistrationInputActivity.class.getSimpleName();
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref;
    FirebaseDbUser firebaseDbUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (firebaseUser == null) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            ref = FirebaseDatabase.getInstance().getReference().child(USERS).child(firebaseUser.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    firebaseDbUser = snapshot.getValue(FirebaseDbUser.class);
                    if (firebaseDbUser == null || firebaseDbUser.getUid() == null) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_registration_input);
            replace(new SexualOrientationFragment());
//            SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);
//
//            bottomBar.setOnItemSelectedListener(i -> {
//                switch (i) {
//                    case 0:
//                        replace(new SwipeFragment());
//                        break;
//                    case 1:
//                        replace(new LikesFragment());
//                        break;
//                    case 2:
//                        replace(new ChatFragment());
//                        break;
//                    case 3:
//                        replace(new ProfileFragment());
//                        break;
//                }
//                return true;
//            });
        }
    }

    public void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}