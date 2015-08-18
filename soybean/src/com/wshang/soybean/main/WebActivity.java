package com.wshang.soybean.main;


import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.ReadType;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.StringUtil;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class WebActivity extends Activity
{
	@ViewInject(R.id.txtHead)
	private TextView txtHead;
	@ViewInject(R.id.wvDetail)
	private WebView wvDetail;
	private String  url = null;
	private String title = null;
	
	private int id;//存储在sqlite的id
	private boolean fromPush;//是否来自推送消息
	private String pushId;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_detail);
		ViewUtils.inject(this);
		
		id = getIntent().getIntExtra("id", -1);
		fromPush = getIntent().getBooleanExtra("fromPush", false);
		pushId = getIntent().getStringExtra("pushId");
		url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		title = StringUtil.interceptContent(title, HuiConstants.INTERCEPT_SIZE);
		txtHead.setText(title);
		wvDetail.loadUrl(url);
		wvDetail.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	             view.loadUrl(url);
	            return true;
	        }
	    });

		WebSettings webSettings = wvDetail.getSettings();  
	     
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);  
		webSettings.setUseWideViewPort(true);//关键点  
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);    
//		webSettings.setDisplayZoomControls(true);  
		webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本  
		webSettings.setAllowFileAccess(true); // 允许访问文件  
		webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮  
		webSettings.setSupportZoom(true); // 支持缩放  
		webSettings.setLoadWithOverviewMode(true);   
		/**  
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：  
		 * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放  
		 */  
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);  

        setMessageRead();
        statisticsMessagesOpened();
	}  
	
	private void setMessageRead()
	{
		Message mess = DbManage.getInstance(WebActivity.this).getMessageById(id);
		if (mess != null && mess.getRead() == ReadType.UN_READ.ordinal())
		{
			mess.setRead(ReadType.READ.ordinal());
			DbManage.getInstance(WebActivity.this).save(mess);
			
			Intent ittMessageRead = new Intent(HttpAction.MESSAGE_READ);
			ittMessageRead.putExtra("id", id);
    		sendBroadcast(ittMessageRead);
		}
	}
	
	private void statisticsMessagesOpened()
	{
		if (fromPush == false)
			return ;
		
		Map<String, String> params = new HashMap<String, String>();
    	params.put("id", String.valueOf(StatisticsType.SingleNumberMessagesOpened.value()));
    	params.put("pushId", pushId);
     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	HttpManage.getInstance().statistics(params, WebActivity.this);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(WebActivity.this.getLocalClassName());
		MobclickAgent.onPause(WebActivity.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(WebActivity.this.getLocalClassName());
		MobclickAgent.onResume(WebActivity.this);       //统计时长
	}
		
	public void onClick(View sender)
	{
		switch (sender.getId())
		{
			case R.id.ivBack:
				this.finish();
				break;
		}
	}

	 
	public void onDestroy()
	{	
		super.onDestroy();
	}
}