package com.moengage.demo.app;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import java.util.ArrayList;

public class CategoryLoader extends AsyncTaskLoader<ArrayList<Product>> {

  Context appContext;

  public CategoryLoader(Context context){
    super(context);
    appContext = context;
  }
  /**
   */
  @Override public ArrayList<Product> loadInBackground() {
    return APIManager.fetchProductsByCategory(MoEngageDemoApp.getInstance().categoryInView, appContext);
  }

  @Override protected void onStartLoading() {
    //super.onStartLoading();
    forceLoad();
  }
}
