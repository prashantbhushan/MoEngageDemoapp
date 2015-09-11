package com.moengage.demo.app;

import android.content.Context;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;
import java.util.Date;
import org.json.JSONObject;


public final class EventTracker {

  private EventTracker(){

  }

  public static void orderSuccessful(Context context, JSONObject value){
    MoEHelper.getInstance(context)
        .trackEvent("orderSuccessful", value);
  }


  public static void trackAddToCartEvent(Product product, Context context){
    PayloadBuilder builder = new PayloadBuilder();
    builder.putAttrDate("date", new Date())
        .putAttrDouble("price", product.price)
        .putAttrString("id", product.productId)
        .putAttrInt("categoryId", product.categoryId)
        .putAttrString("prodName", product.title)
        .putAttrString("currency", product.currency)
        .putAttrInt("quantity", 1);
    MoEHelper.getInstance(context).trackEvent("addedToCart", builder.build());
    MoEHelper.getInstance(context).setUserAttribute("lastAddedProduct", product.title);
  }

  public static void trackWishlistEvent(Product product, Context context, boolean add){
    PayloadBuilder builder = new PayloadBuilder();
    builder.putAttrDate("date", new Date())
        .putAttrDouble("price", product.price)
        .putAttrString("id", product.productId)
        .putAttrInt("categoryId", product.categoryId)
        .putAttrString("prodName", product.title)
        .putAttrString("currency", product.currency);
    if( add ){
      MoEHelper.getInstance(context).trackEvent("addedToWishlist", builder.build());
    }else{
      MoEHelper.getInstance(context).trackEvent("removeFromWishlist", builder.build());
    }
  }

  public static void trackProductViewed(Product product, Context context){
    PayloadBuilder builder = new PayloadBuilder();
    builder.putAttrString("productName", product.title)
        .putAttrString("productCategory", MoEngageDemoApp.getCategoryNameFromId(product.categoryId))
        .putAttrDouble("productPrice", product.price);
    MoEHelper.getInstance(context).trackEvent("productViewed", builder.build());
    MoEHelper.getInstance(context).setUserAttribute("lastViewedProductName", product.getTitle());
  }

  public static void trackLastCategoryView(Category category, Context context){
    MoEHelper.getInstance(context).setUserAttribute("lastViewedCategoryName", category.name);
    MoEHelper.getInstance(context).setUserAttribute("lastViewedCategoryDeepLink", "moengage://category?id="+ category.id);

  }
}
