package com.harish.tinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.tinder.fab.FloatingActionButton;
import com.harish.tinder.material_ui.LoginActivity;
import com.harish.tinder.material_ui.MainActivity;
import com.harish.tinder.utils.Imageutils;
import com.harish.tinder.views.ProfileEdit;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView profileName;
    TextView profileEmail;
    Imageutils imageutils;
    TextView logOut;
    CircleImageView profilePic;
    FirebaseStorage storage;
    RelativeLayout layout_logout;
    StorageReference storageReference;
    private Uri filePath;
    private Bitmap bitmap;
    private String file_name;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Profile);
        setContentView(R.layout.activity_profile);

        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.tvNumber3);
        logOut = (TextView) findViewById(R.id.tvNumber7);
        layout_logout = (RelativeLayout) findViewById(R.id.layout_logout);
        profilePic = (CircleImageView) findViewById(R.id.profile_image);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        storageReference = FirebaseStorage.getInstance().getReference();
        userRef.child(user.getUid()).child("online").setValue("true");
        profileName.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());

        //Log.e("Photo URL", filePath.toString());
        Picasso.get().load(user.getPhotoUrl()).into(profilePic);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(ProfileActivity.this, ProfileEdit.class);
                startActivity(intent);
                finish();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.child(user.getUid()).child("online").setValue("false");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRef.child(user.getUid()).child("online").setValue("false");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
