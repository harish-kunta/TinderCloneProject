package com.harish.tinder.material_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "RegistrationActivity";
    private TextInputEditText user_name;
    private TextInputEditText user_email;
    private TextInputEditText user_password;
    private TextInputEditText user_check_password;
    private TextInputEditText user_dob;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private RadioGroup mRadioGroup;
    private ScrollView rootLayout;
    private ProgressDialog mRegProgress;

    private Button signup;

    private String userEmail, password, dob, name, checkPassword;

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
        user_dob = (TextInputEditText) findViewById(R.id.dob_field);
        user_password = (TextInputEditText) findViewById(R.id.password_reg_field);
        user_check_password = (TextInputEditText) findViewById(R.id.confirm_pass_field);
        signup = (Button) findViewById(R.id.signup_reg_button);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        signup.setOnClickListener(v -> {
            int selectId = mRadioGroup.getCheckedRadioButtonId();
            name = user_name.getText().toString();
            userEmail = user_email.getText().toString();
            password = user_password.getText().toString();
            checkPassword = user_check_password.getText().toString();
            dob = user_dob.getText().toString();
            Integer unixTime = dateToUnix(dob);
            if (name.length() <= 0) {
                Snackbar.make(rootLayout, "Enter name", Snackbar.LENGTH_LONG).show();
            } else if (name.matches(".*\\d.*")) {
                Snackbar.make(rootLayout, "Name cannot contain numbers", Snackbar.LENGTH_LONG).show();
            } else if (checkCapitalLetters(userEmail)) {
                Snackbar.make(rootLayout, "Email cannot contain capital letters", Snackbar.LENGTH_LONG).show();
            } else if (!isValidEmail(userEmail)) {
                Snackbar.make(rootLayout, "Enter valid email", Snackbar.LENGTH_LONG).show();
            } else if (selectId == -1) {
                Snackbar.make(rootLayout, "Gender not selected", Snackbar.LENGTH_LONG).show();
            } else if (dob.length() <= 0) {
                Snackbar.make(rootLayout, "Enter DOB", Snackbar.LENGTH_LONG).show();
            } else if (unixTime == null) {
                Snackbar.make(rootLayout, "Error while parsing DOB", Snackbar.LENGTH_LONG).show();
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
                        userInfo.put("dob", unixTime.intValue());
                        currentUserDb.updateChildren(userInfo).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Snackbar.make(rootLayout, "Account Created successfully", Snackbar.LENGTH_LONG).show();
                                mRegProgress.dismiss();
                                Intent signin = new Intent(getApplicationContext(), UploadImageActivity.class);
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

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    boolean checkCapitalLetters(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
             if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static Integer dateToUnix(String timestamp) {
        if (timestamp == null) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dt = sdf.parse(timestamp);
            long epoch = dt.getTime();
            return (int) (epoch / 1000);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mFormat.setRoundingMode(RoundingMode.DOWN);
        String date = mFormat.format(Double.valueOf(dayOfMonth)) + "/" + mFormat.format(Double.valueOf(month)) + "/" + year;
        user_dob.setText(date);
    }
}
