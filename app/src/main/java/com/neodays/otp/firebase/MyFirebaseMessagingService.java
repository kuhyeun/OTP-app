package com.neodays.otp.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.neodays.otp.R;
import com.neodays.otp.activity.SplashActivity;
import com.neodays.otp.config.MKFunction;
import com.neodays.otp.config.StringConfig;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
  Context mContext = this;
  String  channel = "";
  int mCode;

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

	Log.e( "message receive", " " + remoteMessage );

	String key    = remoteMessage.getData().get("key");
	String sender = remoteMessage.getData().get("title");
	String mode   = remoteMessage.getData().get("mode");

	channel = getString( R.string.channel );
	mCode = MKFunction.makeMillTime();

	if( mode.equals( "otp" ) ) {
	  MKFunction.setBooleanPreference( mContext, StringConfig.REQUEST_PREF, StringConfig.REQUEST_KEY, true );
	  MKFunction.setStringPreference( mContext, StringConfig.OTP_PREF, StringConfig.OTP_KEY, key );
	  setNotification(key, sender, mCode);
	  sendBroadcast( key );
	} else {
	  sendConfirmBroadcast( mode );
	}

  }

  private void setNotification(String key, String title, int code) {
	Intent intent = new Intent(mContext, SplashActivity.class);
	intent.putExtra("NOTIFICATION", "create");

	PendingIntent pendingIntent = PendingIntent.getActivity(mContext, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);

	NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channel);
	builder.setAutoCancel(true)
			.setSmallIcon(R.drawable.ic_baseline_lock_24)
			.setDefaults(Notification.DEFAULT_LIGHTS)
			.setWhen(System.currentTimeMillis())
			.setContentIntent(pendingIntent)
			.setContentTitle(title)
			.setContentText(key);

	mManager.notify(code, builder.build());
  }

  @Override
  public void onNewToken(String s) {
	super.onNewToken(s);
  }

  private void sendBroadcast(String key) {
	Intent intent = new Intent( StringConfig.KEY_CONFIRM );
	intent.putExtra( "confirm", "otp" );
	intent.putExtra( "key", key );
	LocalBroadcastManager.getInstance( mContext ).sendBroadcast( intent );
  }

  private void sendConfirmBroadcast(String check) {
    Intent intent = new Intent( StringConfig.KEY_CONFIRM );
    intent.putExtra( "confirm", check );
    LocalBroadcastManager.getInstance( mContext ).sendBroadcast( intent );
  }
}