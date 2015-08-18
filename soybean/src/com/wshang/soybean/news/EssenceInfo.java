package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.NewsType;
import com.wshang.soybean.bean.ReadType;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.IndexFragment;
import com.wshang.soybean.main.LoginTaoBao;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.LoadingProgress;
import com.wshang.soybean.tools.StringUtil;
import com.wshang.soybean.tools.ToastUtil;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 长文
 * @author liangpeng
 *
 */
public class EssenceInfo extends LoginTaoBao implements View.OnClickListener
{
	@ViewInject(R.id.txtHead)
	private TextView txtHead;
	@ViewInject(R.id.ivBack)
	private ImageView ivBack;
	@ViewInject(R.id.rlyContent)
	private RelativeLayout rlyContent;
	@ViewInject(R.id.wvDetail)
	private WebView wvDetail;
	@ViewInject(R.id.txtCommentOutArea)
	private TextView txtCommentOutArea;
	
	private String messageId;//id
	private int id;//存储在sqlite的id
	private boolean fromPush;//是否来自推送消息
	private String  url = null;
	private String title = null;
	private String pushId;//推送消息id
	
	@ViewInject(R.id.txtSplit)
	private TextView txtSplit;
	@ViewInject(R.id.ivShare)
	private ImageView ivShare;
	@ViewInject(R.id.ivEdit)
	private ImageView ivEdit;
	@ViewInject(R.id.ivSource)
	private ImageView ivSource;
	@ViewInject(R.id.rlyPraise)
	private RelativeLayout rlyPraise;
	
	@ViewInject(R.id.llyFunction)
	private LinearLayout llyFunction;
	
	@ViewInject(R.id.llyComment)
	private LinearLayout llyComment;
	@ViewInject(R.id.txtPraiseCount)
	private TextView txtPraiseCount;
	
