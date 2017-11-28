package com.example.sergioaraya.bringit.Requests;

import android.util.Log;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Singleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SergioAraya on 09/11/2017.
 */

public class Put {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

    /**
     * Update product state
     */
    public void updateStateProductShoppingList(){

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(constants.getUrlUpdateStateProductShopList() + singleton.getProduct().getId());

        try {
            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair("isInCart", String.valueOf(singleton.getProduct().getIsInCart())));
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPut);
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
     * Modify product from shopping list
     * @param name product
     * @param quantity product
     * @param price product
     */
    public void modifyProductShoppingList(String name, String quantity, String price) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(constants.getUrlUpdateProductShopList() + singleton.getProduct().getId());

        try {
            List nameValuePairs = new ArrayList(3);
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("quantity", quantity));
            nameValuePairs.add(new BasicNameValuePair("price", price));
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPut);
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
     * Modify shopping list from user
     * @param name shopping list
     * @param date shopping list for notifications
     * @param time shopping list for notifications
     */
    public void modifyShoppingList(String name, String date, String time) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(constants.getUrlUpdateShopList() + singleton.getShoppingList().getId());

        try {

            List nameValuePairs = new ArrayList(3);
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("shopDate", date));
            nameValuePairs.add(new BasicNameValuePair("shopTime", time));
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPut);
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
     * Share shopping list with other user
     * @param id user
     */
    public void shareShoppingList(String id) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(constants.getUrlShareShopList() + singleton.getShoppingList().getId());

        try {
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("idUser", id));
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPut);
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
