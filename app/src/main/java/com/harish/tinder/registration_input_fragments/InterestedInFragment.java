package com.harish.tinder.registration_input_fragments;

import static com.harish.tinder.model.FirebaseConstants.INTERESTED_IN;
import static com.harish.tinder.model.FirebaseConstants.SEXUAL_ORIENTATION;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.tinder.R;
import com.harish.tinder.material_ui.RegistrationInputActivity;
import com.harish.tinder.model.FirebaseConstants;
import com.harish.tinder.utils.StringResourceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InterestedInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterestedInFragment extends Fragment {

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
    private View mInterestedInView;
    private ScrollView rootLayout;

    public InterestedInFragment() {
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
    public static InterestedInFragment newInstance(String param1, String param2) {
        InterestedInFragment fragment = new InterestedInFragment();
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
        mInterestedInView = inflater.inflate(R.layout.fragment_interested_in, container, false);
        rootLayout = mInterestedInView.findViewById(R.id.root_layout);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS).child(current_uid);
        mUserDatabase.keepSynced(true);
        agreeButton = mInterestedInView.findViewById(R.id.agree_button);
        List<String> items = new ArrayList<>();
        items.add("Women");
        items.add("Men");
        items.add("Everyone");

        RadioGroup mRadioGroup = (RadioGroup) mInterestedInView.findViewById(R.id.radiogroup);
        RadioButton[] rb = new RadioButton[items.size()];
        for (int i = 0; i < items.size(); i++) {
            rb[i] = new RadioButton(getContext());
            mRadioGroup.addView(rb[i]);
            rb[i].setText(items.get(i));
        }

        agreeButton.setOnClickListener(v -> {
            int selectId = mRadioGroup.getCheckedRadioButtonId();
            if (selectId == -1) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(getContext(), R.string.interest_not_selected), Snackbar.LENGTH_LONG).show();
                return;
            }
            final RadioButton radioButton = mRadioGroup.findViewById(selectId);
            mUserDatabase.child(INTERESTED_IN).setValue(radioButton.getText().toString()).addOnCompleteListener(task -> {
                ((RegistrationInputActivity)getActivity()).replace(new SchoolFragment());
            });
        });
        return mInterestedInView;
    }
}