package com.harish.tinder.material_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.harish.tinder.R;
import com.harish.tinder.fragments.HomeFragment;
import com.harish.tinder.fragments.MatchesFragment;
import com.harish.tinder.fragments.LikesFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;


public class MainActivity extends AppCompatActivity {


    private SmoothBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        replace(new HomeFragment());
        bottomBar = findViewById(R.id.bottomBar);

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i){
                    case 0:
                        replace(new HomeFragment());
                        break;

                    case 1:
                        replace(new LikesFragment());
                        break;

                    case 2:
                        replace(new MatchesFragment());
                        break;
                }
                return true;
            }
        });

    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,fragment);
        transaction.commit();
    }
}