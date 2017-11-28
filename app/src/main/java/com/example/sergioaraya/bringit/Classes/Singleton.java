package com.example.sergioaraya.bringit.Classes;

import com.example.sergioaraya.bringit.Adapters.AdapterShoppingListProducts;
import com.example.sergioaraya.bringit.Adapters.AdapterShoppingLists;

/**
 * Created by SergioAraya on 15/09/2017.
 */

public class Singleton {

    private static Singleton singleton;
    private String body;
    private int status;
    private User user = new User();
    private ShoppingList shoppingList = new ShoppingList();
    private Product product = new Product();
    private AdapterShoppingLists adapterShoppingLists;
    private AdapterShoppingListProducts adapterShoppingListProducts;
    private int controlUpdateShoppingList;
    private int controlUpdateProduct;

    private Singleton() {}

    public static synchronized Singleton getInstance() {

        if (singleton == null){

            singleton = new Singleton();

        }

        return singleton;
    }

    public String getBody() { return body; }

    public void setBody(String body) { this.body = body; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status;}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShoppingList getShoppingList() { return shoppingList; }

    public void setShoppingList(ShoppingList shoppingList) { this.shoppingList = shoppingList; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) {
        this.product = product;
    }

    public AdapterShoppingLists getAdapterShoppingLists() {
        return adapterShoppingLists;
    }

    public void setAdapterShoppingLists(AdapterShoppingLists adapterShoppingLists) { this.adapterShoppingLists = adapterShoppingLists; }

    public AdapterShoppingListProducts getAdapterShoppingListProducts() { return adapterShoppingListProducts; }

    public void setAdapterShoppingListProducts(AdapterShoppingListProducts adapterShoppingListProducts) { this.adapterShoppingListProducts = adapterShoppingListProducts; }

    public int getControlUpdateShoppingList() { return controlUpdateShoppingList; }

    public void setControlUpdateShoppingList(int controlUpdateShoppingList) { this.controlUpdateShoppingList = controlUpdateShoppingList; }

    public int getControlUpdateProduct() { return controlUpdateProduct; }

    public void setControlUpdateProduct(int controlUpdateProduct) { this.controlUpdateProduct = controlUpdateProduct; }
}
