package com.neodays.otp.config;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MKFunction {
  public static boolean checkWifi(Context context) {
	boolean rtn = false;

	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();

	if (ni != null) {
	  if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
		rtn = ni.isConnected();
	  }
	}

	return rtn;
  }

  public static boolean checkLTE(Context context) {
	boolean rtn = false;

	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();

	if (ni != null) {
	  if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
		rtn = ni.isConnected();
	  }
	}

	return rtn;
  }

  public static boolean checkNetwork(Context context) {
	boolean rtn = false;

	if (checkLTE(context) || checkWifi(context)) {
	  rtn = true;
	} else {
	  rtn = false;
	}

	return rtn;
  }

  public static void customToast(Context context, String msg) {
	try {
	  Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
	  toast.show();
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  public static void threadToast(Activity mActivity, String con) {
	final String content = con;
	final Context mContext = mActivity.getApplicationContext();
	mActivity.runOnUiThread(new Runnable() {
	  @Override
	  public void run() {
		MKFunction.customToast(mContext, content);
	  }
	});
  }

  public static ArrayList<HashMap<String, String>> parseJSON(Context context, String jsonString) {
	ArrayList<HashMap<String, String>> rtn = new ArrayList<HashMap<String, String>>();

	try {
	  JSONArray array = new JSONArray(jsonString);

	  for (int i = 0; i < array.length(); i++) {
		HashMap<String, String> map = new HashMap<String, String>();

		JSONObject object = array.getJSONObject(i);
		Iterator<?> keys = object.keys();

		while (keys.hasNext()) {
		  String key = (String) keys.next();
		  String val = object.getString(key);

		  map.put(key, val);
		}

		rtn.add(i, map);

		map = null;
	  }
	} catch (JSONException e) {
//      e.printStackTrace();
	}

	return rtn;
  }

  public static void createNotificationChannel(Context context, String channelId, String channelName, String description, String pref, String key) {
	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	NotificationChannel mChannel;

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
	  mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
	  mChannel.setDescription(description);
	  mChannel.setVibrationPattern(new long[] {500, 500});

	  if (notificationManager != null) {
		notificationManager.createNotificationChannel(mChannel);
		MKFunction.setBooleanPreference( context, pref, key, true);
		Log.e("CreateChannel", "create , " + channelId);
	  }
	}
  }

  public static void setStringPreference(Context context, String type, String key, String value) {
	SharedPreferences prefs = context.getSharedPreferences(type, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(key, value);
	editor.apply();
  }

  public static void setBooleanPreference(Context context, String type, String key, boolean value) {
	SharedPreferences prefs = context.getSharedPreferences(type, Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putBoolean(key, value);
	editor.apply();
  }

  public static void setIntPreference(Context context, String type, String key, int value) {
	SharedPreferences prefs = context.getSharedPreferences(type, context.MODE_PRIVATE);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putInt(key, value);
	editor.commit();
  }

  public static String getStringPreference(Context context, String type, String key) {
	SharedPreferences prefs = context.getSharedPreferences(type, Context.MODE_PRIVATE);
	String rtn = prefs.getString(key, "");

	return rtn;
  }

  public static boolean getBooleanPreference(Context context, String type, String key) {
	SharedPreferences prefs = context.getSharedPreferences(type, context.MODE_PRIVATE);
	boolean rtn = prefs.getBoolean(key, false);

	return rtn;
  }

  public static int getIntPreference(Context context, String type, String key) {
	SharedPreferences prefs = context.getSharedPreferences(type, context.MODE_PRIVATE);
	int rtn = prefs.getInt(key, 0);

	return rtn;
  }

  public static void deletePreference(Context context, String type) {
	SharedPreferences prefs = context.getSharedPreferences(type, context.MODE_PRIVATE);
	SharedPreferences.Editor editor = prefs.edit();
	editor.clear();
	editor.apply();
  }

  public static String getString(Context context, int stringId) {
	return context.getResources().getString(stringId);
  }

  public static String setBase64Encode(String str) {
	return Base64.encodeToString(str.getBytes(), 0);
  }

  public static String setBase64Decode(String str) {
	return new String(Base64.decode(str, 0));
  }

  public static String setURLEncode(String content, String lngType) throws UnsupportedEncodingException {
	return URLEncoder.encode(content, lngType);
  }

  public static String setURLDecode(String content, String lngType) throws UnsupportedEncodingException {
	return URLDecoder.decode(content, lngType);
  }

  public static String aesEncrypt(String encKey, byte[] iv, String paramInput) throws Exception {
	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
	SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");

	cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

	byte[] cipherText = cipher.doFinal(paramInput.getBytes("UTF-8"));

	return Base64.encodeToString(cipherText, 0);
  }

  public static String aesDecrypt(String encKey, byte[] iv, String paramInput) throws Exception {
	byte[] textBytes = Base64.decode(paramInput, 0);
	AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
	SecretKeySpec newKey = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");

	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
	cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

	return new String(cipher.doFinal(textBytes), "UTF-8");
  }

  public static String hashMapToString(HashMap<String, String> hashMap) {
	String rtn = "";

	int i = 0;

	for (String key : hashMap.keySet()) {
	  if (i > 0) {
		rtn += "&";
	  }

	  try {
		rtn += key + "=" + URLEncoder.encode(hashMap.get(key), "UTF-8");
	  } catch (Exception e) {
		e.printStackTrace();
	  }

	  i++;
	}

	return rtn;
  }

  public static String returnCode(Context mContext, String result) {
	Log.e("returnCode", ":" + result);
	if (result == null) {
	  return "error";
	} else {
	  if (result.equals("")) {
		return "error";
	  } else {
		ArrayList<HashMap<String, String>> data = MKFunction.parseJSON(mContext, result);

		int total = data.size();

		if (total > 0) {
		  for (HashMap<String, String> map : data) {
			String code = map.get("code") == null ? "" : map.get("code");

			return code;
		  }
		}
	  }
	}

	return "error";
  }

  public static int makeMillTime() {
	long aa = System.currentTimeMillis();

	String bb = Long.toString(aa);
	int cc = Integer.parseInt(bb.substring(bb.length() - 6));

	return cc;
  }
}
