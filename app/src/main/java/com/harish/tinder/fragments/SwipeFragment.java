package com.harish.tinder.fragments;

import static com.harish.tinder.model.FirebaseConstants.CHAT;
import static com.harish.tinder.model.FirebaseConstants.CHAT_ID;
import static com.harish.tinder.model.FirebaseConstants.CONNECTIONS;
import static com.harish.tinder.model.FirebaseConstants.DOB;
import static com.harish.tinder.model.FirebaseConstants.FEMALE;
import static com.harish.tinder.model.FirebaseConstants.MALE;
import static com.harish.tinder.model.FirebaseConstants.MATCHES;
import static com.harish.tinder.model.FirebaseConstants.MEMBERS;
import static com.harish.tinder.model.FirebaseConstants.NAME;
import static com.harish.tinder.model.FirebaseConstants.NOPE;
import static com.harish.tinder.model.FirebaseConstants.PROFILE_IMAGE_URL;
import static com.harish.tinder.model.FirebaseConstants.SEX;
import static com.harish.tinder.model.FirebaseConstants.THREADS;
import static com.harish.tinder.model.FirebaseConstants.USERS;
import static com.harish.tinder.model.FirebaseConstants.USER_ID;
import static com.harish.tinder.model.FirebaseConstants.YEPS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.harish.tinder.R;
import com.harish.tinder.material_ui.UserProfileActivity;
import com.harish.tinder.adapter.ProfileAdapter;
import com.harish.tinder.model.FirebaseConstants;
import com.harish.tinder.model.Profile;
import com.harish.tinder.utils.AgeCalculator;
import com.harish.tinder.utils.ProgressDialogHelper;
import com.harish.tinder.utils.StringResourceHelper;
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
import java.util.Objects;

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
        progressDialog = ProgressDialogHelper.getProgressDialog(getContext());
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
        progressDialog.show();
        getFirebaseResponseData();
        return mHomeView;
    }

    // set up buttons actions
    private void setupButton(View homeView) {
        // skip button
        FloatingActionButton skip = homeView.findViewById(R.id.skip_button);
        skip.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });
        // rewind  button
        FloatingActionButton rewind = homeView.findViewById(R.id.rewind_button);
        rewind.setOnClickListener(v -> {
            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            layoutManager.setRewindAnimationSetting(setting);
            cardStackView.rewind();

        });
        // like button
        FloatingActionButton like = homeView.findViewById(R.id.like_button);
        like.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });
        FloatingActionButton chat = homeView.findViewById(R.id.info_button);
        chat.setOnClickListener(v -> {
            Profile profile = currentProfile;
            String userId = profile.getId();
            Intent intent = new Intent(getContext(), UserProfileActivity.class);
            intent.putExtra(USER_ID, userId);
            startActivity(intent);
        });

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

    private void getFirebaseResponseData() {
        usersDb = FirebaseDatabase.getInstance().getReference().child(USERS);

        mAuth = FirebaseAuth.getInstance();
        currentUId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        checkUserSex();

        profileList = new ArrayList<>();

        profileAdapter = new ProfileAdapter(getContext(), profileList);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        cardStackView.setAdapter(profileAdapter);

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child(CONNECTIONS).child(YEPS).child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), StringResourceHelper.getString(getContext(), R.string.new_connection), Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child(CHAT).push().getKey();

                    usersDb.child(Objects.requireNonNull(dataSnapshot.getKey())).child(CONNECTIONS).child(MATCHES).child(currentUId).child(CHAT_ID).setValue(key);
                    usersDb.child(currentUId).child(CONNECTIONS).child(MATCHES).child(dataSnapshot.getKey()).child(CHAT_ID).setValue(key);

                    appendThread(currentUId, userId, key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void appendThread(String currentUserId, String MatchUserId, String uniqueID) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> members = new ArrayList<>();
        members.add(MatchUserId);
        members.add(currentUserId);
        root = root.child(THREADS);
        root.keepSynced(true);
        root.child(uniqueID).child(MEMBERS).setValue(members);
    }

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(SEX).getValue() != null) {
                        userSex = Objects.requireNonNull(dataSnapshot.child(SEX).getValue()).toString();
                        switch (userSex) {
                            case MALE:
                                oppositeUserSex = FEMALE;
                                break;
                            case FEMALE:
                                oppositeUserSex = MALE;
                                break;
                        }
                        getOppositeSexUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexUsers() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child(SEX).getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child(CONNECTIONS).child(NOPE).hasChild(currentUId) && !dataSnapshot.child(CONNECTIONS).child(YEPS).hasChild(currentUId) && Objects.requireNonNull(dataSnapshot.child(SEX).getValue()).toString().equals(oppositeUserSex)) {
                        String profileImageUrl = FirebaseConstants.DEFAULT;
                        if (!Objects.equals(dataSnapshot.child(PROFILE_IMAGE_URL).getValue(), FirebaseConstants.DEFAULT)) {
                            profileImageUrl = Objects.requireNonNull(dataSnapshot.child(PROFILE_IMAGE_URL).getValue()).toString();
                        }
                        //TODO: remove this annotation
                        //TODO : set distance dynamically
                        Profile profile = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            profile = new Profile(dataSnapshot.getKey(), Objects.requireNonNull(dataSnapshot.child(NAME).getValue()).toString(), AgeCalculator.calculateAge(getObjectLong(dataSnapshot.child(DOB).getValue())), profileImageUrl, 20);
                        }
                        profileList.add(profile);
                        profileAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction.equals(Direction.Right)) {
            Profile profile = currentProfile;
            String userId = profile.getId();
            usersDb.child(userId).child(CONNECTIONS).child(YEPS).child(currentUId).setValue("true");
            isConnectionMatch(userId);
        } else {
            Profile profile = currentProfile;
            String userId = profile.getId();
            usersDb.child(userId).child(CONNECTIONS).child(NOPE).child(currentUId).setValue("true");
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

    long getObjectLong(Object o) {
        if (o != null) {
            return (Long) o;
        }
        return -1;
    }
}