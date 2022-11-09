package com.harish.tinder.material_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.tinder.R;
import com.harish.tinder.UploadImageActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private TextInputEditText user_name;
    private TextInputEditText user_email;
    private TextInputEditText user_password;
    private TextInputEditText user_check_password;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private RadioGroup mRadioGroup;
    private ScrollView rootLayout;
    private ProgressDialog mRegProgress;

    private Button signup;

    private String userEmail, password, name, checkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getApplicationContext(), UploadImageActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };
        rootLayout = findViewById(R.id.root_layout);
        user_name = (TextInputEditText) findViewById(R.id.name_field);
        user_email = (TextInputEditText) findViewById(R.id.email_field);
        user_password = (TextInputEditText) findViewById(R.id.password_reg_field);
        user_check_password = (TextInputEditText) findViewById(R.id.confirm_pass_field);
        signup = (Button) findViewById(R.id.signup_reg_button);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        signup.setOnClickListener(v -> {
            int selectId = mRadioGroup.getCheckedRadioButtonId();
            name = user_name.getText().toString();
            userEmail = user_email.getText().toString();
            password = user_password.getText().toString();
            checkPassword = user_check_password.getText().toString();

            if (name.length() <= 0) {
                Snackbar.make(rootLayout, "Enter name", Snackbar.LENGTH_LONG).show();
            } else if (isValid(userEmail)) {
                Snackbar.make(rootLayout, "Enter valid email", Snackbar.LENGTH_LONG).show();
            } else if (selectId == -1) {
                Snackbar.make(rootLayout, "Gender not selected", Snackbar.LENGTH_LONG).show();
            } else if (password.length() <= 0) {
                Snackbar.make(rootLayout, "Enter password", Snackbar.LENGTH_LONG).show();
            } else if (!password.equals(checkPassword)) {
                Snackbar.make(rootLayout, "Passwords donot match", Snackbar.LENGTH_LONG).show();
            } else {
                final RadioButton radioButton = (RadioButton) findViewById(selectId);
                mRegProgress = new ProgressDialog(RegistrationActivity.this,
                        R.style.AppThemeDialog);
                mRegProgress.setIndeterminate(true);
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.setMessage("Creating Account...");
                mRegProgress.show();
                mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(RegistrationActivity.this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        Map userInfo = new HashMap<>();
                        userInfo.put("name", name);
                        userInfo.put("email", userEmail);
                        userInfo.put("sex", radioButton.getText().toString());
                        userInfo.put("online", true);
                        userInfo.put("profileImageUrl", "default");
                        userInfo.put("uid", userId);
                        currentUserDb.updateChildren(userInfo).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Snackbar.make(rootLayout, "Account Created successfully", Snackbar.LENGTH_LONG).show();
                                mRegProgress.dismiss();
                                Intent signin = new Intent(getApplicationContext(), LoginActivity.class);
                                signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(signin);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mRegProgress.dismiss();
                                Toast.makeText(getApplicationContext(), "Details were not saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mRegProgress.dismiss();
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Snackbar.make(rootLayout, "Password is too weak!", Snackbar.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Snackbar.make(rootLayout, "Invalid Credentials!", Snackbar.LENGTH_LONG).show();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Snackbar.make(rootLayout, "User with this email already exist!", Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Snackbar.make(rootLayout, "Authentication failed.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
