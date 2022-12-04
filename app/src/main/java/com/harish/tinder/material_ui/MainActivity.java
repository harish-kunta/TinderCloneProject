package com.harish.tinder.material_ui;

import static com.harish.tinder.model.FirebaseConstants.DEFAULT;
import static com.harish.tinder.model.FirebaseConstants.DEVICE_TOKEN;
import static com.harish.tinder.model.FirebaseConstants.ONLINE;
import static com.harish.tinder.model.FirebaseConstants.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.harish.tinder.R;
import com.harish.tinder.main_fragments.ChatFragment;
import com.harish.tinder.main_fragments.LikesFragment;
import com.harish.tinder.main_fragments.ProfileFragment;
import com.harish.tinder.main_fragments.SwipeFragment;
import com.harish.tinder.model.FirebaseDbUser;

import me.ibrahimsn.lib.SmoothBottomBar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
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
                    } else if (!firebaseDbUser.isTermsAgreed()) {
                        Intent intent = new Intent(getApplicationContext(), RegistrationInputActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (firebaseDbUser.getLocation() == null) {
                        Intent intent = new Intent(getApplicationContext(), EnableLocationActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (firebaseDbUser.getProfileImageUrl() == null || DEFAULT.equals(firebaseDbUser.getProfileImageUrl())) {
                        Intent intent = new Intent(getApplicationContext(), UploadImageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);
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
                        replace(new ChatFragment());
                        break;
                    case 3:
                        replace(new ProfileFragment(firebaseDbUser));
                        break;
                }
                return true;
            });
        }
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
            if (ref != null) ref.child(ONLINE).setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ref != null) ref.child(ONLINE).setValue("false");
    }
}