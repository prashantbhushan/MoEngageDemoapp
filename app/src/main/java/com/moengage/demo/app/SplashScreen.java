package com.moengage.demo.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.moe.pushlibrary.PayloadBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SplashScreen extends BaseActivity
    implements LoaderManager.LoaderCallbacks<ArrayList<Category>> {

  private static final String TAG = "MoEDA:SplashScreen";

  private ArrayList<Category> apiList  = null;
  boolean firstRun = false;
  View splashContent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    splashContent = findViewById(R.id.splashContent);
    final SharedPreferences pref = MoEngageDemoApp.getInstance().getSharedPreferences();

    loadingMessage.setText(R.string.txt_loading);

    findViewById(R.id.btnGetStarted).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        progress();
        //LISessionManager.getInstance(getApplicationContext())
        //    .init(SplashScreen.this, buildScope(), new AuthListener() {
        //          @Override public void onAuthSuccess() {
        //            // Authentication was successful.  You can now do
        //            // other calls with the SDK.
        //            AccessToken token = LISessionManager.getInstance(getApplicationContext())
        //                .getSession()
        //                .getAccessToken();
        //            pref.edit().putString("LI_ACCESS_TOKEN", token.getValue()).apply();
        //            pref.edit().putLong("LI_VALIDITY", token.getExpiresOn()).apply();
        //            progress();
        //          }
        //
        //          @Override public void onAuthError(LIAuthError error) {
        //            Toast.makeText(SplashScreen.this, "Failed to authenticate with LinkedIN",
        //                Toast.LENGTH_LONG).show();
        //          }
        //        }, true);
      }
    });

    firstRun = pref.getBoolean("FIRST_TIME_USE", true);
    if (firstRun) {
      pref.edit().putBoolean("FIRST_TIME_USE", false).apply();
      helper.setExistingUser(false);
    } else {
      splashContent.setVisibility(View.INVISIBLE);
      helper.setExistingUser(true);
    }

    getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);

    helper.trackEvent("lastAppUsed", new PayloadBuilder().putAttrDate("date", new Date()).build());
  }

  static final int INVENTORY_LOADER = 12345;

  private void progress(){
    Intent categoryIntent = new Intent(getApplicationContext(), LandingScreen.class);
    if( null != apiList){
      categoryIntent.putParcelableArrayListExtra(LandingScreen.EXTRA_CATEGORY_LIST, apiList);
    }
    startActivity(categoryIntent);
    finish();
  }

  @Override protected void onStart() {
    super.onStart();
    getSupportLoaderManager().getLoader(INVENTORY_LOADER).startLoading();
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

  /**
   * Called when a previously created loader has finished its load.  Note
   * that normally an application is <em>not</em> allowed to commit fragment
   * transactions while in this call, since it can happen after an
   * activity's state is saved.  See {@link FragmentManager#beginTransaction()
   * FragmentManager.openTransaction()} for further discussion on this.
   *
   * <p>This function is guaranteed to be called prior to the release of
   * the last data that was supplied for this Loader.  At this point
   * you should remove all use of the old data (since it will be released
   * soon), but should not do your own release of the data since its Loader
   * owns it and will take care of that.  The Loader will take care of
   * management of its data so you don't have to.  In particular:
   *
   * <ul>
   * <li> <p>The Loader will monitor for changes to the data, and report
   * them to you through new calls here.  You should not monitor the
   * data yourself.  For example, if the data is a {@link android.database.Cursor}
   * and you place it in a {@link android.widget.CursorAdapter}, use
   * the {@link android.widget.CursorAdapter#CursorAdapter(Context,
   * android.database.Cursor, int)} constructor <em>without</em> passing
   * in either {@link android.widget.CursorAdapter#FLAG_AUTO_REQUERY}
   * or {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
   * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
   * from doing its own observing of the Cursor, which is not needed since
   * when a change happens you will get a new Cursor throw another call
   * here.
   * <li> The Loader will release the data once it knows the application
   * is no longer using it.  For example, if the data is
   * a {@link android.database.Cursor} from a {@link  android.content.CursorLoader},
   * you should not call close() on it yourself.  If the Cursor is being placed in a
   * {@link android.widget.CursorAdapter}, you should use the
   * {@link android.widget.CursorAdapter#swapCursor(android.database.Cursor)}
   * method so that the old Cursor is not closed.
   * </ul>
   *
   * @param loader The Loader that has finished.
   * @param data The data generated by the Loader.
   */
  @Override public void onLoadFinished(Loader<ArrayList<Category>> loader,
      ArrayList<Category> data) {
    if (data != null && !data.isEmpty()) {
      //success
      apiList = data;
      if (!firstRun) {
        String token = MoEngageDemoApp.getInstance().getSharedPreferences().getString("LI_ACCESS_TOKEN", null);
        long validity = MoEngageDemoApp.getInstance().getSharedPreferences().getLong("LI_VALIDITY",-1);
        if( validity == -1 || validity < System.currentTimeMillis() || null == token){
          Log.d(TAG, "Loader finished with a failure response");
        }else{
          LISessionManager.getInstance(getApplicationContext()).init(new AccessToken(token, validity));
          progress();
        }
      }
    } else {
      loadingMessage.setText(R.string.text_api_failed);
      Log.d(TAG, "Loader finished with a failure response");
    }
    showProgress(false);
  }

  /**
   * Called when a previously created loader is being reset, and thus
   * making its data unavailable.  The application should at this point
   * remove any references it has to the Loader's data.
   *
   * @param loader The Loader that is being reset.
   */
  @Override public void onLoaderReset(Loader<ArrayList<Category>> loader) {
    showProgress(true);
  }

  @Override protected void showProgress(boolean state) {
    super.showProgress(state);
    if (state) {
      splashContent.setVisibility(View.INVISIBLE);
    } else {
      splashContent.setVisibility(View.VISIBLE);
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (getSupportLoaderManager().getLoader(INVENTORY_LOADER).isStarted()) {
      getSupportLoaderManager().getLoader(INVENTORY_LOADER).abandon();
    }
  }

  // Build the list of member permissions our LinkedIn session requires
  private static Scope buildScope() {
    return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
  }
}
