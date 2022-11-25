package com.harish.tinder.fragments;

import static com.harish.tinder.model.Constants.DEFAULT;
import static com.harish.tinder.model.Constants.EMAIL;
import static com.harish.tinder.model.Constants.INDEX_0;
import static com.harish.tinder.model.Constants.INDEX_1;
import static com.harish.tinder.model.Constants.MEMBERS;
import static com.harish.tinder.model.Constants.MESSAGES;
import static com.harish.tinder.model.Constants.NAME;
import static com.harish.tinder.model.Constants.ONLINE;
import static com.harish.tinder.model.Constants.PROFILE_IMAGE_URL;
import static com.harish.tinder.model.Constants.THREADS;
import static com.harish.tinder.model.Constants.UID;
import static com.harish.tinder.model.Constants.USERS;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.tinder.ChatActivity;
import com.harish.tinder.R;
import com.harish.tinder.model.ChatThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatThreadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mResultList;
    Context context;

    private DatabaseReference mThreadsDatabase;
    private DatabaseReference mUsersDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;


    public ChatThreadFragment() {
        // Required empty public constructor
    }

    public static ChatThreadFragment newInstance(String param1, String param2) {
        ChatThreadFragment fragment = new ChatThreadFragment();
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
        view = inflater.inflate(R.layout.fragment_chat_threads, container, false);
        mResultList = view.findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(context));
        mThreadsDatabase = FirebaseDatabase.getInstance().getReference(THREADS);
        mThreadsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference(USERS);
        mUsersDatabase.keepSynced(true);
        mResultList.setAdapter(firebaseRecyclerAdapter);
        getThreads();
        return view;
    }

    public void getThreads() {
        mThreadsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> receiver = new ArrayList<>();
                String record = "";
                List<ChatThread> t = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        record = Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_0).getValue()) + " " + Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_1).getValue());
                    } catch (Exception e) {
                        Log.e("Record", "Exception");
                    }
                    if (record.contains(user.getUid())) {
                        if (Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_0).getValue()).toString().equals(user.getUid())) {
                            record = Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_1).getValue()).toString();
                        } else {
                            record = Objects.requireNonNull(ds.child(MEMBERS).child(INDEX_0).getValue()).toString();
                        }
                        receiver.add(record);
                        t.add(new ChatThread(record, ds.getKey()));
                    }
                }
                SimpleRecyclerAdapter<ChatThread, UserBinder> adapter = new SimpleRecyclerAdapter<>(new UserBinder());
                mResultList.setAdapter(adapter);
                adapter.setData(t);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class UserBinder extends ItemBinder<ChatThread, UserBinder.UserViewHolder> {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private final DatabaseReference lastMessage = FirebaseDatabase.getInstance().getReference();
        private final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS);
        private String lastMessageValue;
        private String access;
        private String sender;

        @Override
        public UserViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new UserViewHolder(inflater.inflate(R.layout.thread_layout, parent, false));
        }

        @Override
        public void bind(final UserViewHolder holder, final ChatThread item) {
            try {
                Query nameQuery = mUsersDatabase;
                nameQuery.keepSynced(true);
                nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot each_user : dataSnapshot.getChildren()) {
                            if (Objects.requireNonNull(each_user.child(UID).getValue()).toString().equals(item.getUid())) {
                                holder.user_name.setText(Objects.requireNonNull(each_user.child(NAME).getValue()).toString());
                                item.setName(Objects.requireNonNull(each_user.child(NAME).getValue()).toString());
                                item.setEmail(Objects.requireNonNull(each_user.child(EMAIL).getValue()).toString());
                                item.setUid(Objects.requireNonNull(each_user.child(UID).getValue()).toString());
                                //TODO: change this to thumb_image
                                item.setImageUrl(Objects.requireNonNull(each_user.child(PROFILE_IMAGE_URL).getValue()).toString());
                                holder.setUserImage(Objects.requireNonNull(each_user.child(PROFILE_IMAGE_URL).getValue()).toString(), getContext());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } catch (Exception e) {
                //Log.e("Raise", "No email");
            }

            holder.profileStorageRef.child(item.getUid()).getDownloadUrl().addOnSuccessListener(uri -> System.out.println("Test"));

            holder.user_name.setOnClickListener(view -> openChatActivity(item.getEmail(), item.getName(), item.getImageUrl(), item.getUid(), item.getThreadID()));

            holder.relativeLayout.setOnClickListener(view -> openChatActivity(item.getEmail(), item.getName(), item.getImageUrl(), item.getUid(), item.getThreadID()));

            Query firebaseLastMessageQuery = lastMessage.child(MESSAGES).child(item.getThreadID());
            firebaseLastMessageQuery.keepSynced(true);
            lastMessage.keepSynced(true);
            firebaseLastMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        access = ds.child("-" + Objects.requireNonNull(user.getEmail()).replace(".", "")).getValue().toString();
                        sender = ds.child(NAME).getValue().toString();
                        lastMessageValue = ds.child("msg").getValue().toString();
                    }
                    if (lastMessageValue != null && access.equals("true")) {
                        if (!lastMessageValue.contains("https://firebasestorage.googleapis.com/")) {
                            if (sender.equals(user.getEmail()))
                                holder.lastMessagView.setText("You: " + lastMessageValue);
                            else holder.lastMessagView.setText(lastMessageValue);
                        } else {
                            if (sender.equals(user.getEmail()))
                                holder.lastMessagView.setText("You: Image");
                            else holder.lastMessagView.setText("Image");
                        }
                    } else {
                        holder.lastMessagView.setText("No messages");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Query userOnlineStatusQuery = users;
            userOnlineStatusQuery.keepSynced(true);
            userOnlineStatusQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot each_user : dataSnapshot.getChildren()) {
                        if (each_user.child(EMAIL).getValue().toString().equals(item.getEmail())) {
                            if ("true".equals(each_user.child(ONLINE).getValue())) {
                                holder.online.setVisibility(View.VISIBLE);
                            } else {
                                holder.online.setVisibility(View.GONE);
                            }
                            //Log.e("snapshot", item.getEmail());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public boolean canBindData(Object item) {
            return item instanceof ChatThread;
        }

        class UserViewHolder extends BaseViewHolder<ChatThread> {
            TextView user_name;
            TextView lastMessagView;
            CircleImageView profile_image;
            ImageView online;
            StorageReference profileStorageRef;
            RelativeLayout relativeLayout;

            public UserViewHolder(View itemView) {
                super(itemView);
                user_name = itemView.findViewById(R.id.email_text);
                profile_image = itemView.findViewById(R.id.profile_image);
                profileStorageRef = FirebaseStorage.getInstance().getReference();
                relativeLayout = itemView.findViewById(R.id.threadLayout);
                lastMessagView = itemView.findViewById(R.id.status_text);
                online = itemView.findViewById(R.id.online);

            }

            public void setUserImage(String thumb_image, Context ctx) {
                if (!thumb_image.equals(DEFAULT)) {
                    Glide.with(ctx).load(thumb_image).into(profile_image);
                } else {
                    profile_image.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_black_person_24dp));
                }
            }
        }

        public void openChatActivity(final String receiver_email, final String name, final String imageUrl, final String uid, final String threadID) {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("threadID", threadID);
            intent.putExtra("name", name);
            intent.putExtra("receiver_email", receiver_email);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
    }
}
