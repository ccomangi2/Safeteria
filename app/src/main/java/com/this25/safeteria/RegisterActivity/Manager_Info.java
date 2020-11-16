package com.this25.safeteria.RegisterActivity;

public class Manager_Info {
    String store_name; //가게 이름
    String time1; //이용 시간 전
    String time2; //이용 시간 후
    String service; //서비스
    String homepage; //홈페이지
    String notice; //order 공지사함
    String rating; //평점
    String area; //주소
    String main_photoUri; //대표 사진

    public Manager_Info(String store_name, String time1, String time2, String service, String homepage, String notice, String main_photoUri, String area) {
        this.store_name = store_name;
        this.time1 = time1;
        this.time2 = time2;
        this.service = service;
        this.homepage = homepage;
        this.notice = notice;
        this.main_photoUri = main_photoUri;
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getMain_photoUri() {
        return main_photoUri;
    }

    public void setMain_photoUri(String main_photoUri) {
        this.main_photoUri = main_photoUri;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }
}
