package com.harish.tinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harish.tinder.Chat.ChatView;
import com.harish.tinder.material_ui.MainActivity;
import com.harish.tinder.model.ChatMessage;
import com.harish.tinder.utils.ApiUtils;
import com.harish.tinder.utils.FirebaseMessage;
import com.harish.tinder.utils.MessageData;
import com.harish.tinder.utils.NotifyData;
import com.harish.tinder.views.ProfileView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

public class ChatActivity extends AppCompatActivity {

    private String threadID, chatUserId;
    private String receiver_email, receiver_name, imageUrl;
    private String chat_msg,chat_user_name, type;
    private String currparticipant;
    private long time;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference threadRef = FirebaseDatabase.getInstance().getReference().child("threads");
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference notifications = FirebaseDatabase.getInstance().getReference().child("notifications");
    private StorageReference mImageStorage;
    private String temp_key;
    private ArrayList <String> messageKey = new ArrayList<>();

    private ChatView chatView;
    private ImageButton imageMessage;
    private ImageView imageMessageView;
    private ListView chat_list;
    private TextView block_message;
    private Button block_button;
    private TextView dotsTextView;
    private final int PICK_IMAGE_REQUEST = 71;

    private String user_name, token;
    private CircleImageView toolbar_profile_icon;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Profile);
        setContentView(R.layout.activity_chat_2);
        Toolbar toolbar = findViewById(R.id.chat_view_toolbar);
        imageMessage = findViewById(R.id.pic_message);
        chat_list = findViewById(R.id.chat_list);
        block_button = findViewById(R.id.block_button);
        block_button.setVisibility(View.GONE);
        block_message = findViewById(R.id.block_message);
        block_button.setVisibility(View.GONE);
        imageMessageView = findViewById(R.id.image_message_view);
        dotsTextView = findViewById(R.id.typing);
        toolbar_profile_icon = (CircleImageView) findViewById(R.id.toolbar_profile_icon);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.inflateMenu(R.menu.menu_profile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        userRef.child(user.getUid()).child("online").setValue("true");
        threadID = getIntent().getExtras().get("threadID").toString();
        chatUserId = getIntent().getExtras().get("uid").toString();
        try{
            receiver_name = getIntent().getExtras().get("name").toString();
        }catch (Exception e){
            receiver_name = getIntent().getExtras().get("receiver_email").toString();
        }
        imageUrl = getIntent().getExtras().get("imageUrl").toString();
        receiver_email = getIntent().getExtras().get("receiver_email").toString();
        threadRef.child(threadID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.e("threadRefDS", ds.getValue().toString());
                    String user1 = user.getEmail().replace(".", "");
                    String user2 = receiver_email.replace(".","");
                    String gotUser = ds.getKey();
                    if(gotUser.equals(user1)){
                        if(ds.getValue().toString().equals("false")){
                            block_message.setVisibility(View.VISIBLE);
                            block_message.setText("You cannot message this user");
                            block_button.setText("Unblock");
                            block_button.setVisibility(View.VISIBLE);
                            chatView.setVisibility(View.GONE);
                        }else {
                            block_message.setVisibility(View.GONE);
                            block_button.setVisibility(View.GONE);
                            chatView.setVisibility(View.VISIBLE);
                        }
                    }else if(gotUser.equals(user2)){
                        if(ds.getValue().toString().equals("false")){
                            block_button.setVisibility(View.GONE);
                            block_message.setVisibility(View.VISIBLE);
                            block_message.setText("You cannot message this user");
                            chatView.setVisibility(View.GONE);
                        }else {
                            block_button.setVisibility(View.GONE);
                            chatView.setVisibility(View.VISIBLE);
                        }
                    }
                }
                Log.e("threadRef", dataSnapshot.getValue().toString());
                //String user1 = dataSnapshot.child(user.getEmail().replace(".","")).getValue().toString();
                /*String user2 = dataSnapshot.child(receiver_email.replace(".","")).getValue().toString();
                if(user2.equals("false")){
                    block_message.setVisibility(View.VISIBLE);
                    block_message.setText("You cannot message this user");
                    block_button.setText("Unblock");
                    block_button.setVisibility(View.VISIBLE);
                    chatView.setVisibility(View.INVISIBLE);
                }
                else if(user2.equals("false")){
                    block_button.setVisibility(View.GONE);
                    block_message.setVisibility(View.VISIBLE);
                    block_message.setText("You cannot message this user");
                    chatView.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        try {
            uri = Uri.parse(getIntent().getExtras().get("photo_url").toString());

        }catch (Exception e){
            Log.e("photo_url", "not supplied");
        }
        setTitle(receiver_name);
        Picasso.get().load(uri).into(toolbar_profile_icon);
        root = FirebaseDatabase.getInstance().getReference().child("messages").child(threadID);
        root.keepSynced(true);
        user_name = user.getEmail();
        //Log.e("threadID", threadID);
        chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {
                dotsTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void userStoppedTyping() {
                dotsTextView.setVisibility(View.GONE);
            }
        });

        userRef.child(chatUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online;
                try {
                    online = dataSnapshot.child("online").getValue().toString();
                }
                catch (Exception e)
                {
                    online="unknown";
                }
                if (dataSnapshot.hasChild("device_token")) {
                    token = dataSnapshot.child("device_token").getValue().toString();
                }
//                userName = dataSnapshot.child("name").getValue().toString();
//                mTitleView.setText(userName);
//                //Snackbar.make(rootLayout,token,Snackbar.LENGTH_LONG).show();
//                if (!image.equals("default")) {
//                    Glide
//                            .with(getApplicationContext())
//                            .load(image)
//                            .into(mProfileImage);
//                } else {
//                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(ChatOpenActivity.this, R.drawable.ic_account_circle_white_48dp));
//
//                }
//                if (online.equals("true")) {
//                    mLastSeenView.setText("Online");
//                }
//                else if(online.equals("unknown"))
//                {
//                    mLastSeenView.setText("");
//                }
//                else {
//                    GetTimeAgo getTimeAgo = new GetTimeAgo();
//
//                    long lastTime = Long.parseLong(online);
//
//                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());
//
//
//                    mLastSeenView.setText(lastSeenTime);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        registerForContextMenu(chat_list);
        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });
        block_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadRef.child(threadID).child(user.getEmail().replace(".", "")).setValue("true");
                finish();
                startActivity(getIntent());
            }
        });
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                Date date = new Date();
                DatabaseReference message_root = root.child(temp_key);
                message_root.keepSynced(true);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg", chatView.getTypedMessage());
                map2.put("time", date.getTime());
                map2.put("type", "text");
                map2.put("-"+user.getEmail().replace(".",""), "true");
                map2.put("-"+receiver_email.replace(".",""), "true");
                message_root.updateChildren(map2);
                chatView.getInputEditText().setText("");
                sendNotification(user.getEmail(), chatMessage.getMessage());
                return false;
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(final String title, final String body) {
        NotifyData notifydata = new NotifyData(title, body, "HANDLE_NOTIFICATION");
        MessageData messageData = new MessageData(user.getUid(), user_name, "message");
        FirebaseMessage firebaseMessage = new FirebaseMessage(token, notifydata, messageData);
        ApiUtils.sendNotificationService()
                .sendMessage(firebaseMessage)
                .enqueue(new retrofit2.Callback<FirebaseMessage>() {
                    @Override
                    public void onResponse(Call<FirebaseMessage> call, retrofit2.Response<FirebaseMessage> response) {
                        if (response.code() == 200) {
                            Log.d("One_login_call", "Message sent");
                        } else if (response.code() == 400) {

                        } else if (response.code() == 500) {
                            Log.d("One_login_call", "Server Error");
                            // Toast.makeText(controllerActivity, "Server Error", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("One_login_call", "SOT API call failed");
                            //Toast.makeText(controllerActivity, "SOT API call failed", Toast.LENGTH_SHORT).show();

                        }
                        Log.d("One_login_Response", response.toString());
                        //closeProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<FirebaseMessage> call, Throwable throwable) {

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_block) {
            threadRef.child(threadID).child(user.getEmail().replace(".", "")).setValue("false");
            finish();
            startActivity(getIntent());
            return true;
        }
        else if(id == R.id.view_profile){
            Intent intent = new Intent(ChatActivity.this, ProfileView.class);
            intent.putExtra("name", receiver_name);
            intent.putExtra("email", receiver_email);
            intent.putExtra("photo_url", imageUrl);
            intent.putExtra("thread", threadID);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStop() {
        super.onStop();
        messageKey.clear();

    }
    @Override
    protected void onPause() {
        super.onPause();
        userRef.child(user.getUid()).child("online").setValue("false");
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.chat_list){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo)info).position;

        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("messages")
                .child(threadID).child(messageKey.get(position));
        switch (item.getItemId()){
            case R.id.chat_delete:
                //Log.e("Delete", "Selected" + messageKey.get(position));

                //Log.e("Delete", "Selected "+position);
                deleteRef.child("-"+user.getEmail().replace(".","")).setValue("false");
                chatView.removeMessage(position);
                messageKey.remove(position);
                //Log.e("After Delete", messageKey.toString() + " Size " + messageKey.size());

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            Uri uri = data.getData();
            //Log.e("Image Uri", uri.toString());
            temp_key = root.push().getKey();
            StorageReference filepath = mImageStorage.child("image_messages").child( temp_key + ".jpg");
            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference();
                        profileStorageRef.child("image_messages/"+temp_key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download = uri.toString();
                                //Log.e("Uri", download);
                                Map<String,Object> map = new HashMap<String, Object>();
                                root.updateChildren(map);
                                Date date = new Date();
                                DatabaseReference message_root = root.child(temp_key);
                                message_root.keepSynced(true);
                                Map<String,Object> map2 = new HashMap<String, Object>();
                                map2.put("name",user_name);
                                map2.put("msg", download);
                                map2.put("time", date.getTime());
                                map2.put("type", "image");
                                map2.put("-"+user.getEmail().replace(".",""), "true");
                                map2.put("-"+receiver_email.replace(".",""), "true");
                                message_root.updateChildren(map2);
                                chatView.getInputEditText().setText("");
                            }
                        });

                    }
                }
            });

        }
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        //Log.e("message key->", messageKey.toString());
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            DataSnapshot part1, part2;
            String access;
            part1 = ((DataSnapshot)i.next());
            part2 = ((DataSnapshot)i.next());
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            time = (long)((DataSnapshot)i.next()).getValue();
            type = (String)((DataSnapshot)i.next()).getValue();

            if(part1.getKey().equals("-"+user.getEmail().replace(".", ""))){
                access = part1.getValue().toString();
            }else {
                access = part2.getValue().toString();
            }

            String data = "{\n\tchat_msg: " + chat_msg
                    + "\n\tchat_user_name: " + chat_user_name
                    + "\n\ttime: "  + time
                    + "\n\ttype: "  + type
                    + "\n\taccess: "+ access
                    +"\n}";

            //Log.e("Data", data);

            //chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
            if(type.equals("text")){
                if(access.equals("true")){
                    messageKey.add(key);
                    if(user_name.equals(chat_user_name)){
                        ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.SENT);
                        chatView.addMessage(message);

                    }else {
                        ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.RECEIVED,uri);
                        chatView.addMessage(message);
                    }
                }
            }
            else if(type.equals("image")){
                Uri imageMessage = Uri.parse(chat_msg);
                //Log.e("Chat Image", chat_msg);
                if(access.equals("true")) {
                    messageKey.add(key);
                    if (user_name.equals(chat_user_name)) {
                        ChatMessage message = new ChatMessage(imageMessage, time, ChatMessage.Type.SENT);
                        chatView.addMessage(message);

                    } else {
                        ChatMessage message = new ChatMessage(imageMessage, time, ChatMessage.Type.RECEIVED, uri);
                        chatView.addMessage(message);
                    }
                }
            }

        }

    }
    public void onBackPressed() {
        finish();
    }
}
