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

import com.cuboid.cuboidcirclebutton.CuboidButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.login_email) EditText loginEmail;
    @BindView(R.id.login_password) EditText loginPassword;
    @BindView(R.id.button_login) Button buttonLogin;
    @BindView(R.id.button_google) CuboidButton buttonGoogle;
    @BindView(R.id.button_twitter) CuboidButton buttonTwitter;
    @BindView(R.id.button_facebook) CuboidButton buttonFacebook;
    @BindView(R.id.register_link) TextView registerLink;

    // Variable used to hide keyboard
    private ScrollView activityLogin;

    // Variable to execute intents to another activities
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        activityLogin = (ScrollView) findViewById(R.id.activity_login);
        activityLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardOnTouchView(v);
                return false;
            }
        });

        buttonLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);

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
            case R.id.button_login:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.register_link:
                intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
