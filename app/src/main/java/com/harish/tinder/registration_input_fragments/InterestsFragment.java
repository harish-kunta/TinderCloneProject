package com.harish.tinder.registration_input_fragments;

import static com.harish.tinder.model.FirebaseConstants.INTERESTS;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.R;
import com.harish.tinder.material_ui.EnableLocationActivity;
import com.harish.tinder.model.FirebaseConstants;
import com.harish.tinder.model.Interest;
import com.harish.tinder.utils.StringResourceHelper;

import java.util.ArrayList;
import java.util.List;


public class InterestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Interest> selectedInterests = new ArrayList<>();

    private RecyclerView mResultList;
    Context context;
    Button agreeButton;
    private LinearLayout rootLayout;

    private DatabaseReference mInterestsDatabase;
    private DatabaseReference mUserDatabase;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseUser mCurrentUser;
    View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public InterestsFragment() {
        // Required empty public constructor
    }

    public static InterestsFragment newInstance(String param1, String param2) {
        InterestsFragment fragment = new InterestsFragment();
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
        context = getContext();
        view = inflater.inflate(R.layout.fragment_interests, container, false);
        rootLayout = view.findViewById(R.id.root_layout);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS).child(current_uid);
        mUserDatabase.keepSynced(true);
        mResultList = view.findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(context));
        agreeButton = view.findViewById(R.id.agree_button);
        mInterestsDatabase = FirebaseDatabase.getInstance().getReference(INTERESTS);
        mInterestsDatabase.keepSynced(true);
        mResultList.setAdapter(firebaseRecyclerAdapter);
        getInterests();

        agreeButton.setOnClickListener(v -> {
            if (selectedInterests.size() != 5) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(getContext(), R.string.interests_not_selected), Snackbar.LENGTH_LONG).show();
                return;
            }
            mUserDatabase.child(INTERESTS).setValue(selectedInterests).addOnCompleteListener(task -> {
                Intent intent = new Intent(getContext(), EnableLocationActivity.class);
                startActivity(intent);
                getActivity().finish();
            });
        });
        return view;
    }

    public void getInterests() {
        mInterestsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> receiver = new ArrayList<>();
                String record = "";
                List<Interest> t = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    record = ds.getValue().toString();
                    receiver.add(record);
                    t.add(new Interest(record));
                }
                SimpleRecyclerAdapter<Interest, interestsBinder> adapter = new SimpleRecyclerAdapter<>(new interestsBinder());
                mResultList.setAdapter(adapter);
                adapter.setData(t);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class interestsBinder extends ItemBinder<Interest, interestsBinder.InterestsViewHolder> {

        @Override
        public InterestsViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new InterestsViewHolder(inflater.inflate(R.layout.interest_item_layout, parent, false));
        }

        @Override
        public void bind(final InterestsViewHolder holder, final Interest item) {
            holder.interest_name.setText(item.getName());
            holder.itemView.setOnClickListener(v -> {
                item.setSelected(!item.isSelected());
                holder.itemView.setBackgroundColor(item.isSelected() ? Color.CYAN : Color.WHITE);
                if(item.isSelected())
                    selectedInterests.add(item);
                else
                    selectedInterests.remove(item);
            });
        }

        @Override
        public boolean canBindData(Object item) {
            return item instanceof Interest;
        }

        class InterestsViewHolder extends BaseViewHolder<Interest> {
            TextView interest_name;
            RelativeLayout relativeLayout;

            public InterestsViewHolder(View itemView) {
                super(itemView);
                interest_name = itemView.findViewById(R.id.interest_name);
                relativeLayout = itemView.findViewById(R.id.interest_layout);
            }
        }

    }
}
