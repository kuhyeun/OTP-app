package com.neodays.otp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.neodays.otp.R;
import com.neodays.otp.config.MKFunction;
import com.neodays.otp.config.StringConfig;

public class SettingActivity extends AppCompatActivity {

  private Activity mActivity;
  private Context  mContext;
  private Toolbar  toolbar;
  private TextView toolbarTitle, logout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_setting);

	mActivity = this;
	mContext  = this;

	init();
  }

  private void init() {
    setMapping();
    setAppbar();
    setEvent();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	  case android.R.id.home:
		finish();
		break;
	}
	return true;
  }

  private void setMapping() {
	toolbar      = (Toolbar)  mActivity.findViewById( R.id.settingBar );
	toolbarTitle = (TextView) mActivity.findViewById( R.id.toolbarTitle );
	logout       = (TextView) mActivity.findViewById( R.id.logout );
  }

  private void setAppbar() {
	setSupportActionBar(toolbar);
	setTitle( "" );
	toolbarTitle.setText( getString( R.string.menu_setting ) );
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private void setEvent() {
    logout.setOnClickListener( onClickListener );
  }

  View.OnClickListener onClickListener = new View.OnClickListener() {
	@Override
	public void onClick(View v) {
	  MKFunction.deletePreference( mContext, StringConfig.LOGIN_PREF );
	  MKFunction.deletePreference( mContext, StringConfig.REQUEST_PREF );
	  MKFunction.customToast( mContext, getString( R.string.logout ) );
	  Intent intent = new Intent( mContext, LoginActivity.class );
	  startActivity( intent );
	  finishAffinity();
	}
  };

}
