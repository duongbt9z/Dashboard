package com.example.dashboard.Domain;

import java.io.Serializable;

public class ProductTypeDomain implements Serializable {
    private String title, description, picURL, price;

    private int review, numberInCart;
    private double score;

    public ProductTypeDomain(){

    }


    public ProductTypeDomain(String title, String description, String picURL, String price, int review, int numberInCart, double score) {
        this.title = title;
        this.description = description;
        this.picURL = picURL;
        this.price = price;
        this.review = review;
        this.numberInCart = numberInCart;
        this.score = score;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPicURL() {
        return picURL;
    }

    public String getPrice() {
        return price;
    }

    public int getReview() {
        return review;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public double getScore() {
        return score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
