package com.harish.tinder.registration_input_fragments;

import static com.harish.tinder.model.FirebaseConstants.CONNECTIONS;
import static com.harish.tinder.model.FirebaseConstants.NAME;
import static com.harish.tinder.model.FirebaseConstants.PROFILE_IMAGE_URL;
import static com.harish.tinder.model.FirebaseConstants.TERMS_AGREED;
import static com.harish.tinder.model.FirebaseConstants.USERS;
import static com.harish.tinder.model.FirebaseConstants.YEPS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.R;
import com.harish.tinder.adapter.LikesAdapter;
import com.harish.tinder.material_ui.RegistrationInputActivity;
import com.harish.tinder.material_ui.UploadImageActivity;
import com.harish.tinder.model.FirebaseConstants;
import com.harish.tinder.model.UserObject;
import com.harish.tinder.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseRulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseRulesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button agreeButton;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mLikesView;

    public HouseRulesFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseRulesFragment newInstance(String param1, String param2) {
        HouseRulesFragment fragment = new HouseRulesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLikesView = inflater.inflate(R.layout.fragment_house_rules, container, false);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS).child(current_uid);
        mUserDatabase.keepSynced(true);
        agreeButton = mLikesView.findViewById(R.id.agree_button);
        agreeButton.setOnClickListener(v -> mUserDatabase.child(TERMS_AGREED).setValue(true).addOnCompleteListener(task -> {
            ((RegistrationInputActivity)getActivity()).replace(new SexualOrientationFragment());
        }));
        return mLikesView;
    }
}