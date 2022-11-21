package com.harish.tinder.material_ui;

import static com.harish.tinder.model.Constants.DEFAULT;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.tinder.R;
import com.harish.tinder.UploadImageActivity;
import com.harish.tinder.model.Constants;
import com.harish.tinder.model.FirebaseUser;
import com.harish.tinder.utils.StringResourceHelper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private TextInputEditText user_name;
    private TextInputEditText user_email;
    private TextInputEditText user_password;
    private TextInputEditText user_check_password;
    private TextInputEditText user_dob;

    private FirebaseAuth mAuth;
    private RadioGroup mRadioGroup;
    private ScrollView rootLayout;
    private ProgressDialog mRegProgress;

    private String userEmail, password, dob, name, checkPassword;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        mAuth = FirebaseAuth.getInstance();
        rootLayout = findViewById(R.id.root_layout);
        user_name = findViewById(R.id.name_field);
        user_email = findViewById(R.id.email_field);
        user_dob = findViewById(R.id.dob_field);
        user_password = findViewById(R.id.password_reg_field);
        user_check_password = findViewById(R.id.confirm_pass_field);
        Button signup = findViewById(R.id.signup_reg_button);
        TextView signIn = findViewById(R.id.sign_in);
        mRadioGroup = findViewById(R.id.radioGroup);
        context = getApplicationContext();

        user_dob.setOnClickListener(view -> showDatePickerDialog());
        signIn.setOnClickListener(view -> {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });
        signup.setOnClickListener(v -> {
            int selectId = mRadioGroup.getCheckedRadioButtonId();
            name = Objects.requireNonNull(user_name.getText()).toString();
            userEmail = Objects.requireNonNull(user_email.getText()).toString();
            password = Objects.requireNonNull(user_password.getText()).toString();
            checkPassword = Objects.requireNonNull(user_check_password.getText()).toString();
            dob = Objects.requireNonNull(user_dob.getText()).toString();
            Integer unixTime = dateToUnix(dob, getApplicationContext());
            if (name.length() <= 0) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_user_name), Snackbar.LENGTH_LONG).show();
            } else if (name.matches(".*\\d.*")) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.name_cannot_contain_numbers), Snackbar.LENGTH_LONG).show();
            } else if (checkCapitalLetters(userEmail)) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.email_cannot_contain_capital_letters), Snackbar.LENGTH_LONG).show();
            } else if (!isValidEmail(userEmail)) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_valid_email), Snackbar.LENGTH_LONG).show();
            } else if (selectId == -1) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.gender_not_selected), Snackbar.LENGTH_LONG).show();
                //TODO : Verify that user is 18 years or older
            } else if (dob.length() <= 0) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_dob), Snackbar.LENGTH_LONG).show();
            } else if (unixTime == null) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.error_parsing_dob), Snackbar.LENGTH_LONG).show();
            } else if (password.length() <= 0) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_password), Snackbar.LENGTH_LONG).show();
            } else if (!password.equals(checkPassword)) {
                Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.passwords_not_match), Snackbar.LENGTH_LONG).show();
            } else {
                final RadioButton radioButton = findViewById(selectId);
                mRegProgress = new ProgressDialog(RegistrationActivity.this,
                        R.style.AppThemeDialog);
                mRegProgress.setIndeterminate(true);
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.setMessage("Creating Account...");
                mRegProgress.show();
                mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(RegistrationActivity.this, task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(userId);
                        FirebaseUser user = new FirebaseUser(name, userEmail, unixTime, true, DEFAULT, radioButton.getText().toString(), DEFAULT, userId);
                        currentUserDb.setValue(user).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Snackbar.make(rootLayout, R.string.ACCOUNT_CREATED_SUCCESSFULLY, Snackbar.LENGTH_LONG).show();
                                mRegProgress.dismiss();
                                Intent signin = new Intent(getApplicationContext(), UploadImageActivity.class);
                                signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(signin);
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            mRegProgress.dismiss();
                            Snackbar.make(rootLayout, R.string.ACCOUNT_CREATION_UNSUCCESSFULL, Snackbar.LENGTH_LONG).show();
                        });
                    } else {
                        mRegProgress.dismiss();
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Snackbar.make(rootLayout, "Password is too weak! please enter a strong password", Snackbar.LENGTH_LONG).show();
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        //User can only register if they are 18 years old
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public static Integer dateToUnix(String timestamp, Context context) {
        if (timestamp == null) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(StringResourceHelper.getString(context, R.string.date_pattern));
            Date dt = sdf.parse(timestamp);
            assert dt != null;
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
