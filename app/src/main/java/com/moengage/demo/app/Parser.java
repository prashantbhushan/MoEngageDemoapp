package com.moengage.demo.app;

import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public final class Parser {

  private static final String TAG ="Parser";

  public static ArrayList<Category> getFeedAPIs(String response){
    if(TextUtils.isEmpty(response)){
      return null;
    }
    try{
      ArrayList<Category> apiListings = new ArrayList<>();
      JSONObject feedObject = new JSONObject(response);
      if(feedObject.has("apiGroups")){
        JSONObject apiGroups = feedObject.getJSONObject("apiGroups");
        if(apiGroups.has("affiliate")){
          JSONObject affilate = apiGroups.getJSONObject("affiliate");
          if(affilate.has("apiListings")){
            JSONObject listings = affilate.getJSONObject("apiListings");
            Set<Map.Entry<String, Category>> categoryMapping = MoEngageDemoApp.getInstance().categoryList.entrySet();
            for(Map.Entry<String, Category> entry: categoryMapping){
              if(listings.has(entry.getKey())){
                JSONObject variantContainer = listings.getJSONObject(entry.getKey());
                Category cat = entry.getValue();
                JSONObject variant = variantContainer.getJSONObject(ATTR_CATEGORY_VARIANT);
                JSONObject version = variant.getJSONObject("v0.1.0");
                cat.api = version.getString("get");
                apiListings.add(cat);
              }
            }
            return apiListings;
          }
        }
      }
    }catch(Exception e){
      Log.e(TAG, "Parser: getFeedAPIs", e);
    }
    return null;
  }


  public static ArrayList<Product> parseProductsByCategory(int catId, String response){
    try{
      JSONObject productList = new JSONObject(response);
      if(productList.has(ATTR_PRODUCT_LIST)){
        ArrayList<Product> list = new ArrayList<>();
        JSONArray infoList = productList.getJSONArray(ATTR_PRODUCT_LIST);
        int infoLength = infoList.length();
        for(int i = 0; i< infoLength ; i++){
          Product product = new Product();
          product.categoryId = catId;
          JSONObject baseProduct = infoList.getJSONObject(i);
          if(baseProduct.has(ATTR_PRODUCT_BASE_INFO)){
            JSONObject baseInfo = baseProduct.getJSONObject(ATTR_PRODUCT_BASE_INFO);
            if(baseInfo.has(ATTR_PRODUCT_IDENTIFIER)){
              JSONObject identifier = baseInfo.getJSONObject(ATTR_PRODUCT_IDENTIFIER);
              product.productId = identifier.getString(ATTR_PRODUCT_ID);
            }
            if(baseInfo.has(ATTR_PRODUCT_ATTRS)){
              JSONObject productAttrs = baseInfo.getJSONObject(ATTR_PRODUCT_ATTRS);
              if(productAttrs.has(ATTR_PRODUCT_TITLE)){
                product.title = productAttrs.getString(ATTR_PRODUCT_TITLE);
              }
              if(productAttrs.has(ATTR_PRODUCT_DESC)){
                product.description = productAttrs.getString(ATTR_PRODUCT_DESC);
              }
              if(productAttrs.has(ATTR_PRODUCT_IMAGEURL_LIST)){
                JSONObject imageUrls = productAttrs.getJSONObject(ATTR_PRODUCT_IMAGEURL_LIST);
                product.imageUrl = imageUrls.optString(ATTR_PRODUCT_IMAGEURL1);
                if( null == product.imageUrl){
                  product.imageUrl = imageUrls.optString(ATTR_PRODUCT_IMAGEURL2);
                }
                if( null == product.imageUrl){
                  product.imageUrl = imageUrls.optString(ATTR_PRODUCT_IMAGEURL3);
                }
              }
              if(productAttrs.has(ATTR_PRODUCT_SELLINGPRICE)){
                JSONObject sellingPrice = productAttrs.getJSONObject(ATTR_PRODUCT_SELLINGPRICE);
                product.currency = sellingPrice.getString(ATTR_PRODUCT_CURRENCY);
                product.price = sellingPrice.getDouble(ATTR_PRODUCT_AMOUNT);
              }
              if(productAttrs.has(ATTR_PRODUCT_BRAND)){
                product.brand = productAttrs.getString(ATTR_PRODUCT_BRAND);
              }
              if(productAttrs.has(ATTR_PRODUCT_INSTOCK)){
                product.inStock = productAttrs.getBoolean(ATTR_PRODUCT_INSTOCK);
              }
            }
            list.add(product);
          }
        }
        return list;
      }
    }catch(Exception e){
      Log.e(TAG, "PARSER: parseProducts", e);
    }
    return null;
  }


  private static final String ATTR_PRODUCT_LIST = "productInfoList";
  private static final String ATTR_PRODUCT_BASE_INFO = "productBaseInfo";
  private static final String ATTR_PRODUCT_IDENTIFIER = "productIdentifier";
  private static final String ATTR_PRODUCT_ID = "productId";
  private static final String ATTR_PRODUCT_ATTRS = "productAttributes";
  private static final String ATTR_PRODUCT_TITLE = "title";
  private static final String ATTR_PRODUCT_DESC = "productDescription";
  private static final String ATTR_PRODUCT_IMAGEURL_LIST = "imageUrls";
  private static final String ATTR_PRODUCT_IMAGEURL1 = "275x275";
  private static final String ATTR_PRODUCT_IMAGEURL2 = "400x400";
  private static final String ATTR_PRODUCT_IMAGEURL3 ="unknown";
  private static final String ATTR_PRODUCT_SELLINGPRICE = "sellingPrice";
  private static final String ATTR_PRODUCT_CURRENCY = "currency";
  private static final String ATTR_PRODUCT_AMOUNT = "amount";
  private static final String ATTR_PRODUCT_BRAND = "productBrand";
  private static final String ATTR_PRODUCT_INSTOCK = "inStock";

  private static final String ATTR_CATEGORY_VARIANT = "availableVariants";


}
