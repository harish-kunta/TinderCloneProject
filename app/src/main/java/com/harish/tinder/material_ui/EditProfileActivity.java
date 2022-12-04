package com.harish.tinder.material_ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.harish.tinder.R;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new EditProfileFragment()).commit();
        }
    }

    public static class EditProfileFragment extends PreferenceFragmentCompat {
        private FirebaseAuth mAuth;
        private ProgressDialog mRegProgress;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = getPreferenceManager().getContext();
            setPreferencesFromResource(R.xml.edit_profile_preferences, rootKey);
            mAuth = FirebaseAuth.getInstance();

            mRegProgress = new ProgressDialog(getActivity(), R.style.AppThemeDialog);
            mRegProgress.setIndeterminate(true);
            mRegProgress.setCanceledOnTouchOutside(false);

        }

    }
}