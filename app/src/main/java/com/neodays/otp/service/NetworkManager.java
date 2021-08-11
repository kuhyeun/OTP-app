package com.neodays.otp.service;

import android.util.Log;

import com.neodays.otp.config.CallBack;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class NetworkManager implements Runnable {

  private CallBack callBack;
  private String        neoUrl;

  HashMap<String, String> hash = new HashMap<String, String>();

  public NetworkManager(CallBack cb, String url, HashMap<String, String> hashMap) {
	this.callBack = cb;
	this.neoUrl   = url;
	this.hash     = hashMap;
  }

  @Override
  public void run() {

	if (callBack != null) {
	  this.callBack.start();
	}

	HttpConnection httpConnection = new HttpConnection(neoUrl, hash);
	String result = "";

	try {
	  result = httpConnection.execute().get();
	  Log.e( "DeviceManager", " result : " + result );
	} catch (ExecutionException | InterruptedException e) {
	  e.printStackTrace();
	  Log.e("DeviceManager", "" + e);
	  if (this.callBack != null) {
		this.callBack.error();
	  }
	}

	if (this.callBack != null) {
	  this.callBack.result(result);
	}

	if (callBack != null) {
	  callBack.end();
	}

  }
}
