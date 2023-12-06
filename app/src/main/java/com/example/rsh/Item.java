package com.example.rsh;

public class Item {
    String name, countVote, price, imageUrl;
    boolean isSpecial;

    public Item(String name, String countVote, String price, String imageUrl, boolean isSpecial) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.countVote = countVote;
        this.price = price;
        this.isSpecial = isSpecial;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountVote() {
        return countVote;
    }

    public void setCountVote(String countVote) {
        this.countVote = countVote;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isSpecial() {
        return isSpecial;
    }
}
