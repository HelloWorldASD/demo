package com.wshang.soybean.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.alibaba.sdk.android.AlibabaSDK;
import com.lidroid.xutils.BitmapUtils;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.wshang.soybean.R;
import com.wshang.soybean.my.MemberInfo;

public class MyApplication extends Application 
{
	private static MyApplication   singleton;
//	private static BitmapUtils bitmapUtils = null;
	private static MemberInfo member;
	public String curTab = "";
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		singleton = this;
		curTab = getString(R.string.main_home);
		member = new MemberInfo(this);
//		bitmapUtils  = new BitmapUtils(this);
//		bitmapUtils.configDefaultLoadingImage(R.drawable.pic_loading);
//		bitmapUtils.configDefaultLoadFailedImage(R.drawable.pic_error);

	    initTae();
	}
	
	public Context getAppCtx()
	{
		return singleton == null ? null : singleton.getApplicationContext();
	}
	
	/**
	 * 初始化阿里百川信息
	 */
	private void initTae()
	{
		InitResultCallback resultBack = new InitResultCallback() {
            @Override
            public void onSuccess() {

            }
 
            @Override
            public void onFailure(int code, String message) {

            }
        };
		
		AlibabaSDK.asyncInit(this.getApplicationContext(), resultBack);
	}
	
	@SuppressLint("ShowToast")
	public static void notice(Context context,String notice)
	{
		Toast.makeText(context,notice,40).show();
	}
	
	public static void notice(Context context,String notice,int seconds)
	{
		Toast.makeText(context,notice,seconds).show();
	}
	
//	public static BitmapUtils getBitmapUtils() {
//		return bitmapUtils;
//	}

	public static MyApplication getInstance() 
	{
		return singleton;
	}

	public MemberInfo getMember() {
		member.getMemberInfo();
		return member;
	}
}