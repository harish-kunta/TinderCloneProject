package com.harish.tinder.material_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.tinder.MainActivity;
import com.harish.tinder.R;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText user_name;
    private TextInputEditText user_username;
    private TextInputEditText user_password;
    private TextInputEditText user_check_password;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private RadioGroup mRadioGroup;

    private Button signup;

    private String username, password,name, checkPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };

        user_name = (TextInputEditText)findViewById(R.id.name_field);
        user_username = (TextInputEditText)findViewById(R.id.username_reg_field);
        user_check_password = (TextInputEditText)findViewById(R.id.password_reg_field);
        user_password = (TextInputEditText)findViewById(R.id.confirm_pass_field);
        signup = (Button)findViewById(R.id.signup_reg_button);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        signup.setOnClickListener(v -> {
            int selectId = mRadioGroup.getCheckedRadioButtonId();

            final RadioButton radioButton = (RadioButton) findViewById(selectId);

            if (radioButton.getText() == null) {
                return;
            }

            name = user_name.getText().toString();
            username = user_username.getText().toString();
            password = user_password.getText().toString();
            checkPassword = user_check_password.getText().toString();

            if(username.length()<=0){
                Toast.makeText(getApplicationContext(), "Enter name", Toast.LENGTH_SHORT).show();
            }
            else if( password.length()<=0){
                Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
            }else if(!password.equals(checkPassword)){
                Toast.makeText(getApplicationContext(), "Passwords donot match", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(RegistrationActivity.this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        Map userInfo = new HashMap<>();
                        userInfo.put("name", name);
                        userInfo.put("email", username);
                        userInfo.put("sex", radioButton.getText().toString());
                        userInfo.put("online", true);
                        userInfo.put("profileImageUrl", "default");
                        userInfo.put("uid", userId);
                        currentUserDb.updateChildren(userInfo);
                    } else {
                        Toast.makeText(getApplicationContext(), "sign up error", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(getApplicationContext(), "Saved!!", Toast.LENGTH_LONG).show();
                Intent signin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(signin);
                finish();
            }
        });
    }
}
