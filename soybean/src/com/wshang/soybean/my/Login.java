package com.wshang.soybean.my;

import com.lidroid.xutils.ViewUtils;
import com.wshang.soybean.R;
import android.app.Activity;
import android.os.Bundle;

public class Login extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ViewUtils.inject(this);
//		AlibabaSDK.getService(LoginService.class).showLogin(Login.this, new InternalLoginCallback());
//	    registerBroadcast();
	}
	
//	private void registerBroadcast()
//	{
//		Login.this.registerReceiver(receiver, new IntentFilter(HttpAction.HOME_TOP));
//	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		Login.this.unregisterReceiver(receiver);
	}



	
}
