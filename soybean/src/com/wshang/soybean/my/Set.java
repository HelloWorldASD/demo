package com.wshang.soybean.my;
//package com.wshang.soybean.my;
//
//import com.taobao.tae.sdk.callback.LogoutCallback;
//import com.umeng.update.UmengUpdateAgent;
//import com.wshang.soybean.R;
//import com.wshang.soybean.main.MyApplication;
//import com.wshang.soybean.main.WebActivity;
//import com.wshang.tools.HttpAction;
//import com.wshang.tools.HuiConstants;
//import com.wshang.tools.StringUtil;
//import com.wshang.tools.ToastUtil;
//import com.alibaba.sdk.android.AlibabaSDK;
//import com.alibaba.sdk.android.login.LoginService;
//import com.alibaba.sdk.android.session.model.Session;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class Set extends Activity
//{
//	@ViewInject(R.id.txtHead)
//	private TextView txtHead;
//	@ViewInject(R.id.txtCheckVersion)
//	private TextView txtCheckVersion;
//	@ViewInject(R.id.btnCloseUser)
//	private Button btnCloseUser;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.set);
//		ViewUtils.inject(this);
//		txtHead.setText("设置");
//		initData();
//	}
//	
//	
//	private void initData()
//    {
//		PackageManager packageManager = getPackageManager();
//        // getPackageName()是你当前类的包名，0代表是获取版本信息
//        PackageInfo packInfo;
//		try {
//			packInfo = packageManager.getPackageInfo(getPackageName(),0);
//	        String version = packInfo.versionName;
//			txtCheckVersion.setText("当前版本" + version);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if (StringUtil.isEmpty(MyApplication.getInstance().getMember().getMemberMap().get("user_id")))
//		{
//			btnCloseUser.setVisibility(View.GONE);
//		}
//		else
//		{
//			btnCloseUser.setVisibility(View.VISIBLE);
//		}
//    }
//	
//	private void logoutUser()
//	{
//		Session aliSession = AlibabaSDK.getService(LoginService.class).getSession();
//		if (aliSession != null && aliSession.isLogin())
//		{
//			AlibabaSDK.getService(LoginService.class).logout(Set.this, new UserLogoutCallback());
//		}
//		else
//		{
//			sendLoginUserBoradcast();
//		}
//	}
//	
//	public void onClick(View sender)
//	{
//		switch (sender.getId())
//		{
//			case R.id.ivBack:
//				this.finish();
//				break;
//			case R.id.rlyUpdate:
//				UmengUpdateAgent.update(this);
//				break;
////			case R.id.rlyContact:
////				if (CommonUtil.openLogin(Set.this, false) == false)
////				{
////					Intent ittMessage = new Intent();
////					ittMessage.setClass(Set.this, QuestionList.class);
////					this.startActivity(ittMessage);
////				}
////				break;
//			case R.id.rlyHelp:
//				Intent itt = new Intent(Set.this, WebActivity.class);
//				itt.putExtra("url", HuiConstants.HTML_URL + "?s=public/help");
//				itt.putExtra("title", "帮助中心");
//				startActivity(itt);
//				break;
//			case R.id.btnCloseUser:
//				MyApplication.getInstance().getMember().clearAll();
//				btnCloseUser.setVisibility(View.GONE);
//				logoutUser();
//				break;
//		}
//	}
//	
//	private class UserLogoutCallback implements LogoutCallback  
//	{
//
//		@Override
//		public void onSuccess() {
//			sendLoginUserBoradcast();
//		}
//
//		@Override
//		public void onFailure(int arg0, String arg1) {
//			ToastUtil.makeFailToastThr(Set.this, "退出失败!");
//		}
//		
//    }
//	
//	private void sendLoginUserBoradcast()
//	{
//		Intent ittUser = new Intent(HttpAction.LOGIN_USER);
//		ittUser.putExtra("isRegister", false);
//		Set.this.sendBroadcast(ittUser);
//	}
//	  
//}