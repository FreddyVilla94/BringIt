package com.example.sergioaraya.bringit.Classes;

import java.util.ArrayList;

/**
 * Created by SergioAraya on 14/09/2017.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private String image;
    private ArrayList<ShoppingList> shoppingLists;

    public User() {
        shoppingLists = new ArrayList<>();
    }

    public User(String id, String name, String email, String password, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        shoppingLists = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public ArrayList<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(ShoppingList shoppingList) {
        shoppingLists.add(shoppingList);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", password=" + password +
                ", image=" + image +
                '}';
    }

}
