package com.neodays.otp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class LoginActivity extends AppCompatActivity {

  private Activity    mActivity;
  private Context     mContext;
  private EditText    userId;
  private Button      submit;
  private ProgressBar progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.activity_login );

	mActivity = this;
	mContext  = this;

	init();
  }

  private void init() {
	setMapping();
	setEvent();
  }

  private void setMapping() {
	userId   = (EditText)    mActivity.findViewById( R.id.loginId );
	submit   = (Button)      mActivity.findViewById( R.id.submit );
	progress = (ProgressBar) mActivity.findViewById( R.id.progress );
  }

  public void setEvent() {
	userId.addTextChangedListener( textWatcher );
	userId.setRawInputType( InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
	submit.setOnClickListener( clickListener );
  }

  private void loginSubmit() {
	if ( userId.getText().toString().equals("") ) {
	  MKFunction.customToast( mContext, getString( R.string.errorEmptyAccount ) );
	} else {
	  loginData();
	}
  }

  private void loginData() {
	boolean isNetwork = MKFunction.checkNetwork( mContext );
	if (!isNetwork) {
	  MKFunction.customToast( mContext, getString( R.string.errorNetworkCancel ) );
	} else {
	  String idData = userId.getText().toString().trim();
	  if( idData.equals( StringConfig.TEST_ID_1 ) || idData.equals( StringConfig.TEST_ID_2 ) ) {
		loginCb(idData);
	  } else {
		MKFunction.customToast( mContext, getString( R.string.errorLoginAccount) );
	  }
	}
  }

  private void loginCb(String userId) {
	CallBack callBack = new CallBack() {
	  String code = "";

	  @Override
	  public void start() {
		Log.e("LoginActivity", "start");
		showProgress();
	  }

	  @Override
	  public void error() {
		Log.e("LoginActivity", "error");
	  }

	  @Override
	  public void result(String result) {
		code = MKFunction.returnCode(mContext, result);
		if ( code.equals("00") ) {
		  autoLoginPref( userId );
		  loginSuccess();
		} else {
		  MKFunction.threadToast( mActivity, getString( R.string.errorDeviceUpdate) );
		}
	  }

	  @Override
	  public void end() {
		Log.e("LoginActivity.end", code);
		hideProgress();
	  }

	};

	FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
	  @Override
	  public void onComplete(@NonNull Task<String> task) {
		if (!task.isSuccessful()) {
		  return;
		}

		String deviceKey = task.getResult();

		Log.e("Token", "" + deviceKey);

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put( "userId", userId );
		hashMap.put( "deviceKey", deviceKey );

		NetworkManager networkManager = new NetworkManager(callBack, StringConfig.DEVICE_URL, hashMap );
		Thread thread = new Thread( networkManager );
		thread.start();

		thread = null;

	  }
	});
  }

  private void loginSuccess() {
	Intent intent = new Intent( mContext, MainActivity.class );
	startActivity(intent);
	finish();
  }

  private void autoLoginPref(String id) {
	MKFunction.setStringPreference( mContext, StringConfig.LOGIN_PREF, StringConfig.USER_ID, id );
  }

  View.OnClickListener clickListener = new View.OnClickListener() {
	@Override
	public void onClick(View v) {
	  if ( v == submit ) {
		loginSubmit();
	  }
	}
  };

  TextView.OnEditorActionListener textListener = new TextView.OnEditorActionListener() {
	@Override
	public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
	  if ( i == EditorInfo.IME_ACTION_DONE ) {
		loginSubmit();
	  }
	  return true;
	}
  };

  private final TextWatcher textWatcher = new TextWatcher() {

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	  if ( userId.getText().toString().equals("") ) {
		submit.setEnabled(false);
	  } else {
		submit.setEnabled(true);
	  }
	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

  };

  private void showProgress() {
	runOnUiThread(new Runnable() {
	  @Override
	  public void run() {
		submit.setVisibility( View.GONE );
		progress.setVisibility( View.VISIBLE );
	  }
	});
  }

  private void hideProgress() {
	runOnUiThread(new Runnable() {
	  @Override
	  public void run() {
		progress.setVisibility( View.GONE );
		submit.setVisibility( View.VISIBLE );
	  }
	});
  }

}
