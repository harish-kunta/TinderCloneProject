package com.harish.tinder.material_ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.tinder.R;
import com.harish.tinder.shared_preferences.SharedFirebasePreferences;
import com.harish.tinder.shared_preferences.SharedFirebasePreferencesContextWrapper;

public class EditProfileActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedFirebasePreferences mPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mPreferences = SharedFirebasePreferences.getDefaultInstance(this);
        mPreferences.keepSynced(true);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        mPreferences.pull().addOnPullCompleteListener(new SharedFirebasePreferences.OnPullCompleteListener() {
            @Override
            public void onPullSucceeded(SharedFirebasePreferences preferences) {
                showView();
            }

            @Override
            public void onPullFailed(Exception e) {
                showView();
                Toast.makeText(EditProfileActivity.this, "Fetch failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static class EditProfileFragment extends PreferenceFragmentCompat{
        private FirebaseUser mCurrentUser;
        FirebaseAuth mAuth;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.edit_profile_preferences, rootKey);
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            mAuth = FirebaseAuth.getInstance();
            if (mCurrentUser == null) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreferences != null) {
            mPreferences.keepSynced(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPreferences != null) {
            mPreferences.keepSynced(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreferences != null) {
            mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new SharedFirebasePreferencesContextWrapper(newBase));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
       //TODO : update this date to firebase
        showView();
    }

    private void showView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.settings, new EditProfileFragment()).commitAllowingStateLoss();
    }
}