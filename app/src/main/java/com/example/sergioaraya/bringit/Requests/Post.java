package com.example.sergioaraya.bringit.Requests;

import android.util.Log;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Singleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SergioAraya on 09/11/2017.
 */

public class Post {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

    /**
     * Post request to save the new product in the shopping list
     */
    public void saveProductShopList(String name, String quantity, String price) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(constants.getUrlSaveProduct());

        try {

            List nameValuePairs = new ArrayList(4);
            nameValuePairs.add(new BasicNameValuePair("idShopList", singleton.getShoppingList().getId()));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("quantity", quantity));
            nameValuePairs.add(new BasicNameValuePair("price", price));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            final String responseBody = EntityUtils.toString(response.getEntity());

            singleton.setBody(responseBody);
            singleton.setStatus(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            Log.e(constants.getTagSLsA(), "Error sending data to backend.", e);
        } catch (IOException e) {
            Log.e(constants.getTagSLsA(), "Error sending data to backend.", e);
        }

    }

    /**
     * Post request to save the new shopping list
     * @param name
     * @param date to set notifications date
     * @param time to set notifications hour
     */
    public void saveShopList(String name, String date, String time) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(constants.getUrlSaveShopList());

        try {

            List nameValuePairs = new ArrayList(4);
            nameValuePairs.add(new BasicNameValuePair("idUser", singleton.getUser().getId()));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("shopDate", date));
            nameValuePairs.add(new BasicNameValuePair("shopTime", time));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            final String responseBody = EntityUtils.toString(response.getEntity());

            singleton.setBody(responseBody);
            singleton.setStatus(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            Log.e(constants.getTagSLsA(), "Error sending data to backend.", e);
        } catch (IOException e) {
            Log.e(constants.getTagSLsA(), "Error sending data to backend.", e);
        }

    }

    /**
     * Post request to save or authenticate current user
     */
    public void authenticate() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(constants.getUrlAuthenticate());

        try {

            List nameValuePairs = new ArrayList(2);
            nameValuePairs.add(new BasicNameValuePair("email", singleton.getUser().getEmail()));
            nameValuePairs.add(new BasicNameValuePair("password", singleton.getUser().getPassword()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            final String responseBody = EntityUtils.toString(response.getEntity());

            singleton.setBody(responseBody);
            singleton.setStatus(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            Log.e(constants.getTagMA(), "Error sending data to backend.", e);
        } catch (IOException e) {
            Log.e(constants.getTagMA(), "Error sending data to backend.", e);
        }

    }

    /**
     * Post request no save a new user
     */
    public void saveUser() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(constants.getUrlSaveUser());

        try {

            List nameValuePairs = new ArrayList(4);
            nameValuePairs.add(new BasicNameValuePair("password", singleton.getUser().getPassword()));
            nameValuePairs.add(new BasicNameValuePair("userImage", singleton.getUser().getImage()));
            nameValuePairs.add(new BasicNameValuePair("name", singleton.getUser().getName()));
            nameValuePairs.add(new BasicNameValuePair("email", singleton.getUser().getEmail()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            final String responseBody = EntityUtils.toString(response.getEntity());

            singleton.setBody(responseBody);
            singleton.setStatus(response.getStatusLine().getStatusCode());


        } catch (ClientProtocolException e) {
            Log.e(constants.getTagMA(), "Error sending data to backend.", e);
        } catch (IOException e) {
            Log.e(constants.getTagMA(), "Error sending data to backend.", e);
        }
    }

    /**
     * Post request to save or authenticate current user by social network
     */
    public void loginSocialNetwork() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(constants.getUrlLoginSocialNetwork());

        try {

            List nameValuePairs = new ArrayList(3);
            nameValuePairs.add(new BasicNameValuePair("email", singleton.getUser().getEmail()));
            nameValuePairs.add(new BasicNameValuePair("name", singleton.getUser().getName()));
            nameValuePairs.add(new BasicNameValuePair("userImage", singleton.getUser().getImage()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            final String responseBody = EntityUtils.toString(response.getEntity());

            singleton.setBody(responseBody);
            singleton.setStatus(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            Log.e(constants.getTagMA(), "Error sending data to backend.", e);
        } catch (IOException e) {
            Log.e(constants.getTagMA(), "Error sending data to backend.", e);
        }


    }

}
