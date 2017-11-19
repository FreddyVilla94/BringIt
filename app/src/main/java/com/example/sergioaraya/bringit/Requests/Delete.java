package com.example.sergioaraya.bringit.Requests;

import android.util.Log;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Singleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by SergioAraya on 09/11/2017.
 */

public class Delete {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

    /**
     * Delete product from shopping list request
     */
    public void deleteProductShopList() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(constants.getUrlDeleteProductShopList() + singleton.getProduct().getId() + "/" + singleton.getShoppingList().getId());

        try {

            HttpResponse response = httpClient.execute(httpDelete);
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
     * Delete a shopping list from user request
     */
    public void deleteShopList() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(constants.getUrlDeleteShopList() + singleton.getShoppingList().getId());

        try {

            HttpResponse response = httpClient.execute(httpDelete);
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
