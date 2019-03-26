package com.bemo.store;

/**
 * Created by MahmoudAhmed on 4/9/2018.
 */

public class Product {
    private String id;
    private String name;
    private String category;
    private String price;
    private String in_stock;

    public Product( String name, String category, String price, String in_stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.in_stock = in_stock;
    }

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(String in_stock) {
        this.in_stock = in_stock;
    }
}
