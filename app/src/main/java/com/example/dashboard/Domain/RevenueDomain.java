package com.example.dashboard.Domain;

public class RevenueDomain {
    private String username;
    private String productName;
    private String pic;
    private String quanity;
    private String totalPrice;
    private String orderTime;
    private String orderDate;
    private String address;

    public RevenueDomain() {
    }

    public RevenueDomain(String username, String productName, String pic, String quanity, String totalPrice, String orderTime, String orderDate, String address) {
        this.username = username;
        this.productName = productName;
        this.pic = pic;
        this.quanity = quanity;
        this.totalPrice = totalPrice;
        this.orderTime = orderTime;
        this.orderDate = orderDate;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getQuanity() {
        return quanity;
    }

    public void setQuanity(String quanity) {
        this.quanity = quanity;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
