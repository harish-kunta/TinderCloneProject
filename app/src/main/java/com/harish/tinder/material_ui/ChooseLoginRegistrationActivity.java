package com.harish.tinder.material_ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.harish.tinder.R;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {
    TextView customText;
    private Button mLogin, mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);
        customText = findViewById(R.id.agreeNContinueTV);
        customTextView(customText);

        mLogin = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By tapping Create Account or Sign In, you agree to our ");
        spanTxt.append("Terms of Service");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_terms)));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - "Terms of Service".length(), spanTxt.length(), 0);
        spanTxt.append(". Learn how we process your data in our");
        //spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 32, spanTxt.length(), 0);
        spanTxt.append(" Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy)));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        spanTxt.append(" and");
        spanTxt.append(" Cookies Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy)));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - " Cookies Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }
}
