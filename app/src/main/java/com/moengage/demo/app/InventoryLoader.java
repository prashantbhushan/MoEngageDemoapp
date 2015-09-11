package com.moengage.demo.app;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.ArrayList;


public class InventoryLoader extends AsyncTaskLoader<ArrayList<Category>> {

  //private static final String TAG = "InventoryLoader";

  Context appContext;

  public InventoryLoader(Context context){
    super(context);
    appContext = context;
  }

  @Override public ArrayList<Category> loadInBackground() {
    return APIManager.getProductInventory(appContext);
  }

  @Override protected void onStartLoading() {
    //super.onStartLoading();
    forceLoad();
  }
}
