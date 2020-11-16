package com.this25.safeteria.WriteActivity;

import java.util.Date;

public class Write_data {
    String store;
    String rating;
    String write;
    String photo;
    String date;
    String user;
    String user_nickname;

    public Write_data(String store, String write, String date, String user, String rating, String like_count, String user_nickname) {
        this.store = store;
        this.rating = rating;
        this.write = write;
        this.photo = photo;
        this.date = date;
        this.user = user;
        this.user_nickname = user_nickname;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRating() {
        return rating;
    }

    public String getPhoto() {
        return photo;
    }

    public String getStore() {
        return store;
    }

    public String getWrite() {
        return write;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setWrite(String write) {
        this.write = write;
    }
}
