package com.example.dashboard.Domain;

import java.util.List;

public class ProductDomain {
    private String id;
    private String name;
    private String category;
    private float price;
    private Float offerPercentage;
    private String description;
    private List<Integer> colors;
    private List<String> sizes;
    private List<String> images;

    public ProductDomain(String id, String name, String category, float price, Float offerPercentage, String description, List<Integer> colors, List<String> sizes, List<String> images) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.offerPercentage = offerPercentage;
        this.description = description;
        this.colors = colors;
        this.sizes = sizes;
        this.images = images;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Float getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Float offerPercentage) {
        this.offerPercentage = offerPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}



