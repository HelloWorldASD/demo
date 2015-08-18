package com.wshang.soybean.main;

import com.igexin.sdk.PushManager;
import com.taobao.tae.sdk.callback.CallbackContext;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.ThreadManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity  
{
	private FragmentManager fragmentManager;  
	private IndexFragment  indexFragment;  
	private MessageFragment  messageFragment;  
	private CenterFragment  centerFragment;
	
	private LinearLayout llyNavi;
	public RadioButton rbnHome;
	public RadioButton rbnMessage;
	public RadioButton rbnMy;
	private  Drawable selector;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		llyNavi = (LinearLayout) findViewById(R.id.llyNavi);
		rbnHome = (RadioButton) findViewById(R.id.rbnHome);
		rbnMessage = (RadioButton) findViewById(R.id.rbnMessage);
		rbnMy = (RadioButton) findViewById(R.id.rbnMy);
		fragmentManager = getSupportFragmentManager();
		//第一次启动时选中第0个tab  
        setTabSelection(0); 
        
        RadioGroup tabGroup = (RadioGroup) this
				.findViewById(R.id.main_tab_group);
		tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch (checkedId)
				{
					case R.id.rbnHome:// 
						 setTabSelection(0);
						break;
					case R.id.rbnMessage:// 
						 setTabSelection(1);
						break;
					case R.id.rbnMy:// 
						setTabSelection(2);
						break;
				}
			}
		});

		PushManager.getInstance().initialize(this.getApplicationContext());
		HttpManage.getInstance().openOrClosePush(this.getApplicationContext());
		MainActivity.this.registerReceiver(receiver, new IntentFilter(HttpAction.PUSH_MESSAGE));
		MainActivity.this.registerReceiver(receiver, new IntentFilter(HttpAction.UMENG_UPDATE));
		HttpManage.getInstance().umengUpdate(MainActivity.this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initMessageState();
	}
	
	private void initMessageState()
	{
		ThreadManager.executeOnSubThread(new Runnable() {
			@Override
			public void run() {
				int unReadCount = DbManage.getInstance(MainActivity.this).getUnReadMessageCount();
				
				if (unReadCount == 0)
				{
					selector = MainActivity.this.getResources().getDrawable(R.drawable.message_selector);
				}
				else 
				{
					selector = MainActivity.this.getResources().getDrawable(R.drawable.message_p_selector);
				}
				selector.setBounds(0, 0, selector.getIntrinsicWidth(), selector.getIntrinsicHeight());
				
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						rbnMessage.setCompoundDrawables(null, selector, null, null);
					}
				});
			}
		});
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("您确定要退出？")
			.setTitle(this.getResources().getString(R.string.app_name))
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0,
						int arg1) {
					arg0.cancel();
				}
			})
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					exit();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

			return false;
		}

		return super.dispatchKeyEvent(event);
	}
	
	public void exit() 
	{
		MobclickAgent.onKillProcess(MainActivity.this);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
	
	
	 /** 
     * 根据传入的index参数来设置选中的tab页。 
     * 
     */  
    public void setTabSelection(int index)
    {  
        // 开启一个Fragment事务  
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况  
        hideFragments(transaction);  
        switch (index) 
        {  
	        case 0:  
	            if (indexFragment == null) {  
	            	indexFragment = new IndexFragment();  
	                transaction.add(R.id.flyContent, indexFragment);  
	            } else {  
	                transaction.show(indexFragment);  
	            }  
	            break;  
	        case 1:
	        	 if (messageFragment == null) {  
	        		 messageFragment = new MessageFragment();  
	                 transaction.add(R.id.flyContent, messageFragment);  
	             } else {  
	                 transaction.show(messageFragment);  
	             } 
	            break;  
	        default:  
	        	if (centerFragment == null) {  
	        		centerFragment = new CenterFragment();  
	                transaction.add(R.id.flyContent, centerFragment);  
	            } else {  
	                transaction.show(centerFragment);  
	            } 
	        	break;
        }  
        transaction.commit();  
    }  
	
	
    /** 
     * 将所有的Fragment都置为隐藏状态。 
     *  
     * @param transaction 
     *            用于对Fragment执行操作的事务 
     */  
    private void hideFragments(FragmentTransaction transaction) {  
        if (indexFragment != null) {  
            transaction.hide(indexFragment);  
        }  
        
        if (messageFragment != null) {  
            transaction.hide(messageFragment);  
        }  
        
        if (centerFragment != null) {  
            transaction.hide(centerFragment);  
        }  
    }
    
    public void autoNavi()
    {
    	if (llyNavi.getVisibility() == View.GONE)
    	{
    		llyNavi.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		llyNavi.setVisibility(View.GONE);
    	}
    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		CallbackContext.onActivityResult(requestCode, resultCode, data);
	}
    
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null)
			{
				String action = intent.getAction();
				if (HttpAction.PUSH_MESSAGE.equals(action))
				{
					selector = MainActivity.this.getResources().getDrawable(R.drawable.message_p_selector);
					selector.setBounds(0, 0, selector.getIntrinsicWidth(), selector.getIntrinsicHeight());
					rbnMessage.setCompoundDrawables(null, selector, null, null);
				}
				else if (HttpAction.MESSAGE_READ.equals(action))
				{
					initMessageState();
				}
				else if (HttpAction.UMENG_UPDATE.equals(action))
				{
					boolean update = intent.getBooleanExtra("update", false);
					if (update)
					{
						selector = MainActivity.this.getResources().getDrawable(R.drawable.my_p_selector);
					}
					else 
					{
						selector = MainActivity.this.getResources().getDrawable(R.drawable.my_selector);
					}
					selector.setBounds(0, 0, selector.getIntrinsicWidth(), selector.getIntrinsicHeight());
					rbnMy.setCompoundDrawables(null, selector, null, null);
				}
			}
		}
	};
}
