package com.harish.tinder;

import static com.harish.tinder.model.Constants.CONNECTIONS;
import static com.harish.tinder.model.Constants.MATCHES;
import static com.harish.tinder.model.Constants.USERS;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.alert.SweetAlertDialog;
import com.harish.tinder.material_ui.MainActivity;
import com.harish.tinder.model.Constants;
import com.harish.tinder.model.FirebaseDbUser;
import com.harish.tinder.utils.MyData;

import java.util.HashMap;
import java.util.Map;


public class UserProfileActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private FloatingActionButton mCloseProfile;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFavouriteDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private MyData myData;
    public String token;
    FirebaseDbUser firebaseDbUser;

    private Button unMatchUserButton;

    private FirebaseUser mCurrentUser;
    String image;
    String display_name;
    String status;
    CoordinatorLayout rootLayout;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    CollapsingToolbarLayout ctl;
    String user_id;
    private Menu profileImageMenu;
    private boolean downloadState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_material_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ctl = findViewById(R.id.toolbar_layout);


        myData = new MyData();
        downloadState = false;
        Intent intent = this.getIntent();

        /* Obtain String from Intent  */
        if (intent != null) {
            user_id = intent.getStringExtra("user_id");
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(user_id);
        mFavouriteDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendDatabase.keepSynced(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(user_id);
        mAuth = FirebaseAuth.getInstance();

        mUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(mAuth.getCurrentUser().getUid());

        if (!myData.isInternetConnected(getApplicationContext())) {
            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }
        rootLayout = findViewById(R.id.rootlayout);
        mProfileImage = findViewById(R.id.user_profile_image);
        mCloseProfile = findViewById(R.id.close_profile);
        unMatchUserButton = findViewById(R.id.unmatch_user_button);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseDbUser = dataSnapshot.getValue(FirebaseDbUser.class);
                if (firebaseDbUser.getName() != null) {
                    display_name = firebaseDbUser.getName();
                }
                if (firebaseDbUser.getStatus() != null) {
                    status = firebaseDbUser.getStatus();
                } else {
                    status = "";
                }
                if (firebaseDbUser.getDevice_token() != null) {
                    token = firebaseDbUser.getDevice_token();
                }
                if (firebaseDbUser.getProfileImageUrl() != null) {
                    image = firebaseDbUser.getProfileImageUrl();
                }
                ctl.setTitle(display_name);
//                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
//                myRef.keepSynced(true);
//                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        // textView2.setText(dataSnapshot.getChildrenCount()+"");
//                        noOfFriends.setText("testing");
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                //mProfileName.setText(display_name);
                if (!image.equals(Constants.DEFAULT)) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_close_drawable)
                            .error(R.drawable.ic_close_drawable)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform();
                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .apply(options)
                            .into(mProfileImage);
                } else {
                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.profile));

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mUserRef.child(CONNECTIONS).child(MATCHES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        if(match.getKey().equals(user_id)){
                            unMatchUserButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        unMatchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("UnMatch user")
                        .setContentText("Are you sure you want to unmatch " + firebaseDbUser.getName())
                        .setCancelText("Cancel")
                        .setConfirmText("unmatch")
                        .showCancelButton(true)
                        .setCancelClickListener(SweetAlertDialog::cancel)
                        .setConfirmClickListener(this::unMatchUser)
                        .show();
            }

            private void unMatchUser(SweetAlertDialog sweetAlertDialog) {
                mUsersDatabase.child(Constants.CONNECTIONS).child(Constants.MATCHES).child(mCurrentUser.getUid()).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                mUserRef.child(Constants.CONNECTIONS).child(Constants.MATCHES).child(user_id).getRef().removeValue();
                                sweetAlertDialog.setTitleText("User unmatched!")
                                        .setContentText("User was removed successfully!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                cancelledDialog(sweetAlertDialog, "Problem", e.getMessage(), "OK");
                            }
                        });
            }
        });

        mFriendDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {

                    // Toast.makeText(ProfileImageActivity.this, "Both are Friends", Toast.LENGTH_SHORT).show();
                    //downloadState = 1; // setting state
                    //profileImageMenu.getMenu().removeItem(R.id.download_image);
                    //invalidateOptionsMenu();
                    //getMenu().removeItem(R.id.item_name);
                    downloadState = true;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mCloseProfile.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();

        } else {
            mUserRef.child(Constants.ONLINE).setValue("true");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child(Constants.ONLINE).setValue("true");
        }

    }

    private void cancelledDialog(SweetAlertDialog sDialog, String title, String contentText, String confirmText) {
        // reuse previous dialog instance, keep widget user state, reset them if you need
        sDialog.setTitleText(title)
                .setContentText(contentText)
                .setConfirmText(confirmText)
                .showCancelButton(false)
                .setCancelClickListener(null)
                .setConfirmClickListener(null)
                .showForgotPassword(false)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    private void sendToStart() {
        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(image);
        }
    }

    public void downloadFile(String image) {
        try {

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(image);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle(display_name)
                    .setDescription("Ping Me Files")
                    .setDestinationInExternalPublicDir("/PingMeFiles", display_name + ".jpg");

            mgr.enqueue(request);

            // Open Download Manager to view File progress
            Snackbar.make(rootLayout, "Downloading...", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(rootLayout, "Error in downloading Image", Snackbar.LENGTH_LONG).show();
        }
    }

//    private void sendNotification(final String title, final String body) {
//        NotifyData notifydata = new NotifyData(title, body, "HANDLE_REQUEST");
//        MessageData messageData = new MessageData(mCurrentUser.getUid(), display_name, "request");
//        FirebaseMessage firebaseMessage = new FirebaseMessage(token, notifydata, messageData);
//        ApiUtils.sendNotificationService()
//                .sendMessage(firebaseMessage)
//                .enqueue(new retrofit2.Callback<FirebaseMessage>() {
//                    @Override
//                    public void onResponse(Call<FirebaseMessage> call, retrofit2.Response<FirebaseMessage> response) {
//                        if (response.code() == 200) {
//                            addtoFirebase(title, body, user_id);
//
//                        } else if (response.code() == 400) {
//
//                        } else if (response.code() == 500) {
//                            Log.d("One_login_call", "Server Error");
//                            // Toast.makeText(controllerActivity, "Server Error", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            Log.d("One_login_call", "SOT API call failed");
//                            //Toast.makeText(controllerActivity, "SOT API call failed", Toast.LENGTH_SHORT).show();
//
//                        }
//                        Log.d("One_login_Response", response.toString());
//                        //closeProgressDialog();
//                    }
//
//                    @Override
//                    public void onFailure(Call<FirebaseMessage> call, Throwable throwable) {
//
//                    }
//                });
//
//    }

    private void addtoFirebase(String title, String body, String user_id) {
        DatabaseReference user_message_push = mNotificationDatabase.push();
        String push_id = user_message_push.getKey();
        HashMap notificationData = new HashMap();
        notificationData.put("title", title);
        notificationData.put("body", body);
        notificationData.put("timestamp", ServerValue.TIMESTAMP);
        notificationData.put("from_user", mCurrentUser.getUid());
        notificationData.put("to_user", user_id);

        Map notificationDataMap = new HashMap();
        notificationDataMap.put(push_id, notificationData);

//        Map requestMap = new HashMap();
//        requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
//        requestMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
        // requestMap.put("notifications/" + user_id + "/" + newnotificationId, notificationData);
        mNotificationDatabase.updateChildren(notificationDataMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {

                    Snackbar.make(rootLayout, "There was some error in sending request", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_image_menu, menu);
        profileImageMenu = menu;
        if (!downloadState) {
            if (profileImageMenu != null) {
                profileImageMenu.findItem(R.id.download_image)
                        .setVisible(false);
                profileImageMenu.findItem(R.id.download_image)
                        .setEnabled(false);
            }
        } else {
            if (profileImageMenu != null) {
                profileImageMenu.findItem(R.id.download_image)
                        .setVisible(true);
                profileImageMenu.findItem(R.id.download_image)
                        .setEnabled(true);
            }
        }
        return true;

    }
}
