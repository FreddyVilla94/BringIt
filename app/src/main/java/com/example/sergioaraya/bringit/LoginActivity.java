package com.example.sergioaraya.bringit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
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

import com.cuboid.cuboidcirclebutton.CuboidButton;
import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Product;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Requests.Post;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

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

    private Post post;

    private ProgressDialog progressDialog;

    private ShoppingList shoppingList;
    private Product product;

    // The core Google Play Services client.
    private GoogleApiClient googleApiClient;

    // Some variables to do the login with facebook.
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        // First we need to configure the Google Sign In API to ensure we are retrieving
        // the server authentication code as well as authenticating the client locally.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // We pass through three "this" arguments to the builder, specifying the:
        // 1. Context
        // 2. Object to use for resolving connection errors
        // 3. Object to call onConnectionFailed on
        // We also add the Google Sign in API we previously created.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        // Configure the ProgressDialog that will be shown if there is a
        // delay in presenting the user with the next sign in step.
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.login_activity_signing_in));

        activityLogin = (ScrollView) findViewById(R.id.activity_login);
        activityLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardOnTouchView(v);
                return false;
            }
        });

        buttonLogin.setOnClickListener(this);
        buttonGoogle.setOnClickListener(this);
        buttonFacebook.setOnClickListener(this);
        registerLink.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        // Callback registration.
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.d("Success", "Login");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject objects, GraphResponse response) {

                        if (response != null){
                            try{
                                JSONObject data = response.getJSONObject();
                                singleton.getUser().setName(objects.optString("first_name"));
                                singleton.getUser().setEmail(response.getJSONObject().optString("email"));
                                singleton.getUser().setImage(data.getJSONObject("picture").getJSONObject("data").getString("url"));
                                new taskLoginSocialNetwork().execute();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_activity_login_cancelled), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == constants.getRcSignIn()) {
            // Resolve the intent into a GoogleSignInResult we can process
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // When we get here in an automanager activity the error is likely not
        // resolvable - meaning Google Sign In and other Google APIs will be
        // unavailable.
        Log.d(constants.getTagMA(), "onConnectionFailed:" + connectionResult);

    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            singleton.getUser().setName(account.getDisplayName());
            singleton.getUser().setEmail(account.getEmail());

            if (account.getPhotoUrl() == null){
                singleton.getUser().setImage("");
            } else{
                singleton.getUser().setImage(account.getPhotoUrl().toString());
            }
            new taskLoginSocialNetwork().execute();

        } else{
            updateUI(false);
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_activity_authentification_fail),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hide keyboard when view is touched
     * @param view is the scrollview loginActivity
     */
    public void hideKeyboardOnTouchView(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                if (isNetAvailed()){
                    if (! (validateFormat(loginEmail.getText().toString(), false)) || loginEmail.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_invalid_data), Toast.LENGTH_LONG).show();
                        loginEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                        loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    } else if (! (validateFormat(loginPassword.getText().toString(), true)) || loginPassword.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_invalid_data), Toast.LENGTH_LONG).show();
                        loginEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                        loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    } else {
                        singleton.getUser().setEmail(loginEmail.getText().toString());
                        singleton.getUser().setPassword(loginPassword.getText().toString());
                        progressDialog.show();
                        new taskAuthenticate().execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_net_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_google:
                Log.v(constants.getTagMA(), "Tapped sign in");
                if (isNetAvailed()){
                    // Show the dialog as we are now signing in.
                    progressDialog.show();
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(intent, constants.getRcSignIn());
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_net_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_twitter:
                break;
            case R.id.button_facebook:
                if (isNetAvailed()){
                    // Login with facebook on clicking custom button.
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_net_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.register_link:
                intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * Check if the device has a net connection
     * @return true if network is up and false is not
     */
    private boolean isNetAvailed() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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

     /* Update user interface afther the user is authenticated
     * @param isLogin true if login is valid and false if login us invalid
     */
    private void updateUI(boolean isLogin){
        if (isLogin){
            // Hide the progress dialog if its showing.
            progressDialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Get the user from server parsing a json file
     * @param url
     */
    private void parseJsonToGetUser(String url){
        try {
            //parsing JSON file
            JSONObject reader = new JSONObject(url);
            JSONObject user = reader.getJSONObject("user");
            singleton.getUser().setId(user.getString("_id"));
            singleton.getUser().setImage(user.getString("userImage"));
            singleton.getUser().setEmail(user.getString("email"));
            singleton.getUser().setName(user.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
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
                parseJsonToGetUser(singleton.getBody());
                new taskParseJsonToGetUserShopLists().execute(constants.getUrlGetShopListsUser() + singleton.getUser().getId());
            }
        }
    }

    /**
     * Async task to a post request to authenticate or save the current user
     */
    private class taskLoginSocialNetwork extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            post = new Post();
            post.loginSocialNetwork();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_activity_error_authenfication), Toast.LENGTH_LONG).show();
            } else {
                parseJsonToGetUser(singleton.getBody());
                new taskParseJsonToGetUserShopLists().execute(constants.getUrlGetShopListsUser() + singleton.getUser().getId());
            }
        }

    }

    /**
     * Async task to get all data, shopping list and products from user
     */
    private class taskParseJsonToGetUserShopLists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return loadContentFromNetwork(urls[0]);
        }

        protected void onPostExecute(String result) {

            JSONArray shoppingLists = null;
            try {
                shoppingLists = new JSONArray(result);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            for (int i = 0; i < shoppingLists.length(); i++) {
                JSONObject temporalShoppingList = null;
                try {
                    temporalShoppingList = shoppingLists.getJSONObject(i);
                    shoppingList = new ShoppingList();
                    shoppingList.setId(temporalShoppingList.getString("_id"));
                    shoppingList.setAmount(Integer.parseInt(temporalShoppingList.getString("amount")));
                    shoppingList.setDate(temporalShoppingList.getString("shopDate"));
                    shoppingList.setName(temporalShoppingList.getString("name"));
                    shoppingList.setIdUser(temporalShoppingList.getString("idUser"));
                    shoppingList.setTime(temporalShoppingList.getString("shopTime"));
                    JSONArray products = null;
                    products = temporalShoppingList.getJSONArray("products");
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
            updateUI(true);
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
}
