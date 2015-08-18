package com.wshang.soybean.main;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.alibaba.sdk.android.callback.CallbackContext;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.StringUtil;

import android.os.Bundle;

public class LoginTaoBao extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		LoginTaoBao.this.registerReceiver(loginReceiver, new IntentFilter(HttpAction.TAE_LOGIN));
	}  
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		CallbackContext.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LoginTaoBao.this.unregisterReceiver(loginReceiver);
	}
	
	private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null)
			{
				String action = intent.getAction();
				String strResult = intent.getStringExtra("result");
				JSONObject result = null;
				try {
					result = StringUtil.isEmpty(strResult) ? null : new JSONObject(strResult);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (action.equals(HttpAction.TAE_LOGIN))
				{
					HttpManage.getInstance().saveMember(result, LoginTaoBao.this);
				}
			}
		}
	};
}