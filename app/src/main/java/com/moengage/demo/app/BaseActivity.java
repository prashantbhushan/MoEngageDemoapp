package com.moengage.demo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.moe.pushlibrary.MoEHelper;


public class BaseActivity  extends AppCompatActivity {

  protected  MoEHelper helper;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    helper = new MoEHelper(this);

  }

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    progressView = findViewById(R.id.progressView);
    loadingMessage = (TextView) findViewById(R.id.progressText);
  }

  @Override protected void onStart() {
    super.onStart();
    helper.onStart(this);
  }

  @Override protected void onStop() {
    super.onStop();
    helper.onStop(this);
  }

  @Override protected void onPause() {
    super.onPause();
    helper.onPause(this);
  }

  @Override protected void onResume() {
    super.onResume();
    helper.onResume(this);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    helper.onSaveInstanceState(outState);
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    helper.onRestoreInstanceState(savedInstanceState);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    helper.onNewIntent(this, intent);
  }

  protected View progressView;
  protected TextView loadingMessage;
  protected void showProgress(boolean state){
    if( null == progressView)return;
    if( state ){
      progressView.setVisibility(View.VISIBLE);
      progressView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          //DO nothing
        }
      });
    }else{
      progressView.setVisibility(View.GONE);
      progressView.setOnClickListener(null);
    }
  }

  protected void changeLoadingText(String message){
    if( null != loadingMessage){
      loadingMessage.setText(message);
    }
  }



}