package com.harish.tinder.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.harish.tinder.R;
import com.harish.tinder.material_ui.SettingsActivity;
import com.harish.tinder.utils.AgeCalculator;
import com.harish.tinder.utils.Imageutils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mProfileView;
    private FirebaseAuth mAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView profileName;
    TextView profileEmail;
//    TextView contentEmail;
//    TextView contentName;
    ImageView editProfileButton;
    CircleImageView profilePic;
    StorageReference storageReference;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");

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
        mProfileView.findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), ChooseLoginRegistrationActivity.class);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        });
        profileName = (TextView) mProfileView.findViewById(R.id.profile_name);
        profileEmail = (TextView) mProfileView.findViewById(R.id.profile_email);

//        contentEmail = (TextView) mProfileView.findViewById(R.id.email_text);
        editProfileButton = (ImageView) mProfileView.findViewById(R.id.edit_profile);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

//        contentName = (TextView) mProfileView.findViewById(R.id.name_text);
        profilePic = (CircleImageView) mProfileView.findViewById(R.id.profile_image);
        storageReference = FirebaseStorage.getInstance().getReference();
        userRef.child(user.getUid()).child("online").setValue("true");
//        contentEmail.setText(user.getEmail());
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            //TODO:remove this annotation
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                long dobUnix = (Long) dataSnapshot.child("dob").getValue();
                String image = dataSnapshot.child("profileImageUrl").getValue().toString();
                LocalDate birthDate =
                        Instant.ofEpochSecond(dobUnix).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentDate
                        = LocalDate.now();
                int age = AgeCalculator.calculateAge(birthDate, currentDate);
                profileName.setText(display_name+", "+age);
                profileEmail.setText(email);
              //  contentName.setText(display_name);
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
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        //Log.e("Photo URL", filePath.toString());
//        Picasso.get().load(user.getPhotoUrl()).into(profilePic);
        return mProfileView;
    }


//    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
////        return ChronoUnit.YEARS.between(start, end);
//    }
}