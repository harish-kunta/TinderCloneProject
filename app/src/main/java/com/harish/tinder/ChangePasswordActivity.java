package com.harish.tinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.tinder.material_ui.EditProfileActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChangedPwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");

        editTextPwdNew = findViewById(R.id.editText_change_new_pwd);
        editTextPwdCurr = findViewById(R.id.editText_change_current_pwd);
        editTextPwdConfirmNew = findViewById(R.id.editText_change_new_confirm_pwd);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangedPwd = findViewById(R.id.button_change_pwd);
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangedPwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "something went wrong!user's details not available",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
            startActivity(intent);
            finish();
        }else{
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextPwdCurr.getText().toString();
                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this, "password is needed", Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirmNew.setEnabled(true);
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangedPwd.setEnabled(true);
                                textViewAuthenticated.setText("you are authenticated/verified" + "you can change password now!");
                                Toast.makeText(ChangePasswordActivity.this, "password has been verified" + "change password now", Toast.LENGTH_SHORT).show();

                                buttonChangedPwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }else{
                                try{
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdNew.getText().toString();
        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this, "new password is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("please enter your new password");
            editTextPwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "please confirm your new password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("please re-enter your new password");
            editTextPwdConfirmNew.requestFocus();
        }else if(!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("please re-enter same password");
            editTextPwdConfirmNew.requestFocus();
        }else if(userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "new password cannot be same as old password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("please enter a new  password");
            editTextPwdNew.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "password has been changed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        try{
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }



}
