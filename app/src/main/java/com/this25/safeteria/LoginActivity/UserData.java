package com.this25.safeteria.LoginActivity;

import android.graphics.drawable.Drawable;

import android.net.Uri;


/**

 * Created by ghd-t on 2018-04-23.

 */


public class UserData {
    private String usersave;
    private String userName;
    private String userid;
    private String email;
    private String photoUri;
    private String phone;

    public UserData(String usersave, String userName, String userid, String email, String photoUri, String phone) {
        this.usersave = usersave;
        this.userName = userName;
        this.userid = userid;
        this.email = email;
        this.photoUri = photoUri;
        this.phone = phone;
    }

    public String getUserSave() {
        return usersave;
    }

    public void setUserSave(String usersave) {
        this.usersave = usersave;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}