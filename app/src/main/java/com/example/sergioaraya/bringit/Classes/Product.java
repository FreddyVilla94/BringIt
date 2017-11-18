package com.example.sergioaraya.bringit.Classes;

/**
 * Created by SergioAraya on 14/09/2017.
 */

public class Product {

    private String id;
    private String name;
    private int price;
    private int quantity;
    private boolean isInCart;
    private String image;

    public Product() { }

    public Product(String id, String name, int price, int quantity, boolean isInCart, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isInCart = isInCart;
        this.image = image;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getIsInCart() {
        return isInCart;
    }

    public void setIsInCart(boolean isInCart) {
        this.isInCart = isInCart;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", isInCart=" + isInCart +
                ", image=" + image +
                '}';
    }
}
