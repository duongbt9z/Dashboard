package com.example.dashboard.Domain;

import java.io.Serializable;

public class ProductTypeDomain implements Serializable {
    private String name, description, picURL, price,id;

    private int review, numberInCart;
    private double score;

    private Boolean love;

    public ProductTypeDomain(){

    }

    public ProductTypeDomain(String name, String description, String picURL, String price, String id, int review, int numberInCart, double score, Boolean love) {
        this.name = name;
        this.description = description;
        this.picURL = picURL;
        this.price = price;
        this.id = id;
        this.review = review;
        this.numberInCart = numberInCart;
        this.score = score;
        this.love = love;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Boolean getLove() {
        return love;
    }

    public void setLove(Boolean love) {
        this.love = love;
    }
}
