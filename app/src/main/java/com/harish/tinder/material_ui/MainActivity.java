package com.harish.tinder.material_ui;

import static com.harish.tinder.model.Constants.DEVICE_TOKEN;
import static com.harish.tinder.model.Constants.ONLINE;
import static com.harish.tinder.model.Constants.USERS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.harish.tinder.R;
import com.harish.tinder.fragments.ChatThreadFragment;
import com.harish.tinder.fragments.LikesFragment;
import com.harish.tinder.fragments.ProfileFragment;
import com.harish.tinder.fragments.SwipeFragment;
import me.ibrahimsn.lib.SmoothBottomBar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (firebaseUser != null) {
            ref = FirebaseDatabase.getInstance().getReference().child(USERS).child(firebaseUser.getUid());
        } else {
            // No user is signed in
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, getString(R.string.fcm_token_fetch_failed), task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();
            Log.d(TAG, token);
            ref.child(DEVICE_TOKEN).setValue(token);
        });

        //TODO : Update the activity to take to profile picture page
//        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child("profileImageUrl").getValue() == null || "default".equals(snapshot.child("profileImageUrl").getValue().toString())) {
//                    Intent intent = new Intent(getApplicationContext(), UploadImageActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        replace(new SwipeFragment());
        SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);

        bottomBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case 0:
                    replace(new SwipeFragment());
                    break;
                case 1:
                    replace(new LikesFragment());
                    break;
                case 2:
                    replace(new ChatThreadFragment());
                    break;
                case 3:
                    replace(new ProfileFragment());
                    break;
            }
            return true;
        });

    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            ref.child(ONLINE).setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.child(ONLINE).setValue(false);
    }
}