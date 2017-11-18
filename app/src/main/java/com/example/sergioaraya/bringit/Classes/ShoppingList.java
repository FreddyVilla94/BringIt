package com.example.sergioaraya.bringit.Classes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by SergioAraya on 14/09/2017.
 */

public class ShoppingList {

    private String id;
    private String name;
    private String date;
    private String time;
    private int amount;
    private ArrayList<String> users;
    private ArrayList<Product> products;
    private String idUser;

    public ShoppingList() {
        users = new ArrayList<>();
        products = new ArrayList<>();
    }

    public ShoppingList(String id, String name, String date, String time, int amount, String idUser) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.amount = amount;
        users = new ArrayList<>();
        products = new ArrayList<>();
        this.idUser = idUser;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(String user) {
        users.add(user);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(Product product) {
        products.add(product);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shopDate='" + date + '\'' +
                ", shopTime='" + time + '\'' +
                ", amount=" + amount +
                ", idUser=" + idUser +
                '}';
    }
}
