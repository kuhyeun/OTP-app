package com.neodays.otp.webview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.neodays.otp.R;
import com.neodays.otp.config.MKFunction;

public class MainWebView {
  private Context mContext;
  private android.webkit.WebView webView;
  private boolean network = false;

  public MainWebView(Context context, android.webkit.WebView webView ) {
	this.mContext = context;
	this.webView  = webView;

	setNetworkCheck();
	setSetting();
	setConsoleLog();
  }

  private void setNetworkCheck() {
	network = MKFunction.checkNetwork( mContext );

	if( !network ) {
	  webView.loadData( MKFunction.getString( mContext, R.string.errorNetworkCancel ), "text/html; charset=UTF-8", null );

	  new AlertDialog.Builder( mContext )
			  .setCancelable( false )
			  .setMessage( MKFunction.getString( mContext, R.string.errorNetworkCancel ) )
			  .setNegativeButton(
					  MKFunction.getString( mContext, R.string.buttonClose ),
					  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						  dialog.cancel();
						}
					  })
			  .show();
	}
  }

  public void setSetting() {
	WebSettings webSettings = webView.getSettings();
	webSettings.setJavaScriptEnabled(true);
	webSettings.setLoadsImagesAutomatically(true);
	webSettings.setSupportZoom(false);
	webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
	android.webkit.WebView.setWebContentsDebuggingEnabled( true );

	webView.clearCache( true );
  }

  public android.webkit.WebView getWebView() {
	return webView;
  }

  public boolean getNetworkCheck() {
	return network;
  }

  private void setConsoleLog() {
	webView.setWebViewClient(new WebViewClient(){
	  public boolean onConsoleMessage(ConsoleMessage cm) {
		Log.e("MyApplication", cm.message() + " -- From line "
				+ cm.lineNumber() + " of "
				+ cm.sourceId() );
		return true;
	  }
	});
  }
}
