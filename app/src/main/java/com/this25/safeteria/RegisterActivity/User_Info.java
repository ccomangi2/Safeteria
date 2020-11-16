package com.this25.safeteria.RegisterActivity;

public class User_Info {
    String nickname;
    private String userName;
    private String userid;
    private String email;
    String area;
    String spinner_do;
    String photoUrl;
    public User_Info(String userName, String userid, String email, String nickname, String area, String spinner_do, String photoUrl) {
        this.userName = userName;
        this.userid = userid;
        this.email = email;
        this.nickname = nickname;
        this.area = area;
        this.spinner_do = spinner_do;
        this.photoUrl = photoUrl;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSpinner_do() {
        return spinner_do;
    }

    public void setSpinner_do(String spinner_do) {
        this.spinner_do = spinner_do;
    }
}
