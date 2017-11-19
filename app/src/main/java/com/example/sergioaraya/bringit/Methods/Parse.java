package com.example.sergioaraya.bringit.Methods;

import com.example.sergioaraya.bringit.Classes.Product;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SergioAraya on 10/11/2017.
 */

public class Parse {

    Singleton singleton = Singleton.getInstance();

    private ShoppingList shoppingList;
    private Product product;

    /**
     * Parse json object to get all the user shopping list including the new one
     * @param url json object from server
     */
    public void parseJsonToGetNewShopList(String url){

        try {

            //parsing JSON file
            JSONObject reader = new JSONObject(url);

            JSONObject temporalShopList = reader.getJSONObject("shopList");

            shoppingList = new ShoppingList();

            shoppingList.setId(temporalShopList.getString("_id"));
            shoppingList.setAmount(Integer.parseInt(temporalShopList.getString("amount")));
            shoppingList.setDate(temporalShopList.getString("shopDate"));
            shoppingList.setName(temporalShopList.getString("name"));
            shoppingList.setIdUser(temporalShopList.getString("idUser"));
            shoppingList.setTime(temporalShopList.getString("shopTime"));

            singleton.getUser().getShoppingLists().add(shoppingList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse json object to get all the shopping list products including the new one
     * @param url json object from server
     */
    public void parseJsonToGetNewProduct(String url) {

        try {

            //parsing JSON file
            JSONObject reader = new JSONObject(url);

            JSONObject temporalProduct = reader.getJSONObject("product");

            product = new Product();

            product.setId(temporalProduct.getString("_id"));
            product.setName(temporalProduct.getString("name"));
            product.setImage(temporalProduct.getString("image"));
            product.setQuantity(Integer.parseInt(temporalProduct.getString("quantity")));
            product.setPrice(Integer.parseInt(temporalProduct.getString("price")));
            product.setIsInCart(Boolean.parseBoolean(temporalProduct.getString("isInCart")));


            singleton.getShoppingList().setProducts(product);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
