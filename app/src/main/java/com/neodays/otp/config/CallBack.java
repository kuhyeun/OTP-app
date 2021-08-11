package com.neodays.otp.config;

public interface CallBack {

  public void start();
  public void error();
  public void result(String string);
  public void end();

}
