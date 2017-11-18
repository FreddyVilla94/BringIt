package com.example.sergioaraya.bringit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.register_username) EditText registerUsername;
    @BindView(R.id.register_email) EditText registerEmail;
    @BindView(R.id.register_password) EditText registerPassword;
    @BindView(R.id.register_confirm_password) EditText registerConfirmPassword;
    @BindView(R.id.button_register) Button buttonRegister;
    @BindView(R.id.login_link) TextView loginLink;

    // Variable used to hide keyboard
    private ScrollView activitySignup;

    // Variable to execute intents to another activities
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        activitySignup = (ScrollView) findViewById(R.id.sign_up_activity);
        activitySignup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardOnTouchView(v);
                return false;
            }
        });

        buttonRegister.setOnClickListener(this);
        loginLink.setOnClickListener(this);
    }

    /**
     * Hide keyboard when view is touched
     * @param view is the scrollview loginActivity
     */
    public void hideKeyboardOnTouchView(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.login_link:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
