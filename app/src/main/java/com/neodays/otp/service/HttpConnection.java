package com.neodays.otp.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

public class HttpConnection extends AsyncTask<Void, Void, String> {

  String neoUrl    = "";

  HashMap<String, String> hashMap = new HashMap<>();

  public HttpConnection(String url, HashMap<String, String> hash) {
	this.neoUrl    = url;
	this.hashMap   = hash;
  }

  @Override
  protected String doInBackground(Void... voids) {
	URL url;
	String result = "";

	try {
	  url = new URL(neoUrl);
	  HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	  SSLContext sc;
	  sc = SSLContext.getInstance("TLS");
	  sc.init(null, null, new java.security.SecureRandom());

	  conn.setRequestMethod("POST");
	  conn.setRequestProperty("NTI_KEY", "KHCHOI");
	  conn.setDoOutput(true);
	  conn.setDoInput(true);
	  conn.setUseCaches(false);
	  conn.setDefaultUseCaches(false);
	  conn.setConnectTimeout(30000);
	  conn.setReadTimeout(30000);

	  OutputStream os = conn.getOutputStream();
	  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
	  if (hashMap != null) {
		bw.write(getPostString(hashMap));
	  }

	  bw.flush();
	  bw.close();
	  os.close();

	  InputStream is = conn.getInputStream();
	  StringBuilder builder = new StringBuilder();
	  BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

	  String line;

	  while ((line = reader.readLine()) != null) {
		builder.append(line + "\n");
	  }

	  result = builder.toString();

	  is.close();
	  conn.disconnect();
	} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
	  e.printStackTrace();
	}
	return result;
  }

  @Override
  protected void onPreExecute() {
	super.onPreExecute();
  }

  @Override
  protected void onPostExecute(String result) {
	super.onPostExecute(result);
  }

  private String getPostString(HashMap<String, String> map) {

	StringBuilder result = new StringBuilder();
	boolean first = true;
	for (Map.Entry<String, String> entry : map.entrySet()) {
	  if (entry.getValue() != null) {
		if (first) {
		  first = false;
		} else {
		  result.append("&");
		}

		try {
		  result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
		  result.append("=");
		  result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		} catch (Exception ue) {
		  ue.printStackTrace();
		  Log.e("HttpLogin", "에러");
		}
	  }
	}
	return result.toString();
  }
}
