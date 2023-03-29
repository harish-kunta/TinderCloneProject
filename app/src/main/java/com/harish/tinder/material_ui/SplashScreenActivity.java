package com.harish.tinder.material_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.tinder.R;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    boolean isAndroidReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);


        View content = findViewById(android.R.id.content);

        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isAndroidReady)
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                dimissSplashScreen();
                return false;
            }
        });
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            Intent intent = new Intent(getApplicationContext(), ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void dimissSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAndroidReady = true;
            }
        }, 10000);
    }
}