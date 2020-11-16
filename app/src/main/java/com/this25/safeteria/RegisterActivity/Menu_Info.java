package com.this25.safeteria.RegisterActivity;

public class Menu_Info {
    //String rep_manu; //대표메뉴
    String menu_name; //메뉴이름
    String menu_menual; //메뉴설명
    String menu_price; //메뉴 가격

    public Menu_Info(String menu_name, String menu_menual, String menu_price) {
        this.menu_menual = menu_menual;
        this.menu_name = menu_name;
        this.menu_price = menu_price;
    }

    public String getMenu_menual() {
        return menu_menual;
    }

    public void setMenu_menual(String menu_menual) {
        this.menu_menual = menu_menual;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenu_price() {
        return menu_price;
    }

    public void setMenu_price(String menu_price) {
        this.menu_price = menu_price;
    }

   // public String getRep_manu() {
      //  return rep_manu;
    //}

   // public void setRep_manu(String rep_manu) {
     //   this.rep_manu = rep_manu;
    //}
}
