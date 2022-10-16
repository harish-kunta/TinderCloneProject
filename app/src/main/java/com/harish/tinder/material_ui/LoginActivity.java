package com.harish.tinder.material_ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.tinder.MainActivity;
import com.harish.tinder.R;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private Button sign_in, sign_up;
    private TextInputEditText uname, pword;
    private TextInputLayout usernameLayout,passwordLayout;

    private String username, password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        sign_in = (Button)findViewById(R.id.sign_in_button);
        sign_up = (Button)findViewById(R.id.sign_up_button);
        uname = (TextInputEditText)findViewById(R.id.username_field);
        pword = (TextInputEditText)findViewById(R.id.password_field);
        usernameLayout = (TextInputLayout)findViewById(R.id.username_field_input_layout);
        passwordLayout = (TextInputLayout)findViewById(R.id.password_field_input_layout);

        uname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    usernameLayout.setError("Please enter username");
                else
                    usernameLayout.setError(null);
            }
        });

        pword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    passwordLayout.setError("Please enter password");
                else
                    passwordLayout.setError(null);
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = uname.getText().toString();
                password = pword.getText().toString();

                if(username.trim().length() > 0 && password.trim().length() > 0){
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "sign in error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                    String uName = null, pWord = null;
//                    if (mSharedPreferences.contains("username")){
//                        uName = mSharedPreferences.getString("username","");
//                    }
//                    if (mSharedPreferences.contains("password")){
//                        pWord = mSharedPreferences.getString("password","");
//                    }
//
//
//                    if(username.equals(uName) && password.equals(pWord)){
//                        Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_LONG).show();
//                        Intent welcome = new Intent(getApplicationContext(), RegistrationActivity.class);
//                        startActivity(welcome);
//                    }
//                    else{
//                        Toast.makeText(getApplicationContext(), "Username/Password is incorrect"+uName+pWord, Toast.LENGTH_LONG).show();
//                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_LONG).show();

                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(signup);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

}