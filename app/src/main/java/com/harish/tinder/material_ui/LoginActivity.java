package com.harish.tinder.material_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.tinder.R;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private Button sign_in, sign_up;
    private TextView forgotPassword;
    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout emailLayout,passwordLayout;
    private LinearLayout rootLayout;
    private String email, password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user !=null){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        sign_in = (Button)findViewById(R.id.sign_in_button);
        sign_up = (Button)findViewById(R.id.sign_up_button);
        editTextEmail = (TextInputEditText)findViewById(R.id.email_field);
        editTextPassword = (TextInputEditText)findViewById(R.id.password_field);
        emailLayout = (TextInputLayout)findViewById(R.id.username_field_input_layout);
        passwordLayout = (TextInputLayout)findViewById(R.id.password_field_input_layout);
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    emailLayout.setError("Please enter username");
                else
                    emailLayout.setError(null);
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
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

        //TODO : forgot password link
        /*forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "you can reset your password now", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });*/

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if(email.trim().length() > 0 && password.trim().length() > 0){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Snackbar.make(rootLayout, "Incorrect username or password", Snackbar.LENGTH_LONG).show();
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
                } else if (email.length() <= 0) {
                Snackbar.make(rootLayout, "Enter username", Snackbar.LENGTH_LONG).show();
            } else if (password.length() <= 0) {
                Snackbar.make(rootLayout, "Enter password", Snackbar.LENGTH_LONG).show();
            }else{
                    Snackbar.make(rootLayout, "Please enter username and password", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        

        sign_up.setOnClickListener(v -> {
            Intent signup = new Intent(getApplicationContext(), RegistrationActivity.class);
            startActivity(signup);
            finish();
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