package com.moengage.demo.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.moe.pushlibrary.PayloadBuilder;
import com.moe.pushlibrary.utils.MoEHelperConstants;
import java.util.ArrayList;
import java.util.Date;

public class SplashScreen extends BaseActivity
    implements LoaderManager.LoaderCallbacks<ArrayList<Category>>, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener {

  private static final String TAG = "MoEDA:SplashScreen";

  private ArrayList<Category> apiList  = null;
  boolean firstRun = false;
  View splashContent;

  /* Is there a ConnectionResult resolution in loginSuccessful? */
  private boolean mIsResolving = false;

  /* Should we automatically resolve ConnectionResults when possible? */
  private boolean mShouldResolve = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    splashContent = findViewById(R.id.splashContent);
    final SharedPreferences pref = MoEngageDemoApp.getInstance().getSharedPreferences();

    loadingMessage.setText(R.string.txt_loading);

    // Build GoogleApiClient with access to basic profile
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(new Scope(Scopes.PROFILE))
        .build();

    findViewById(R.id.sign_in_button).setOnClickListener(this);


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

  private void loginSuccessful(int loginSource){
    Intent categoryIntent = new Intent(getApplicationContext(), LandingScreen.class);
    if( null != apiList){
      categoryIntent.putParcelableArrayListExtra(LandingScreen.EXTRA_CATEGORY_LIST, apiList);
    }
    categoryIntent.putExtra("LOGIN_SOURCE", loginSource);
    startActivity(categoryIntent);
    finish();
  }

  @Override protected void onStart() {
    super.onStart();
    getSupportLoaderManager().getLoader(INVENTORY_LOADER).startLoading();
    mGoogleApiClient.connect();
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
    mGoogleApiClient.disconnect();
  }



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

    if (requestCode == RC_SIGN_IN) {
      // If the error resolution was not successful we should not resolve further.
      if (resultCode != RESULT_OK) {
        mShouldResolve = false;
      }

      mIsResolving = false;
      mGoogleApiClient.connect();
    }

  }

  /* Request code used to invoke sign in user interactions. */
  private static final int RC_SIGN_IN = 0;

  /* Client used to interact with Google APIs. */
  private GoogleApiClient mGoogleApiClient;

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.sign_in_button) {
      onSignInClicked();
    }

  }

  private void onSignInClicked() {
    // User clicked the sign-in button, so begin the sign-in process and automatically
    // attempt to resolve any errors that occur.
    mShouldResolve = true;
    mGoogleApiClient.connect();
    changeLoadingText("Attempt Login..");
    showProgress(true);
  }



  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    // Could not connect to Google Play Services.  The user needs to select an account,
    // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
    // ConnectionResult to see possible error codes.
    Log.d(TAG, "onConnectionFailed:" + connectionResult);

    if (!mIsResolving && mShouldResolve) {
      if (connectionResult.hasResolution()) {
        try {
          connectionResult.startResolutionForResult(this, RC_SIGN_IN);
          mIsResolving = true;
        } catch (IntentSender.SendIntentException e) {
          Log.e(TAG, "Could not resolve ConnectionResult.", e);
          mIsResolving = false;
          mGoogleApiClient.connect();
        }
      } else {
        // Could not resolve the connection result, show the user an
        // error dialog.
        Toast.makeText(SplashScreen.this, "Failed to resolve a connection with Google+",
            Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    // onConnected indicates that an account was selected on the device, that the selected
    // account has granted any requested permissions to our app and that we were able to
    // establish a service connection to Google Play services.
    Log.d(TAG, "onConnected:" + bundle);
    mShouldResolve = false;

    if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null && !MoEngageDemoApp.getInstance().userIsLoggedIn()) {
      MoEngageDemoApp.getInstance().setUserLoggedIn();
      Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
      String personName = currentPerson.getDisplayName();

      String personPhoto = currentPerson.getImage().getUrl();
      String personGooglePlusProfile = currentPerson.getUrl();

      helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_USER_NAME, personName);
      helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_USER_GENDER, currentPerson.getGender() == 0 ? MoEHelperConstants.GENDER_MALE : MoEHelperConstants.GENDER_FEMALE);
      helper.setUserAttribute("ProfilePic", personPhoto);
      helper.setUserAttribute("GooglePlusProfile", personGooglePlusProfile);

      String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
      helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_USER_EMAIL, email);

    }
    // Show the signed-in UI
    loginSuccessful(2);
  }

  @Override public void onConnectionSuspended(int i) {

  }

  private void onSignOutClicked() {
    // Clear the default account so that GoogleApiClient will not automatically
    // connect in the future.
    if (mGoogleApiClient.isConnected()) {
      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
      mGoogleApiClient.disconnect();
    }
  }


}