	@ViewInject(R.id.edtMessage)
	private EditText edtMessage;
	@ViewInject(R.id.btnSend)
	private Button btnSend;
	private JSONArray product_list;
	private int praise;
	private Dialog loading = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.essence_info);
		ViewUtils.inject(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
		
		pushId = getIntent().getStringExtra("pushId");
		messageId = getIntent().getStringExtra("messageId");
		id = getIntent().getIntExtra("id", -1);
		fromPush = getIntent().getBooleanExtra("fromPush", false);
		url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		ivBack.setOnClickListener(this);
		ivShare.setOnClickListener(this);
		ivEdit.setOnClickListener(this);
		ivSource.setOnClickListener(this);
		rlyPraise.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		txtCommentOutArea.setOnClickListener(this);
		
		loading = new LoadingProgress(this, R.style.LoadDialog, "");
		txtHead.setText(getResources().getString(R.string.brilliant_anecdote));
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
		
        getInfoDetail();
        setMessageRead();
        registerBroadcast();
        statisticsMessagesOpened();
        cancelComment();
	}  
	
	private void setMessageRead()
	{
		Message mess = DbManage.getInstance(EssenceInfo.this).getMessageById(id);
		if (mess != null && mess.getRead() == ReadType.UN_READ.ordinal())
		{
			mess.setRead(ReadType.READ.ordinal());
			DbManage.getInstance(EssenceInfo.this).save(mess);
			
			Intent ittMessageRead = new Intent(HttpAction.MESSAGE_READ);
			ittMessageRead.putExtra("id", id);
    		sendBroadcast(ittMessageRead);
		}
	}
	
	private void registerBroadcast()
	{
		EssenceInfo.this.registerReceiver(receiver, new IntentFilter(HttpAction.POST_DETAIL));
		EssenceInfo.this.registerReceiver(receiver, new IntentFilter(HttpAction.COMMENT_LISTS));
		EssenceInfo.this.registerReceiver(receiver, new IntentFilter(HttpAction.COMMENT_CREATE));
		EssenceInfo.this.registerReceiver(receiver, new IntentFilter(HttpAction.POST_PRAISE));
	}
	
	private void getInfoDetail()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		HttpManage.getInstance().getNewsDetail(params, EssenceInfo.this);
	}
		
	public void onClick(View sender)
	{
		switch (sender.getId())
		{
			case R.id.ivShare:
				Intent ittShare = new Intent(this, NewsShare.class);
				ittShare.putExtra("wapUrl", url);
				ittShare.putExtra("id", messageId);
				ittShare.putExtra("title", StringUtil.interceptContent(title, HuiConstants.INTERCEPT_SIZE));
				ittShare.putExtra("type", NewsType.LONG.ordinal());
				startActivity(ittShare);
				break;
			case R.id.ivEdit:
				llyFunction.setVisibility(View.GONE);
				txtCommentOutArea.bringToFront();
				llyComment.setVisibility(View.VISIBLE);
				txtSplit.setVisibility(View.GONE);
				edtMessage.requestFocus();
				InputMethodManager imm = (InputMethodManager) EssenceInfo.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
				break;
			case R.id.ivSource:
				openSource();
				break;
			case R.id.rlyPraise:
				sendPraise();
				break;
			case R.id.ivBack:
				finish();
				break;
			case R.id.btnSend:
				createComment();
				break;
			case R.id.txtCommentOutArea:
				cancelComment();
				break;
		}
	}
	
	public void sendPraise()
	{
		loading.show();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("deviceid", CommonUtil.getMac());
		HttpManage.getInstance().sendPraise(params, EssenceInfo.this);
	}
	
	private void cancelComment()
	{
		rlyContent.bringToFront();
		llyFunction.setVisibility(View.VISIBLE);
		llyComment.setVisibility(View.GONE);
		txtSplit.setVisibility(View.VISIBLE);
		wvDetail.reload();
	}
	
	private void createComment()
	{
		String content = edtMessage.getText().toString().trim();
		
		if (StringUtil.isEmpty(content))
		{
			Toast.makeText(EssenceInfo.this, EssenceInfo.this.getResources().getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
			return;
		}

		if (CommonUtil.openLogin(EssenceInfo.this, false) == true)
			return;
		
		loading.show();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("token",  MyApplication.getInstance().getMember().getMemberMap().get("token"));
		params.put("content", content);
		HttpManage.getInstance().createComment(params, EssenceInfo.this);
	}
	
	private void handleCreateComment(JSONObject result)
	{
 		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(EssenceInfo.this, EssenceInfo.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(EssenceInfo.this, result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(EssenceInfo.this, true);
    			}
    		}
    		else
    		{
    			ToastUtil.makeOkToastThr(EssenceInfo.this, "评论成功");
     			edtMessage.setText("");
    			cancelComment();
    		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressLint("ShowToast") 
	private void openSource()
	{
		if (product_list != null)
		{
			int count = product_list.length();
			if (count == 0)
			{
				Toast.makeText(EssenceInfo.this, EssenceInfo.this.getResources().getString(R.string.no_source), Toast.LENGTH_SHORT).show();
				return ;
			}
			
			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<String> urls = new ArrayList<String>();
			ArrayList<String> product_ids = new ArrayList<String>();
			for (int i = 0; i < count; i++)
			{
				JSONObject jsonObj;
				String title = null;
				String url = null;
				String product_id = null;
				try {
					jsonObj = product_list.getJSONObject(i);
					product_id = jsonObj.getString("product_id");
					title = jsonObj.getString("title");
					url = jsonObj.getString("url");
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				product_ids.add(product_id);
				titles.add(title);
				urls.add(url);
			}
			
			if (product_ids.size() == 1)
			{
				HttpManage.getInstance().openTaoBaoH5(this, urls.get(0));
				statisticsSingleContentOnlookers();
			}
			else
			{
				Intent ittSource = new Intent(this, DataSourcePop.class);
				ittSource.putExtra("titles", titles);
				ittSource.putExtra("urls", urls);
				ittSource.putExtra("product_ids", product_ids);
				startActivity(ittSource);
			}
		}
		else
		{
			Toast.makeText(EssenceInfo.this, EssenceInfo.this.getResources().getString(R.string.no_source), Toast.LENGTH_SHORT).show();
			statisticsSingleContentOnlookers();
		}	
	}
	
	private void statisticsSingleContentOnlookers()
	{
		Map<String, String> params = new HashMap<String, String>();
    	params.put("postId", messageId);
    	params.put("id", String.valueOf(StatisticsType.SingleContentClickNumberOnlookers.value()));
     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	HttpManage.getInstance().statistics(params, this);
	}
	
	private void handleInfoDetail(JSONObject result)
	{
		if (result == null)
		{
			ToastUtil.makeFailToastThr(EssenceInfo.this, EssenceInfo.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			if (success == false)
			{
				ToastUtil.makeFailToastThr(EssenceInfo.this, result.getString("msg"));
				return ;
			}
			
			JSONObject data = result.getJSONObject("data");
			product_list = data.getJSONArray("product_list");
			
			if (product_list != null && product_list.length() > 0)
			{
				ivSource.setImageResource(R.drawable.eye);
			}
			else
			{
				ivSource.setImageResource(R.drawable.no_eye);
			}
			
			praise = data.getInt("praise");
			txtPraiseCount.setText(praise == 0 ? "" : String.valueOf(praise));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void handleSendPraise(JSONObject result)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(EssenceInfo.this, EssenceInfo.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			Toast.makeText(EssenceInfo.this, result.getString("msg"), Toast.LENGTH_SHORT).show();
    			return ;
    		}
    		else 
    		{
    			praise = praise + 1;
    			txtPraiseCount.setText(String.valueOf(praise));
    		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(EssenceInfo.this.getLocalClassName());
		MobclickAgent.onPause(EssenceInfo.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(EssenceInfo.this.getLocalClassName());
		MobclickAgent.onResume(EssenceInfo.this);       //统计时长
	}
	 
	public void onDestroy()
	{	
		super.onDestroy();
		EssenceInfo.this.unregisterReceiver(receiver);
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
				
				if (action.equals(HttpAction.POST_DETAIL))
				{
					handleInfoDetail(result);
				}
				else if (action.equals(HttpAction.POST_PRAISE))
				{
					handleSendPraise(result);
				}
				else if (action.equals(HttpAction.COMMENT_CREATE))
				{
					handleCreateComment(result);
				}
			}
		}
	};
	
	private void statisticsMessagesOpened()
	{
		if (fromPush == false)
			return ;
		
		Map<String, String> params = new HashMap<String, String>();
    	params.put("pushId", pushId);
    	params.put("id", String.valueOf(StatisticsType.SingleNumberMessagesOpened.value()));
     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	HttpManage.getInstance().statistics(params, EssenceInfo.this);
	}
}