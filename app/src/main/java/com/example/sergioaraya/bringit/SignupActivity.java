package com.example.sergioaraya.bringit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Product;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Methods.Parse;
import com.example.sergioaraya.bringit.Requests.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

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

    private ProgressDialog progressDialog;

    private Post post;
    private Parse parse;

    private ShoppingList shoppingList;
    private Product product;

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

        // Configure the ProgressDialog that will be shown if there is a
        // delay in presenting the user with the next sign in step.
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Signing in...");

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

    /**
     * Validate input(email, password) according with a patther
     * @param string email or password
     * @param type defines if the input is a password or email
     * @return if input matches with pattern and false if not
     */
    private boolean validateFormat(String string, boolean type) {
        Pattern pattern; Matcher matcher;
        if (type){
            pattern = Pattern.compile(constants.getPatternPassword());
            matcher = pattern.matcher(string);
        } else{
            pattern = Pattern.compile(constants.getPatternEmail());
            matcher = pattern.matcher(string);
        }
        return matcher.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register:
                if (registerUsername.getText().toString().equals("")){
                    registerUsername.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_activity_invalid_username), Toast.LENGTH_LONG).show();
                } else if (! (validateFormat(registerEmail.getText().toString(), false)) || registerEmail.getText().toString().equals("")) {
                    registerEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_activity_invalid_email), Toast.LENGTH_LONG).show();
                } else if (! (validateFormat(registerPassword.getText().toString(), true)) || registerPassword.getText().toString().equals("")) {
                    registerPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_activity_invalid_password), Toast.LENGTH_LONG).show();
                } else if (! registerPassword.getText().toString().equals(registerConfirmPassword.getText().toString())) {
                    registerConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_activity_invalid_confirm_password), Toast.LENGTH_LONG).show();
                } else {
                    singleton.getUser().setImage("");
                    singleton.getUser().setName(registerUsername.getText().toString());
                    singleton.getUser().setEmail(registerEmail.getText().toString());
                    singleton.getUser().setPassword(registerPassword.getText().toString());
                    progressDialog.show();
                    new taskSaveUser().execute();
                }
                break;
            case R.id.login_link:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * Async task to authenticate user when login button is pressed
     */
    private class taskAuthenticate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            post = new Post();
            post.authenticate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (singleton.getStatus() != 200){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_error_authenfication), Toast.LENGTH_LONG).show();
            } else {
                parse = new Parse();
                parse.parseJsonToGetNewUser(singleton.getBody());
                new taskParseJsonToGetUserShoppingLists().execute(constants.getUrlGetShopListsUser() + singleton.getUser().getId());
            }
        }
    }

    /**
     * Async task to get all data, shopping list and products from user
     */
    private class taskParseJsonToGetUserShoppingLists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return loadContentFromNetwork(urls[0]);
        }

        protected void onPostExecute(String result) {

            JSONArray shopLists = null;
            try {
                shopLists = new JSONArray(result);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            for (int i = 0; i < shopLists.length(); i++) {

                JSONObject temporalShopList = null;
                try {

                    temporalShopList = shopLists.getJSONObject(i);
                    shoppingList = new ShoppingList();

                    shoppingList.setId(temporalShopList.getString("_id"));
                    shoppingList.setAmount(Integer.parseInt(temporalShopList.getString("amount")));
                    shoppingList.setDate(temporalShopList.getString("shopDate"));
                    shoppingList.setName(temporalShopList.getString("name"));
                    shoppingList.setIdUser(temporalShopList.getString("idUser"));
                    shoppingList.setTime(temporalShopList.getString("shopTime"));

                    JSONArray products = null;

                    products = temporalShopList.getJSONArray("products");

                    for (int j = 0; j < products.length(); j++) {

                        JSONObject temporalProduct = null;
                        temporalProduct = products.getJSONObject(j);
                        product = new Product();

                        product.setId(temporalProduct.getString("_id"));
                        product.setIsInCart(Boolean.parseBoolean(temporalProduct.getString("isInCart")));
                        product.setQuantity(Integer.parseInt(temporalProduct.getString("quantity")));
                        product.setImage(temporalProduct.getString("image"));
                        product.setPrice(Integer.parseInt(temporalProduct.getString("price")));
                        product.setName(temporalProduct.getString("name"));

                        shoppingList.setProducts(product);

                    }

                    singleton.getUser().setShoppingLists(shoppingList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        // method to download the content
        private String loadContentFromNetwork(String url) {
            try {
                InputStream mInputStream = (InputStream) new URL(url).getContent();
                InputStreamReader mInputStreamReader = new InputStreamReader(mInputStream);
                BufferedReader responseBuffer = new BufferedReader(mInputStreamReader);
                StringBuilder strBuilder = new StringBuilder();
                String line = null;
                while ((line = responseBuffer.readLine()) != null) {
                    strBuilder.append(line);
                }
                return strBuilder.toString();

            } catch (Exception e) {
                Log.v(constants.getTagImg(), e.getMessage());
            }
            return null;
        }
    }

    /**
     * Async task to do a post request to save a new user
     */
    private class taskSaveUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            post = new Post();
            post.saveUser();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_activity_error_signing_up), Toast.LENGTH_LONG).show();

            } else {
                new taskAuthenticate().onPostExecute(result);
            }
        }

    }

}
