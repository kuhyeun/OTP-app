package com.neodays.otp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.neodays.otp.R;
import com.neodays.otp.config.CallBack;
import com.neodays.otp.config.MKFunction;
import com.neodays.otp.config.StringConfig;
import com.neodays.otp.service.NetworkManager;
import com.neodays.otp.webview.MainWebView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

  private WebView      webView;
  private Activity     mActivity;
  private Context      mContext;
  private Toolbar      toolbar;
  private TextView     toolbarTitle;
  private LinearLayout emptyLayout;
  private ProgressBar  progressBar;

  private boolean      request;
  private String       confirm, deviceKey;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater menuInflater = getMenuInflater();
	menuInflater.inflate(R.menu.main_menu, menu);
	return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	  case R.id.setting:
		Intent intent = new Intent(mContext, SettingActivity.class);
		startActivity( intent );
		return true;
	}
	return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	mActivity = this;
	mContext  = this;

	init();
  }

  private void init() {
	setMapping();
	setAppbar();
	setRequest( "" );
	setBroadcast();
  }

  private void setMapping() {
	webView         = (WebView)      mActivity.findViewById( R.id.webView );
	toolbar         = (Toolbar)      mActivity.findViewById( R.id.mainToolbar );
	toolbarTitle    = (TextView)     mActivity.findViewById( R.id.toolbarTitle );
	emptyLayout     = (LinearLayout) mActivity.findViewById( R.id.emptyLayout );
	progressBar     = (ProgressBar)  mActivity.findViewById( R.id.webViewProgress );
  }

  private void setAppbar() {
	setSupportActionBar( toolbar );
	setTitle( "" );
	toolbarTitle.setText( getString( R.string.menu_title ) );
  }

  private void setRequest(String mode) {
	request = MKFunction.getBooleanPreference( mContext, StringConfig.REQUEST_PREF, StringConfig.REQUEST_KEY );
	deviceKey = MKFunction.getStringPreference( mContext, StringConfig.OTP_PREF, StringConfig.OTP_KEY );
	if ( request ) {
	  showWebView();
	  setWebView( mode );
	} else {
	  hideWebView();
	}
  }

  private void setWebView(String mode) {
	MainWebView mkWebView = new MainWebView( mContext, webView );

	webView = mkWebView.getWebView();
	webView.setWebViewClient( new CustomWebViewClient() );

	if( mkWebView.getNetworkCheck() ) {
	  loadWebView( mode );
	} else {
	  MKFunction.customToast( mContext, getString( R.string.errorNetworkCancel ) );
	}
  }

  private void loadWebView(String mode) {
    switch( mode ) {
	  case "SUCCESS":
	    Log.e("load", "mode = " + mode );
	    webView.loadUrl( StringConfig.RESULT_SUCCESS );
	    webView.reload();
	    clearPreference();
	    break;
	  case "FAILED":
		Log.e("load", "mode = " + mode );
	    webView.loadUrl( StringConfig.RESULT_FAILED );
	    webView.reload();
	    clearPreference();
	    break;
	  default:
		String params = "auth_num=" + deviceKey;
		Log.e( "parameter", " " + params );
		webView.postUrl( StringConfig.TIMER_URL, params.getBytes() );
		break;
	}
  }

  private void setBroadcast() {
	LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, new IntentFilter(StringConfig.KEY_CONFIRM));
  }

  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
	  confirm = intent.getStringExtra( "confirm");
	  deviceKey = MKFunction.getStringPreference( mContext, StringConfig.OTP_PREF, StringConfig.OTP_KEY );
	  Log.e("BroadCast : ", "receive = " + confirm );
	  if( confirm.equals( "otp" ) ) {
		setRequest( confirm );
	  } else {
	    loadWebView( confirm );
	  }
	}
  };

  private class CustomWebViewClient extends WebViewClient {
	@Override
	public void onPageStarted(WebView webview, String url, Bitmap favicon) {
	  webview.setVisibility( View.INVISIBLE );
	  Log.e("pageStart", "" + url );
	  showProgress();
	}

	@Override
	public void onPageFinished(WebView webview, String url) {
	  super.onPageFinished(webview, url);
	  Log.e("pageFinish", "" + url );
	  hideProgress();
	  webview.setVisibility( View.VISIBLE );
	}
  }

  private void showWebView() {
    runOnUiThread(new Runnable() {
	  @Override
	  public void run() {
	    emptyLayout.setVisibility( View.GONE );
		webView.setVisibility( View.VISIBLE );
	  }
	});
  }

  private void hideWebView() {
    runOnUiThread(new Runnable() {
	  @Override
	  public void run() {
		webView.setVisibility( View.GONE );
		emptyLayout.setVisibility( View.VISIBLE );
	  }
	});
  }

  private void showProgress() {
	runOnUiThread(new Runnable() {
	  @Override
	  public void run() {
		progressBar.setVisibility( View.VISIBLE );
	  }
	});
  }

  private void hideProgress() {
    runOnUiThread(new Runnable() {
      @Override
	  public void run() {
		progressBar.setVisibility( View.GONE );
	  }
	});
  }

  private void clearPreference() {
    MKFunction.deletePreference( mContext, StringConfig.REQUEST_PREF );
  }

  @Override
  protected void onDestroy() {
	super.onDestroy();

	if( webView != null ) {
	  webView.destroy();
	}
  }
}