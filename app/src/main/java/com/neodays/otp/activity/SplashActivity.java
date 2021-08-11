package com.neodays.otp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.neodays.otp.R;
import com.neodays.otp.config.CallBack;
import com.neodays.otp.config.MKFunction;
import com.neodays.otp.config.StringConfig;
import com.neodays.otp.service.NetworkManager;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

  private Activity mActivity;
  private Context  mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.activity_splash );

    mActivity = this;
    mContext  = this;

	new Handler().postDelayed(new Runnable() {
	  @Override
	  public void run() {
		init();
	  }
	}, 500);

  }

  private void init() {
	checkCreateNotification();
	checkLogin();
  }

  private void checkCreateNotification() {
	boolean isChannel = MKFunction.getBooleanPreference(mContext, getString( R.string.notification_pref ), getString( R.string.notification_key ) );
	if ( !isChannel ) {
	  MKFunction.createNotificationChannel(mContext, getString( R.string.channel ), getString( R.string.channel_name ),
			  getString( R.string.channel_description), getString( R.string.notification_pref), getString( R.string.notification_key ) );
	}
  }


  private void checkLogin() {
	boolean network = MKFunction.checkNetwork(mContext);
	if (!network) {
	  MKFunction.threadToast(mActivity, getString( R.string.errorNetworkCancel ) );
	  moveToLogin();
	} else {
	  String userId = MKFunction.getStringPreference(mContext, StringConfig.LOGIN_PREF, StringConfig.USER_ID).trim();

	  if ( userId.equals( StringConfig.TEST_ID_1 ) || userId.equals( StringConfig.TEST_ID_2 ) ) {
		deviceCb( userId );
	  } else {
	    moveToLogin();
	  }
	}
  }

  private void deviceCb(String userId) {
	CallBack callBack = new CallBack() {
	  String code = "";

	  @Override
	  public void start() {
		Log.e("Splash", "start");
	  }

	  @Override
	  public void error() {
		Log.e("Splash", "error");
	  }

	  @Override
	  public void result(String result) {
		code = MKFunction.returnCode(mContext, result);
		if ( code.equals("00") ) {
		  moveToMain();
		} else {
		  MKFunction.threadToast( mActivity, getString( R.string.errorDeviceUpdate) );
		  moveToLogin();
		}
	  }

	  @Override
	  public void end() {
		Log.e("Splash.end", code);
	  }

	};

	FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
	  @Override
	  public void onComplete(@NonNull Task<String> task) {
		if (!task.isSuccessful()) {
		  return;
		}

		String deviceKey = task.getResult();
		String mode = "update";

		Log.e("Token", "" + deviceKey);

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put( "userId", userId );
		hashMap.put( "deviceKey", deviceKey );

		NetworkManager networkManager = new NetworkManager(callBack, StringConfig.DEVICE_URL, hashMap );
		Thread thread = new Thread(networkManager);
		thread.start();

		thread = null;

	  }
	});
  }

  private void moveToLogin() {
    Intent intent = new Intent( mContext, LoginActivity.class );
    startActivity( intent );
    finish();
  }

  private void moveToMain() {
    Intent intent = new Intent( mContext, MainActivity.class );
    startActivity( intent );
    finish();
  }


}
