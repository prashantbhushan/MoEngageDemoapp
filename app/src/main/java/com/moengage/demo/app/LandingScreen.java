package com.moengage.demo.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.util.ArrayList;

public class LandingScreen extends BaseActivity implements LoaderManager.LoaderCallbacks<ArrayList<Category>>, NavigationView.OnNavigationItemSelectedListener,
    CategoryListingFragment.OnClickListener, ProductListingFragment.OnClickListener, ProgressCallbacks {

  private static final String TAG = "CategoryLandingScreen";
  static final String EXTRA_CATEGORY_LIST = "categorylist";

  private static final long DRAWER_CLOSE_DELAY_MS = 350;

  private ActionBarDrawerToggle mDrawerToggle;
  ArrayList<Category> categoryArrayList;
  DrawerLayout mDrawerLayout;
  int mNavItemId;
  NavigationView navigationView;
  private final Handler mDrawerActionHandler = new Handler();

  private static final String NAV_ITEM_ID = "navItemId";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_category_landing);

    //String url = "https://api.linkedin.com/v1/people/~";
    //
    //APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
    //apiHelper.getRequest(this, url, new ApiListener() {
    //  @Override public void onApiSuccess(ApiResponse apiResponse) {
    //    try{
    //      JSONObject resp = apiResponse.getResponseDataAsJson();
    //      helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_USER_FIRST_NAME, resp.getString("firstName"));
    //      helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_USER_LAST_NAME, resp.getString("lastName"));
    //      helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_UNIQUE_ID, resp.getString("id"));
    //
    //      JSONObject profileReq = resp.getJSONObject("siteStandardProfileRequest");
    //      helper.setUserAttribute("LIN_PROFILE_URL", profileReq.getString("url"));
    //    }catch(Exception e){
    //      Log.e("LandingScreen", "APIHelper", e);
    //    }
    //
    //  }
    //
    //  @Override public void onApiError(LIApiError liApiError) {
    //    // Error making GET request!
    //    Log.d(TAG, "response:"+ liApiError.getMessage());
    //    helper.trackEvent("dataFetchFailed", new PayloadBuilder().putAttrDate("attemptedOn", new Date()).build());
    //  }
    //});

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    if (null == savedInstanceState) {
      mNavItemId = R.id.drawer_home;
    } else {
      mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
    }

    // listen for navigation events
    navigationView = (NavigationView) findViewById(R.id.navigation);
    navigationView.setNavigationItemSelectedListener(this);

    // select the correct nav menu item
    navigationView.getMenu().findItem(mNavItemId).setChecked(true);

    // set up the hamburger icon to open and close the drawer
    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,  R.string.close);
    mDrawerLayout.setDrawerListener(mDrawerToggle);
    mDrawerToggle.syncState();

    navigate(mNavItemId);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if(null != extras && extras.containsKey(EXTRA_CATEGORY_LIST)) {
      categoryArrayList = extras.getParcelableArrayList(EXTRA_CATEGORY_LIST);
      showCategories(categoryArrayList);
      showProgress(false);
    }else if(null != categoryArrayList){
      showCategories(categoryArrayList);
      showProgress(false);
    }else{
      getSupportLoaderManager().initLoader(SplashScreen.INVENTORY_LOADER, null, this);
      getSupportLoaderManager().getLoader(SplashScreen.INVENTORY_LOADER).startLoading();
      showProgress(true);
    }

  }

  private boolean categoriesNeedTobeSet = true;

  private static final String TAG_CATLIST = "catlist";

  private void showCategories(final ArrayList<Category> categoryArrayList){
    if(categoriesNeedTobeSet){
      if( null != categoryArrayList) {
        categoriesNeedTobeSet = false;
        Log.d(TAG, "showCategories - called");
        Menu m = navigationView.getMenu();
        SubMenu productListing = m.addSubMenu("Categories");

        for(Category cat: categoryArrayList){
          productListing.add(1, cat.id, cat.id + 1, cat.name);
        }
        MenuItem mi = m.getItem(m.size()-1);
        mi.setTitle(mi.getTitle());
      }
    }

    mDrawerActionHandler.postDelayed(new Runnable() {
      @Override public void run() {
        CategoryListingFragment fragment = new CategoryListingFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList(LandingScreen.EXTRA_CATEGORY_LIST, categoryArrayList);
        fragment.setArguments(data);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, fragment, TAG_PRODUCT_FRAG)
            .addToBackStack(null)
            .commit();
      }
    }, 300);


  }
  /**
   * Instantiate and return a new Loader for the given ID.
   *
   * @param id The ID whose loader is to be created.
   * @param args Any arguments supplied by the caller.
   * @return Return a new Loader instance that is ready to start loading.
   */
  @Override public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
    return new InventoryLoader(getApplicationContext());
  }


  @Override public void onLoadFinished(Loader<ArrayList<Category>> loader,
      ArrayList<Category> data) {
    if( data != null && !data.isEmpty()) {
      categoryArrayList = data;
      showCategories(data);

      Intent intent = getIntent();
      Uri uri = intent.getData();
      if( null != uri ){
        final String id = uri.getQueryParameter("id");
        if( null != id ){
          mDrawerActionHandler.postDelayed(new Runnable() {
            @Override public void run() {
              onCategoryClick(MoEngageDemoApp.getInstance().getCategoryById(Integer.parseInt(id)));
            }
          }, 300);
          return;
        }
      }
      Log.d(TAG, "Loader finished with a success response");
    }else{
      //failure
      showErrorMessage();
      Toast.makeText(this, R.string.text_api_failed, Toast.LENGTH_LONG).show();
      Log.d(TAG, "Loader finished with a failure response");
    }
    showProgress(false);
  }


  @Override public void onLoaderReset(Loader<ArrayList<Category>> loader) {
    showProgress(true);
  }

  @Override protected void showProgress(boolean state) {
    super.showProgress(state);
  }

  private static final String TAG_PRODUCT_FRAG = "prod";
  private static final String TAG_CART = "cart";
  private void navigate(final int itemId) {
    switch (itemId) {
      case R.id.drawer_home:
        //already there
        showCategories(categoryArrayList);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        break;
      case R.id.drawer_orders:
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, new OrderSuccess(), TAG_CART).addToBackStack(null).commit();
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case R.id.drawer_cart:
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, new CartFragment(), TAG_CART).addToBackStack(null).commit();
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_CAMERA :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_CAMERA);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_CAMERA_ACC :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_CAMERA_ACC);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_FURNITURE :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_FURNITURE);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_LAPTOP :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_LAPTOP);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_LAPTOP_ACC :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_LAPTOP_ACC);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_MOBILE :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_MOBILE);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      case MoEngageDemoApp.ID_CAT_MOBILE_ACC :
        findOrResetFragment(TAG_PRODUCT_FRAG, MoEngageDemoApp.KEY_CAT_MOBILE_ACC);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        break;
      default:
        // ignore

        break;
    }
  }

  private void findOrResetFragment(String TAG, String KEY){
    showProgress(true);
    Fragment frag = getSupportFragmentManager().findFragmentByTag(TAG);
    if( null == MoEngageDemoApp.getInstance().categoryList || MoEngageDemoApp.getInstance().categoryList
        .size() == 0) {
      getSupportLoaderManager().initLoader(SplashScreen.INVENTORY_LOADER, null, this);
      getSupportLoaderManager().getLoader(SplashScreen.INVENTORY_LOADER).startLoading();
      showProgress(true);
      return;
    }
    MoEngageDemoApp.getInstance().categoryInView = MoEngageDemoApp.getInstance().categoryList.get(KEY);
    if( null != frag && frag instanceof ProductListingFragment){
      level++;
      ProductListingFragment listFrag = (ProductListingFragment)frag;
      listFrag.reset();
    }else{
      level++;
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.content, new ProductListingFragment(), TAG).addToBackStack(null).commit();
    }
  }

  /**
   * Handles clicks on the navigation menu.
   */
  @Override
  public boolean onNavigationItemSelected(final MenuItem menuItem) {
    // update highlighted item in the navigation menu
    menuItem.setChecked(true);
    mNavItemId = menuItem.getItemId();

    // allow some time after closing the drawer before performing real navigation
    // so the user can see what is happening
    mDrawerLayout.closeDrawer(GravityCompat.START);
    mDrawerActionHandler.postDelayed(new Runnable() {
      @Override public void run() {
        navigate(menuItem.getItemId());
      }
    }, DRAWER_CLOSE_DELAY_MS);
    return true;
  }

  @Override
  public void onConfigurationChanged(final Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
      return mDrawerToggle.onOptionsItemSelected(item);
    }
    return super.onOptionsItemSelected(item);
  }

  int level = 0;
  @Override
  public void onBackPressed() {
    level--;
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      if(level == 0){
        mDrawerToggle.setDrawerIndicatorEnabled(true);
      }
      hideProgressBar();
      super.onBackPressed();
    }
  }

  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(NAV_ITEM_ID, mNavItemId);
  }

  @Override public void onCategoryClick(Category category) {
    navigate(category.id);
  }

  @Override public void onProductClick(Product product, int type) {
    if( type == 3){
      navigate(R.id.drawer_cart);
    }else if( type == 4){
      navigate(R.id.drawer_orders);
      Toast.makeText(this, "Order placed successfully", Toast.LENGTH_LONG).show();
    }
  }

  @Override public void onProductRemoved(Product product) {
    onBackPressed();
    Toast.makeText(this, "Product removed from cart", Toast.LENGTH_LONG).show();
  }

  @Override public void viewProductDetail(Product product) {
    ProductDetailFragment frag = new ProductDetailFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable("PRODUCT",product);
    frag.setArguments(bundle);
    level++;
    getSupportFragmentManager().beginTransaction()
          .replace(R.id.content, frag, TAG).addToBackStack(null).commit();
  }

  @Override public void showProgressBar() {
    showProgress(true);
  }

  @Override public void hideProgressBar() {
    showProgress(false);
  }

  @Override public void showErrorMessage() {
    Toast.makeText(this, R.string.text_api_failed, Toast.LENGTH_LONG).show();
    loadingMessage.setText(R.string.text_api_failed);
  }
}
