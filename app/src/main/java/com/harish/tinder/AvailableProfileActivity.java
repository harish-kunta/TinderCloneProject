package com.harish.tinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.tinder.adapter.AvailableProfileAdapter;
import com.harish.tinder.adapter.ProfileAdapter;
import com.harish.tinder.custom.CustomDialog;
import com.harish.tinder.model.AvailableProfile;
import com.harish.tinder.model.Profile;
import com.harish.tinder.web_services.ProfileAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AvailableProfileActivity extends AppCompatActivity {

    AvailableProfileAdapter adapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    List<Object> listData;
    private DatabaseReference usersDb;
    private FirebaseAuth mAuth;
    private String currentUId;
    List<AvailableProfile> availableProfiles;

    private String oppositeUserSex;
    private String userSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_profile);
        recyclerView=findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
       // getWebServiceResponseData();
        getFirebaseResponseData();
        setUpProgressDialog();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    /*
    private void addProfilesData() {
        listData.addAll(new ProfileDAO().createAvailableProfiles(this));
    }



     */

    // call the api to get profiles data
    private void getWebServiceResponseData() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(ProfileAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ProfileAPI profileAPI=retrofit.create(ProfileAPI.class);
        Call<List<Profile>> call=profileAPI.getProfiles();
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {

                List<Profile> profileList=new ArrayList<>(response.body());
                Resources res = getApplicationContext().getResources();
                List<AvailableProfile> availableProfiles=new ArrayList<>();
                String[] textArray = res.getStringArray(R.array.text_array);
                for (int i=0;i<profileList.size();i++) {
                    Profile profile=getRandomProfile(profileList);
                    if(profile!=null) {
                        availableProfiles.add(new AvailableProfile(profile,textArray[i]));
                    }
                }
                adapter=new AvailableProfileAdapter(AvailableProfileActivity.this,availableProfiles);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                recyclerView.setAdapter(adapter);

            }


            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.d("Failure",t.toString());

            }
        });
    }

    private void getFirebaseResponseData() {
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        availableProfiles=new ArrayList<>();
        Resources res = getApplicationContext().getResources();
        String[] textArray = res.getStringArray(R.array.text_array);
        checkUserSex();
        //arrayAdapter = new arrayAdapter(getContext(), R.layout.item, rowItems);
        adapter = new AvailableProfileAdapter(getApplicationContext(), availableProfiles);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        recyclerView.setAdapter(adapter);
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
                        Profile profile=getRandomProfile(profileList);
                        if(profile!=null) {
                            availableProfiles.add(new AvailableProfile(profile,textArray[i]));
                        }
                        adapter.notifyDataSetChanged();
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


    private Profile getRandomProfile(List<Profile> profiles) {
        Random random = new Random();
        int index = random.nextInt(profiles.size());
        return profiles.get(index);
    }
    // set up progress dialog
    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(AvailableProfileActivity.this);
        progressDialog.setMessage("Fetching profiles data");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CustomDialog customDialog=new CustomDialog();
                customDialog.createDialog(this);
                customDialog.buttonRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }

                });
                customDialog.buttonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.alertDialog.dismiss();
                        NavUtils.navigateUpFromSameTask(getParent());

                    }
                });
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}