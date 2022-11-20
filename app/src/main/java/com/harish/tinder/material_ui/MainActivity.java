package com.harish.tinder.material_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.harish.tinder.R;
import com.harish.tinder.UploadImageActivity;
import com.harish.tinder.fragments.ChatThreadFragment;
import com.harish.tinder.fragments.LikesFragment;
import com.harish.tinder.fragments.ProfileFragment;
import com.harish.tinder.fragments.SwipeFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;


public class MainActivity extends AppCompatActivity {


    private SmoothBottomBar bottomBar;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference ref;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (user != null) {
            // User is signed in do nothing
            //Log.e("User:", user.getEmail().toString());
            ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        } else {
            // No user is signed in commence Login
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        replace(new SwipeFragment());
        bottomBar = findViewById(R.id.bottomBar);

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
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
            }
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
        if (user != null) {
            ref.child("online").setValue("true");
        }
    }

//    @Override
//    public void onNewToken(@NonNull String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token);
//    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.child("online").setValue("false");
    }
}