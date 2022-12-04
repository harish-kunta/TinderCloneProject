package com.harish.tinder.registration_input_fragments;

import static com.harish.tinder.model.FirebaseConstants.INTERESTS;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.R;
import com.harish.tinder.model.Interests;

import java.util.ArrayList;
import java.util.List;


public class InterestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mResultList;
    Context context;

    private DatabaseReference mInterestsDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
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
        mResultList = view.findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(context));

        mInterestsDatabase = FirebaseDatabase.getInstance().getReference(INTERESTS);
        mInterestsDatabase.keepSynced(true);
        mResultList.setAdapter(firebaseRecyclerAdapter);
        getInterests();
        return view;
    }

    public void getInterests() {
        mInterestsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> receiver = new ArrayList<>();
                String record = "";
                List<Interests> t = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    record = ds.getValue().toString();
                    receiver.add(record);
                    t.add(new Interests(record));
                }
                SimpleRecyclerAdapter<Interests, interestsBinder> adapter = new SimpleRecyclerAdapter<>(new interestsBinder());
                mResultList.setAdapter(adapter);
                adapter.setData(t);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        mThreadsDatabase = FirebaseDatabase.getInstance().getReference(THREADS);
//        mThreadsDatabase.keepSynced(true);
//        mThreadsDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<String> receiver = new ArrayList<>();
//                String record = "";
//                List<ChatThread> t = new ArrayList<>();
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    try {
//                        record = Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_0).getValue()) + " " + Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_1).getValue());
//                    } catch (Exception e) {
//                        Log.e("Record", "Exception");
//                    }
//                    if (record.contains(user.getUid())) {
//                        if (Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_0).getValue()).toString().equals(user.getUid())) {
//                            record = Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_1).getValue()).toString();
//                        } else {
//                            record = Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_0).getValue()).toString();
//                        }
//                        receiver.add(record);
//                        t.add(new ChatThread(record, ds.getKey()));
//                    }
//                }
//                SimpleRecyclerAdapter<ChatThread, UserBinder> adapter = new SimpleRecyclerAdapter<>(new UserBinder());
//                mResultList.setAdapter(adapter);
//                adapter.setData(t);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    public class interestsBinder extends ItemBinder<Interests, interestsBinder.InterestsViewHolder> {

        @Override
        public InterestsViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new InterestsViewHolder(inflater.inflate(R.layout.interest_item_layout, parent, false));
        }

        @Override
        public void bind(final InterestsViewHolder holder, final Interests item) {
            holder.interest_name.setText(item.getName());
            // holder.relativeLayout.setOnClickListener(view -> openChatActivity(item.getEmail(), item.getName(), item.getImageUrl(), item.getUid(), item.getThreadID()));
        }

        @Override
        public boolean canBindData(Object item) {
            return item instanceof Interests;
        }

        class InterestsViewHolder extends BaseViewHolder<Interests> {
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
