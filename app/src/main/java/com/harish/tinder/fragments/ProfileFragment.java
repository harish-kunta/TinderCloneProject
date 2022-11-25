package com.harish.tinder.fragments;

import static com.harish.tinder.model.Constants.DOB;
import static com.harish.tinder.model.Constants.EMAIL;
import static com.harish.tinder.model.Constants.NAME;
import static com.harish.tinder.model.Constants.ONLINE;
import static com.harish.tinder.model.Constants.PROFILE_IMAGE_URL;
import static com.harish.tinder.model.Constants.USERS;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.tinder.ChooseLoginRegistrationActivity;
import com.harish.tinder.ImagePickerActivity;
import com.harish.tinder.R;
import com.harish.tinder.material_ui.SettingsActivity;
import com.harish.tinder.utils.AgeCalculator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment {
    private TextView mAboutMe;
    private TextView mInterests;
    private TextView mJob;
    private TextView mCompany;
    private TextView mSchool;
    private TextView mLivingIn;
    public String aboutMe, interests, job, company, school, livingIn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mProfileView;
    private final FirebaseAuth mAuth;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView profileName;
    TextView profileEmail;
    ImageView editProfileButton;
    ImageView settingsButton;
    CircleImageView profilePic;
    StorageReference storageReference;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(USERS);

    private final int PICK_IMAGE_REQUEST = 71;

    public ProfileFragment() {
        // Required empty public constructor
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mProfileView = inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileView.findViewById(R.id.sign_out).setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(getContext(), ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        profileName = mProfileView.findViewById(R.id.profile_name);
        profileEmail = mProfileView.findViewById(R.id.profile_email);

        editProfileButton = mProfileView.findViewById(R.id.edit_profile);
        settingsButton = mProfileView.findViewById(R.id.settings);


        mAboutMe = mProfileView.findViewById(R.id.TextViewPersonName3);
        mInterests = mProfileView.findViewById(R.id.TextViewPersonName4);
        mJob = mProfileView.findViewById(R.id.TextViewPersonName5);
        mCompany = mProfileView.findViewById(R.id.TextViewPersonName6);
        mSchool = mProfileView.findViewById(R.id.TextViewPersonName7);
        mLivingIn = mProfileView.findViewById(R.id.TextViewPersonName8);
        mLivingIn.setOnClickListener(view -> {
            aboutMe = mAboutMe.getText().toString();
            interests = mInterests.getText().toString();
            job = mJob.getText().toString();
            company = mCompany.getText().toString();
            school = mSchool.getText().toString();
            livingIn = mLivingIn.getText().toString();
        });

        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ImagePickerActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        profilePic = mProfileView.findViewById(R.id.profile_image);
        storageReference = FirebaseStorage.getInstance().getReference();
        assert user != null;
        userRef.child(user.getUid()).child(ONLINE).setValue("true");
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            //TODO:remove this annotation
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = getObjectString(dataSnapshot.child(NAME).getValue());
                String email = getObjectString(dataSnapshot.child(EMAIL).getValue());
                long dobUnix = getObjectLong(dataSnapshot.child(DOB).getValue());
                String image = getObjectString(dataSnapshot.child(PROFILE_IMAGE_URL).getValue());
                int age = AgeCalculator.calculateAge(dobUnix);
                profileName.setText(display_name + ", " + age);
                profileEmail.setText(email);
                if (!image.equals("default")) {
                    Glide
                            .with(mProfileView.getContext())
                            .load(image)
                            .into(profilePic);
                } else {
                    profilePic.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_account_circle_white));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        return mProfileView;
    }

    String getObjectString(Object o) {
        if (o != null) {
            return o.toString();
        }
        return "";
    }

    long getObjectLong(Object o) {
        if (o != null) {
            return (Long) o;
        }
        return -1;
    }
}