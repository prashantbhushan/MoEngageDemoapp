package com.moengage.demo.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.moe.pushlibrary.MoEHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MoEngageDemoApp extends Application {


  private static MoEngageDemoApp _INSTANCE;

  public static MoEngageDemoApp getInstance(){
    if( _INSTANCE == null){
      throw new IllegalStateException("Application class was not instantiated");
    }
    return _INSTANCE;
  }

  @Override public void onCreate() {
    super.onCreate();
    _INSTANCE = this;
    MoEHelper.setInAppDelayDurationInMins(this, 1);
    Firebase.setAndroidContext(this);
    MoEHelper.APP_DEBUG = true;
    populateCategories();
    populateOrderHistory();

  }

  public SharedPreferences getSharedPreferences(){
    return getSharedPreferences("DEMO_APP", Context.MODE_PRIVATE);
  }

  public HashMap<String, Category> categoryList;

  public static final int ID_CAT_CAMERA = 1;
  public static final int ID_CAT_CAMERA_ACC = 2;
  public static final int ID_CAT_MOBILE = 3;
  public static final int ID_CAT_MOBILE_ACC = 4;
  public static final int ID_CAT_LAPTOP = 5;
  public static final int ID_CAT_LAPTOP_ACC = 6;
  public static final int ID_CAT_FURNITURE = 7;


  public static final String KEY_CAT_CAMERA = "cameras";
  public static final String KEY_CAT_CAMERA_ACC = "camera_accessories";
  public static final String KEY_CAT_MOBILE = "mobiles";
  public static final String KEY_CAT_MOBILE_ACC = "mobile_accessories";
  public static final String KEY_CAT_LAPTOP = "laptops";
  public static final String KEY_CAT_LAPTOP_ACC = "laptop_accessories";
  public static final String KEY_CAT_FURNITURE = "furniture";


  private void populateCategories(){
    categoryList = new HashMap<>();

    categoryList.put(KEY_CAT_MOBILE, new Category("Mobiles", ID_CAT_MOBILE, "https://imagehoster.firebaseapp.com/mobiles.jpg"));
    categoryList.put(KEY_CAT_MOBILE_ACC, new Category("Mobile Accessories", ID_CAT_MOBILE_ACC, "https://imagehoster.firebaseapp.com/mobile_accessories.png"));

    categoryList.put(KEY_CAT_CAMERA, new Category("Cameras", ID_CAT_CAMERA, "https://imagehoster.firebaseapp.com/camera.jpg"));
    categoryList.put(KEY_CAT_CAMERA_ACC, new Category("Camera Accessories", ID_CAT_CAMERA_ACC, "https://imagehoster.firebaseapp.com/camera_accessories.jpg"));

    categoryList.put(KEY_CAT_LAPTOP_ACC, new Category("Laptop Accessories", ID_CAT_LAPTOP_ACC, "https://imagehoster.firebaseapp.com/laptop_accessories.jpg"));
    categoryList.put(KEY_CAT_LAPTOP, new Category("Laptops", ID_CAT_LAPTOP, "https://imagehoster.firebaseapp.com/laptop.jpg"));

    categoryList.put(KEY_CAT_FURNITURE, new Category("Furniture", ID_CAT_FURNITURE,
        "https://imagehoster.firebaseapp.com/furniture.jpg"));

  }

  public static String getCategoryNameFromId(int categoryId){
    switch (categoryId){
      case ID_CAT_MOBILE:
        return "Mobiles";
      case ID_CAT_MOBILE_ACC:
        return "Mobile Accessories";
      case ID_CAT_CAMERA:
        return "Cameras";
      case ID_CAT_CAMERA_ACC:
        return "Camera Accessories";
      case ID_CAT_LAPTOP_ACC:
        return "Laptop Accessories";
      case ID_CAT_LAPTOP:
        return "Laptops";
      case ID_CAT_FURNITURE:
        return "Furniture";
      default:
        return "unknown";

    }
  }

  public Category categoryInView;

  public Firebase getFirebase(String uri){
      String uid = getSharedPreferences().getString("USER_ID", null);
      if( null == uid ){
        uid = UUID.randomUUID().toString();
        getSharedPreferences().edit().putString("USER_ID", uid).apply();
      }
    return new Firebase("https://imagehoster.firebaseio.com/uid/"+uid+uri);
  }

  public boolean userIsLoggedIn(){
    return getSharedPreferences().contains("loggedIn");
  }

  public void setUserLoggedIn(){
    getSharedPreferences().edit().putBoolean("loggedIn", true).commit();
  }

  public List<Product> orderHistory;

  public List<Product> cartList;

  public List<Product> wishList;

  private final Object lock = new Object();

  public void orderSuccessful(Product newOrder){
    synchronized (lock){
      orderHistory.add(newOrder);
      Firebase ref = getFirebase("/Orders");
      ref.setValue(orderHistory);
    }
  }

  public void checkoutAll(){
    synchronized (lock){
      orderHistory.addAll(cartList);
      Firebase ref1 = getFirebase("/Orders");
      ref1.setValue(orderHistory);

      cartList = null;
      Firebase ref2 = getFirebase("/Cart");
      ref2.setValue(cartList);
    }
  }

  public void addToCart(Product product){
    synchronized (lock){
      cartList.add(product);
      double totalPrice = 0;
      int cartCount = 0;
      for(Product prod : cartList){
        totalPrice+= prod.price;
        cartCount++;
      }

      MoEHelper.getInstance(this).setUserAttribute("totalCartValue", totalPrice);
      MoEHelper.getInstance(this).setUserAttribute("cartCount", cartCount);

      Firebase ref = getFirebase("/Cart");
      ref.setValue(cartList);
    }
  }

  public void removeFromCart(Product cart){
    synchronized (lock){
      cartList.remove(cart);
      Firebase ref = getFirebase("/Cart");
      ref.setValue(cartList);
    }
  }

  public void addToWishList(Product product){
    synchronized (lock){
      wishList.add(product);
      Firebase ref = getFirebase("/wishlist");
      ref.setValue(wishList);
    }
  }

  public void removeFromWishList(Product product){
    synchronized (lock){
      wishList.remove(product);
      Firebase ref = getFirebase("/wishlist");
      ref.setValue(wishList);
    }
  }

  private void populateOrderHistory(){
    Firebase orderDataSet = getFirebase("/Orders");
    orderDataSet.addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot snapshot) {
        GenericTypeIndicator<ArrayList<Product>> t = new GenericTypeIndicator<ArrayList<Product>>() {
        };
        if( null != snapshot){
          orderHistory = snapshot.getValue(t);
        }
        if( null == orderHistory){
          orderHistory = new ArrayList<>();
        }
      }

      @Override public void onCancelled(FirebaseError firebaseError) {

      }
    });

    Firebase cartDataSet = getFirebase("/Cart");
    cartDataSet.addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot snapshot) {
        GenericTypeIndicator<List<Product>> t = new GenericTypeIndicator<List<Product>>() {
        };
        if( null != snapshot){
          cartList = snapshot.getValue(t);
        }
        if( null == cartList){
          cartList = new ArrayList<>();
        }
      }

      @Override public void onCancelled(FirebaseError firebaseError) {

      }
    });

    Firebase wishDataSet = getFirebase("/wishlist");
    wishDataSet.addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot snapshot) {
        GenericTypeIndicator<List<Product>> t = new GenericTypeIndicator<List<Product>>() {
        };
        if( null != snapshot){
          wishList = snapshot.getValue(t);
        }
        if( null == wishList){
          wishList = new ArrayList<>();
        }
      }

      @Override public void onCancelled(FirebaseError firebaseError) {

      }
    });
  }


  public Category getCategoryById(int id){
    Set<Map.Entry<String, Category>> keySet = categoryList.entrySet();
    for(Map.Entry<String, Category> map : keySet){
      if(map.getValue().id == id){
        return map.getValue();
      }
    }
    return null;
  }

}
