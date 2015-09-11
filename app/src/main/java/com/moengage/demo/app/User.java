package com.moengage.demo.app;

import java.util.ArrayList;

/**
 * Created by abhisheknandi on 05/09/15.
 */
public class User {

  private String uid;
  private ArrayList<Product> cart;

  public User(String uid){
    this.uid = uid;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public ArrayList<Product> getCart() {
    return cart;
  }

  public void setCart(ArrayList<Product> cart) {
    this.cart = cart;
  }
}
