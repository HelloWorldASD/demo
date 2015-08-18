package com.wshang.soybean.main;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.taobao.tae.sdk.callback.CallbackContext;
import com.taobao.tae.sdk.callback.LogoutCallback;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.my.PraiseList;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.LoadingProgress;
import com.wshang.soybean.tools.StringUtil;
import com.wshang.soybean.tools.ThreadManager;
import com.wshang.soybean.tools.ToastUtil;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.session.model.Session;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class CenterFragment extends Fragment implements View.OnClickListener
{
	private ImageView ivHeader;
	private TextView txtNickName;
	private RelativeLayout rlyLogin;
	private RelativeLayout rlyPraise;
	private RelativeLayout rlyBad;
	private RelativeLayout rlyCache;
	private RelativeLayout rlyVersionUpdate;
	private TextView txtVersionNumber;
	private RelativeLayout rlyExit;
	

	private View centerLayout;
	private ToggleButton tbPush;
	private Dialog loading = null;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		centerLayout = inflater.inflate(R.layout.center,  
                container, false);  
		
		initView();
		initData();
		return centerLayout;
	}
	
	private void initView()
	{
		TextView txtTitle = (TextView) centerLayout.findViewById(R.id.txtTitle);
		txtTitle.setText("我的");
		
		ivHeader = (ImageView) centerLayout.findViewById(R.id.ivHeader);
		txtNickName = (TextView) centerLayout.findViewById(R.id.txtNickName);
		rlyLogin = (RelativeLayout) centerLayout.findViewById(R.id.rlyLogin);
		rlyPraise = (RelativeLayout) centerLayout.findViewById(R.id.rlyPraise);
		rlyBad = (RelativeLayout) centerLayout.findViewById(R.id.rlyBad);
		rlyCache = (RelativeLayout) centerLayout.findViewById(R.id.rlyCache);
		rlyVersionUpdate = (RelativeLayout) centerLayout.findViewById(R.id.rlyVersionUpdate);
		txtVersionNumber = (TextView) centerLayout.findViewById(R.id.txtVersionNumber);
		rlyExit = (RelativeLayout) centerLayout.findViewById(R.id.rlyExit);
		
		tbPush = (ToggleButton)centerLayout.findViewById(R.id.tbPush);
//		tbPush.getBackground().setAlpha(0);
		tbPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					MyApplication.getInstance().getMember().saveKeyOfValue("push", "true");
					//选中
				}else{
					MyApplication.getInstance().getMember().saveKeyOfValue("push", "false");
					//未选中
				}
				
				HttpManage.getInstance().openOrClosePush(CenterFragment.this.getActivity().getApplicationContext());
			}
		});// 添加监听事件
		
		rlyLogin.setOnClickListener(this);
		rlyPraise.setOnClickListener(this);
		rlyBad.setOnClickListener(this);
		rlyCache.setOnClickListener(this);
		rlyVersionUpdate.setOnClickListener(this);
		rlyExit.setOnClickListener(this);

	}
	
	private void initData()
	{
		BitmapTools tools = new BitmapTools(CenterFragment.this.getActivity());
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
			
		if ("true".equals(MyApplication.getInstance().getMember().getMemberMap().get("push")))
		{
			tbPush.setChecked(true);
		}
		else
			tbPush.setChecked(false);
		
		PackageManager packageManager = CenterFragment.this.getActivity().getPackageManager();
        PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(CenterFragment.this.getActivity().getPackageName(),0);
	        String version = packInfo.versionName;
			txtVersionNumber.setText(version);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initUserInfo();
		CenterFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.LOGIN_USER));
		CenterFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.TAE_LOGIN));
	}


	@Override
	public void onResume() 
	{
		super.onResume();
		MobclickAgent.onPageStart(getResources().getString(R.string.main_my));
		MobclickAgent.onResume(CenterFragment.this.getActivity());
	}

	@Override
	public void onPause() 
	{
		super.onPause();
		MobclickAgent.onPageEnd(getResources().getString(R.string.main_my));
		MobclickAgent.onPause(CenterFragment.this.getActivity());
	}
	
	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		CenterFragment.this.getActivity().unregisterReceiver(receiver);
	}

	private void initUserInfo()
	{
		String user_id = MyApplication.getInstance().getMember().getMemberMap().get("user_id");
		if (StringUtil.isEmpty(user_id))
		{
			rlyExit.setVisibility(View.GONE);
		}
		else 
		{
			rlyExit.setVisibility(View.VISIBLE);
		}
		
		
		String nick_name = MyApplication.getInstance().getMember().getMemberMap().get("nickname");
		if (StringUtil.isEmpty(nick_name))
		{
			if (StringUtil.isEmpty(user_id))
			{
				txtNickName.setText(CenterFragment.this.getActivity().getResources().getString(R.string.taobao_account));
			}
			else 
			{
				txtNickName.setText("");
			}
			
			Paint paint = txtNickName.getPaint();
			paint.setUnderlineText(true);
			paint.setAntiAlias(true);
			txtNickName.setTextColor(Color.argb(255, 0, 102, 255));
				
		}
		else 
		{
			txtNickName.setText(nick_name);
			
			Paint paint = txtNickName.getPaint();
			paint.setUnderlineText(false);
			paint.setAntiAlias(true);
			txtNickName.setTextColor(CenterFragment.this.getActivity().getResources().getColor(R.color.txt_gray));
		}
		
		String face = MyApplication.getInstance().getMember().getMemberMap().get("face");
		if (StringUtil.isEmpty(face) == false)
		{
			bitmapUtils.display(ivHeader, face, config);
		}
		else
			ivHeader.setImageResource(R.drawable.default_person);
			
		
		
	}
	
	private void statisticsActivatedUser()
	{
	 	Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(StatisticsType.ActivatedUser.value()));
		params.put("deviceid", CommonUtil.getMac());
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		HttpManage.getInstance().statistics(params, CenterFragment.this.getActivity());
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rlyPraise:
				if (CommonUtil.openLogin(CenterFragment.this.getActivity(), false) == false)
				{
					Intent ittPraiseList = new Intent(CenterFragment.this.getActivity(), PraiseList.class);
					ittPraiseList.putExtra("type", 1);
					this.getActivity().startActivity(ittPraiseList);
				}
				break;
			case R.id.rlyBad:
				if (CommonUtil.openLogin(CenterFragment.this.getActivity(), false) == false)
				{
					Intent ittPraiseList = new Intent(CenterFragment.this.getActivity(), PraiseList.class);
					ittPraiseList.putExtra("type", 2);
					this.getActivity().startActivity(ittPraiseList);
				}
				break;
			case R.id.rlyCache:
				if (loading == null)
					loading = new LoadingProgress(CenterFragment.this.getActivity(), R.style.LoadDialog, "");
				
				loading.show();
				ThreadManager.executeOnSubThread(new Runnable() {
					@Override
					public void run() {
						HttpUtils http = new HttpUtils();
						http.sHttpCache.clear();
						
						BitmapUtils bitmapUtils = new BitmapUtils(CenterFragment.this.getActivity());
						bitmapUtils.clearMemoryCache();
						bitmapUtils.clearDiskCache();
						try {
							Thread.sleep(500);//0.5秒
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						CenterFragment.this.getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loading.dismiss();
								ToastUtil.makeOkToastThr(CenterFragment.this.getActivity(), CenterFragment.this.getResources().getString(R.string.clear_success));
							}
						});
					}
				});
				break;
			case R.id.rlyLogin:
				CommonUtil.openLogin(CenterFragment.this.getActivity(), false);
				break;
			case R.id.rlyVersionUpdate: //版本更新
				UmengUpdateAgent.forceUpdate(CenterFragment.this.getActivity());
				break;
			case R.id.rlyExit:
				AlertDialog.Builder builder = new AlertDialog.Builder(CenterFragment.this.getActivity());
				builder.setMessage("退出当前账号？")
				.setTitle(this.getResources().getString(R.string.app_name))
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0,
							int arg1) {
						arg0.cancel();
					}
				})
				.setPositiveButton("退出",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						
						logoutUser();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				break;
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		CallbackContext.onActivityResult(requestCode, resultCode, data);
	}

	private void logoutUser()
	{
		Session aliSession = AlibabaSDK.getService(LoginService.class).getSession();
		if (aliSession != null && aliSession.isLogin())
		{
			AlibabaSDK.getService(LoginService.class).logout(CenterFragment.this.getActivity(), new UserLogoutCallback());
		}
		else
		{
			MyApplication.getInstance().getMember().clearAll();
			rlyExit.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	



	private class UserLogoutCallback implements LogoutCallback  
	{

		@Override
		public void onSuccess() {
			MyApplication.getInstance().getMember().clearAll();
			initUserInfo();
		}

		@Override
		public void onFailure(int arg0, String arg1) {
			ToastUtil.makeFailToastThr(CenterFragment.this.getActivity(), "退出失败!");
		}
    }

	private BroadcastReceiver receiver = new BroadcastReceiver() {
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
				if (HttpAction.LOGIN_USER.equals(action))
				{
					initUserInfo();
					statisticsActivatedUser();
				}
				else if (HttpAction.TAE_LOGIN.equals(action))
				{
					HttpManage.getInstance().saveMember(result, CenterFragment.this.getActivity());
		    		Intent ittUser = new Intent(HttpAction.LOGIN_USER);
		    		ittUser.putExtra("isRegister", true);
		    		CenterFragment.this.getActivity().sendBroadcast(ittUser);
				}
			}
		}
	};
	
	
}