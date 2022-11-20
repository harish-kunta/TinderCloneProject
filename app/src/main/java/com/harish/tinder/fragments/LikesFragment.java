package com.harish.tinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.Matches.MatchesObject;
import com.harish.tinder.R;
import com.harish.tinder.adapter.LikesAdapter;
import com.harish.tinder.model.CourseCard;
import com.harish.tinder.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mLikesAdapter;
    private RecyclerView.LayoutManager mLikesLayoutManager;

    private String currentUserID;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mLikesView;
    private ArrayList<CourseCard> courseCards;

    public LikesFragment() {
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
    public static LikesFragment newInstance(String param1, String param2) {
        LikesFragment fragment = new LikesFragment();
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

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue()!=null){
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }


                    MatchesObject obj = new MatchesObject(userId, name, profileImageUrl);
                    resultsMatches.add(obj);
                    mLikesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mLikesView = inflater.inflate(R.layout.fragment_courses_stagged, container, false);
        Context mcontext = mLikesView.getContext();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mLikesAdapter = new LikesAdapter(getDataSetMatches(), mcontext);
        getUserMatchId();

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView = mLikesView.findViewById(R.id.rv_courses);
        mRecyclerView.setLayoutManager(
                layoutManager
        );
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setHasFixedSize(true);


        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.horizontal_card);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));

        mRecyclerView.setAdapter(mLikesAdapter);

        return mLikesView;
    }
}