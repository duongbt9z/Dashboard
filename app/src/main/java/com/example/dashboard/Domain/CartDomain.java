package com.example.dashboard.Domain;

public class CartDomain {
    String productName;
    String productPrice;
    String totalQuanity;
    String currentTime, currentDate;
    double totalPrice;

    public CartDomain() {
    }
    public CartDomain(String productName, String productPrice, String totalQuanity, String currentTime, String currentDate, double totalPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.totalQuanity = totalQuanity;
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        this.totalPrice = totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getTotalQuanity() {
        return totalQuanity;
    }

    public void setTotalQuanity(String totalQuanity) {
        this.totalQuanity = totalQuanity;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
