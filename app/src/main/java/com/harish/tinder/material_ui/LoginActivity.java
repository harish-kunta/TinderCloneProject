package com.harish.tinder.material_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.harish.tinder.ChangePasswordActivity;
import com.harish.tinder.ForgotPasswordActivity;
import com.harish.tinder.R;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.harish.tinder.alert.SweetAlertDialog;
import com.harish.tinder.utils.StringResourceHelper;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private LinearLayout rootLayout;
    private String email, password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        rootLayout = findViewById(R.id.root_layout);
        Button sign_in = findViewById(R.id.sign_in_button);
        Button sign_up = findViewById(R.id.sign_up_button);
        editTextEmail = findViewById(R.id.email_field);
        editTextPassword = findViewById(R.id.password_field);
        emailLayout = findViewById(R.id.username_field_input_layout);
        passwordLayout = findViewById(R.id.password_field_input_layout);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        context = getApplicationContext();
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    emailLayout.setError(StringResourceHelper.getString(context, R.string.enter_user_name));
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
                if (s.length() == 0)
                    passwordLayout.setError(StringResourceHelper.getString(context, R.string.enter_password));
                else
                    passwordLayout.setError(null);
            }
        });

        forgotPassword.setOnClickListener(view -> {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Enter your email id")
                    .setContentText("You will receive an email to reset your password")
                    .setCancelText("Cancel")
                    .setConfirmText("Send email")
                    .showForgotPassword(true)
                    .showCancelButton(true)
                    .setCancelClickListener(sDialog -> {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog.setTitleText("Cancelled!")
                                .setContentText("Your imaginary file is safe :)")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                        // or you can new a SweetAlertDialog to show
                           /* sDialog.dismiss();
                            new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Cancelled!")
                                    .setContentText("Your imaginary file is safe :)")
                                    .setConfirmText("OK")
                                    .show();*/
                    })
                    .setConfirmClickListener(sDialog -> {
                                resetPassword(sDialog.getEmailText());
                                sDialog.setTitleText("Email sent!")
                                        .setContentText("Please check your email/spam folder for password recovery link!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .showForgotPassword(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                    )
                    .show();
        });

        sign_in.setOnClickListener(v -> {
            email = Objects.requireNonNull(editTextEmail.getText()).toString();
            password = Objects.requireNonNull(editTextPassword.getText()).toString();
            if (email.trim().length() > 0 && password.trim().length() > 0) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.incorrect_username_or_password), Snackbar.LENGTH_LONG).show();
                    }
                });
            } else if (email.length() <= 0) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_user_name), Snackbar.LENGTH_LONG).show();
            } else if (password.length() <= 0) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_password), Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_username_and_password), Snackbar.LENGTH_LONG).show();
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

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getApplicationContext(), "please check your inbox for password reset link", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    Toast.makeText(getApplicationContext(), "user does not exists or is no longer valid.please register again", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}