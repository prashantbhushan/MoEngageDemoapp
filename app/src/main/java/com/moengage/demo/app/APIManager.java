package com.moengage.demo.app;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public final class APIManager {

  private static final String TAG = "APIManager";
  private static final String INVENTORY_URI = "";

  private static final String FEED_PRODUCTS_API = "https://affiliate-api.flipkart.net/affiliate/api/6d35ca7a886041a098675cdcfe4079af.json";
  private static final String FEED_TOP_OFFERS_API = "https://affiliate-api.flipkart.net/affiliate/offers/v1/top/json";
  private static final String FEED_DEALS_OF_THE_DAY_API = "https://affiliate-api.flipkart.net/affiliate/offers/v1/dotd/json";
  private static final String FEED_SEARCH_API = "https://affiliate-api.flipkart.net/affiliate/search/json?query=%s&resultCount=%d";

  public static HashMap<String, String> getFlipkartHeaders(Context context){
    String fkaid = context.getString(R.string.fk_aid);
    String token = context.getString(R.string.fk_token);

    HashMap<String, String> headers = new HashMap<>();
    headers.put(HEADER_FK_AID, fkaid);
    headers.put(HEADER_FK_TOKEN, token);

    return headers;
  }

  public static ArrayList<Category> getProductInventory(Context appContext) {
    String response = executeHTTPSGETRequest(FEED_PRODUCTS_API, getFlipkartHeaders(appContext));
    return Parser.getFeedAPIs(response);
  }

  public static ArrayList<Product> fetchProducts(ArrayList<Category> apilistings, Context appContext){
    HashMap<String, String> headers = getFlipkartHeaders(appContext);
    ArrayList<Product> allProducts = new ArrayList<>();
    for(Category cat : apilistings){
      String response = executeHTTPSGETRequest(cat.api, headers);
      ArrayList<Product> products = Parser.parseProductsByCategory(cat.id, response);
      if( null != products){
        allProducts.addAll(products);
      }
    }
    return allProducts;
  }

  public static ArrayList<Product> fetchProductsByCategory(Category category, Context appContext){
    HashMap<String, String> headers = getFlipkartHeaders(appContext);
    String response = executeHTTPSGETRequest(category.api, headers);
    return Parser.parseProductsByCategory(category.id, response);
  }

  private static final String HEADER_FK_AID = "Fk-Affiliate-Id";
  private static final String HEADER_FK_TOKEN = "Fk-Affiliate-Token";


  /*
   * Following the solution provided by Alok Gupta & Guido García
   * http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https/6378872#6378872
   *
   */
  private static void trustEveryone() {
    try {
      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }});
      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, new X509TrustManager[]{new X509TrustManager(){
        public void checkClientTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {}
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }}}, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(
          context.getSocketFactory());
    } catch (Exception e) { // should never happen
      if(BuildConfig.DEBUG)Log.e(TAG, "APIManager: trustEveryone: error while trusting everyone ", e);
    }
  }


  public static String executeHTTPSGETRequest(final String URI, HashMap<String, String> headers){
    try {

      Log.d(TAG, "APIManager:executeHTTPSGETRequest Hitting API: "+URI);
      trustEveryone();

      URL url = new URL(URI);

      HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
      Set<Map.Entry<String, String>> keyValueMap = headers.entrySet();
      for(Map.Entry<String, String> entry: keyValueMap){
        urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
      }

      if( 200 != urlConnection.getResponseCode()){
        Log.d(TAG, "Response: API Failed: "+ URI);
        return null;
      }
      InputStream in = urlConnection.getInputStream();
      String resp = convertStreamToString(in);
      //if(BuildConfig.DEBUG){
        Log.d(TAG, "Response: "+ resp);
      //}
      return resp;
    } catch (IOException e) {
      if(BuildConfig.DEBUG)Log.e(TAG, "error while fetching "+ URI, e);
    }
    return null;
  }

  /**
   * Convert input stream to String
   *
   * @param inputStream
   *            The input stream from the API response entity
   * @return String representation of the API response mStringBody
   */
  private static String convertStreamToString(InputStream inputStream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        inputStream));
    StringBuilder sb = new StringBuilder();

    String line;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      Log.e(TAG, "APIManager:executeRequest: IOException", e);
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        Log.e(TAG, "APIManager:executeRequest: IOException", e);
      }
    }
    return sb.toString();
  }

}
