package com.harish.tinder.material_ui;

import static com.harish.tinder.model.FirebaseConstants.ABOUT_ME;
import static com.harish.tinder.model.FirebaseConstants.USERS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.R;
import com.harish.tinder.model.FirebaseConstants;
import com.harish.tinder.model.FirebaseDbUser;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new EditProfileFragment()).commit();
        }

    }

    public static class EditProfileFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        private ProgressDialog mRegProgress;
        private DatabaseReference mUserDatabase;
        private FirebaseUser mCurrentUser;
        DatabaseReference ref;
        FirebaseDbUser firebaseDbUser;
        FirebaseAuth mAuth;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.edit_profile_preferences, rootKey);
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            String current_uid = mCurrentUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS).child(current_uid);
            mUserDatabase.keepSynced(true);

            mRegProgress = new ProgressDialog(getActivity(), R.style.AppThemeDialog);
            mRegProgress.setIndeterminate(true);
            mRegProgress.setCanceledOnTouchOutside(false);

            Preference aboutMePref = findPreference("about_me_title"); //You can put this string key in string resources in fact
            aboutMePref.setOnPreferenceChangeListener(this);

            mAuth = FirebaseAuth.getInstance();
            if (mCurrentUser == null) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                ref = FirebaseDatabase.getInstance().getReference().child(USERS).child(mCurrentUser.getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firebaseDbUser = snapshot.getValue(FirebaseDbUser.class);
                        PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .edit()
                                .putString("about_me_title", firebaseDbUser.getAboutMe()).apply();

                        PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .edit()
                                .putString("interests", firebaseDbUser.getInterests().toString()).apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals("about_me_title")) {
                String aboutMe = newValue.toString();
                mUserDatabase.child(ABOUT_ME).setValue(aboutMe).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //preference.setSummary(aboutMe);
                        } else {
                            Toast.makeText(getActivity(), "Issue with uploading", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            return true;
        }
    }
}