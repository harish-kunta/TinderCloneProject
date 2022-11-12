package com.harish.tinder.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.AvailableProfileActivity;
import com.harish.tinder.R;
import com.harish.tinder.adapter.ProfileAdapter;
import com.harish.tinder.model.Profile;
import com.harish.tinder.web_services.ProfileAPI;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;
import com.yuyakaido.android.cardstackview.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SwipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SwipeFragment extends Fragment implements CardStackListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mHomeView;
    private FirebaseAuth mAuth;
    private String userSex;
    private String oppositeUserSex;

    private String currentUId;

    private ProfileAdapter profileAdapter;
    private CardStackLayoutManager layoutManager;
    private CardStackView cardStackView;
    private ProgressDialog progressDialog;

    private DatabaseReference usersDb;
    List<Profile> profileList;
    Profile currentProfile;

    public SwipeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SwipeFragment newInstance(String param1, String param2) {
        SwipeFragment fragment = new SwipeFragment();
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
        mHomeView = inflater.inflate(R.layout.fragment_home, container, false);
        cardStackView = mHomeView.findViewById(R.id.card_stack_view);
        layoutManager = new CardStackLayoutManager(getContext(), this);
        setUpCardStack();
        setupButton(mHomeView);
        setUpProgressDialog();
        getFirebaseResponseData();
        return mHomeView;
    }


    // set up buttons actions
    private void setupButton(View homeView) {
        // skip button
        FloatingActionButton skip = homeView.findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                layoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });
        // rewind  button
        FloatingActionButton rewind = homeView.findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                layoutManager.setRewindAnimationSetting(setting);
                cardStackView.rewind();

            }
        });
        // like button
        FloatingActionButton like = homeView.findViewById(R.id.like_button);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                layoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }

        });
//        FloatingActionButton chat = homeView.findViewById(R.id.chat_button);
//        chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), AvailableProfileActivity.class));
//            }
//        });

    }

    // set up progress dialog
    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching profiles data");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    // set Up cardStack animation
    private void setUpCardStack() {
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8.0f);
        layoutManager.setScaleInterval(0.95f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(20.0f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollHorizontal(true);
        layoutManager.setCanScrollVertical(true);
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        layoutManager.setOverlayInterpolator(new LinearInterpolator());
        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    // call the api to get profiles data
    private void getWebServiceResponseData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ProfileAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ProfileAPI profileAPI = retrofit.create(ProfileAPI.class);
        Call<List<Profile>> call = profileAPI.getProfiles();
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {

                List<Profile> profileList = new ArrayList<>(response.body());
                Log.d("Profiles", "" + profileList);
                profileAdapter = new ProfileAdapter(getContext(), profileList);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                cardStackView.setAdapter(profileAdapter);

            }


            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.d("Failure", t.toString());

            }
        });
    }

    private void getFirebaseResponseData() {
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        profileList = new ArrayList<Profile>();

        //arrayAdapter = new arrayAdapter(getContext(), R.layout.item, rowItems);
        profileAdapter = new ProfileAdapter(getContext(), profileList);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        cardStackView.setAdapter(profileAdapter);

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    appendThread(currentUId, userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void appendThread(String currentUserId, String MatchUserId) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> members = new ArrayList<>();
        members.add(MatchUserId);
        members.add(currentUserId);
        root = root.child("threads");
        root.keepSynced(true);
        String uniqueID = UUID.randomUUID().toString();
        root.child(uniqueID).child("members").setValue(members);
    }
    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sex").getValue() != null) {
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        switch (userSex) {
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            case "Female":
                                oppositeUserSex = "Male";
                                break;
                        }
                        getOppositeSexUsers();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexUsers() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("sex").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)) {
                        String profileImageUrl = "default";
                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        }
                        Profile profile = new Profile(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), 26, profileImageUrl, 20);
                        profileList.add(profile);
                        profileAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction.equals(Direction.Right)) {

            Profile profile = (Profile) currentProfile;
            String userId = profile.getId();
            usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
            isConnectionMatch(userId);
            Toast.makeText(getContext(), "Right", Toast.LENGTH_SHORT).show();
        } else {
            Profile profile = (Profile) currentProfile;
            String userId = profile.getId();
            usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            Toast.makeText(getContext(), "Left", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
       currentProfile = profileList.get(position);
    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}