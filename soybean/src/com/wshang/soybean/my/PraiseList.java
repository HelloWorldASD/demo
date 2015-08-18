package com.wshang.soybean.my;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.main.LoginTaoBao;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.EditChangedListener;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.LoadingProgress;
import com.wshang.soybean.tools.StringUtil;
import com.wshang.soybean.tools.ToastUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class PraiseList extends LoginTaoBao implements  View.OnClickListener {
    
	@ViewInject(R.id.ivBack)
	private ImageView ivBack;
	@ViewInject(R.id.txtHead)
	private TextView txtHead;
	@ViewInject(R.id.lvPraise)
	private ListView lvMessage;
	@ViewInject(R.id.edtMessage)
	private EditText edtMessage;
	@ViewInject(R.id.btnSend)
	private Button btnSend;
	
	private PraiseAdapter praiseAdapter;
	private JSONArray data;
	private Dialog loading = null;
	private String title;
	private int type = 1;//1赞美；2吐槽
	
	private boolean isRefresh = false;
    private boolean isLoad = false;
    private boolean loadState = false;
    private boolean isFinish = false;
	private int currentPage = 1;
	
	
	public void onCreate(Bundle savedIntanceState) {
		super.onCreate(savedIntanceState);
		setContentView(R.layout.praise_list);
		ViewUtils.inject(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
		lvMessage = (ListView) findViewById(R.id.lvPraise);
		loading = new LoadingProgress(this, R.style.LoadDialog, "");
		ivBack.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		
		praiseAdapter = new PraiseAdapter(PraiseList.this, data);
		lvMessage.setAdapter(praiseAdapter);
		lvMessage.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == ListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
				{
					InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
					if (imm != null && getCurrentFocus() != null)
					{
						IBinder binder = getCurrentFocus().getWindowToken();
						if (binder != null)
						{
							imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
				}
				
				if (scrollState != OnScrollListener.SCROLL_STATE_IDLE)
					return ;
				
				if (isRefresh == true)
				{
					isFinish = false;
					currentPage = 1;
					getDataList(false);
				}
				else if (isLoad == false
					&& loadState == true
					&& isFinish == false)
				{
					currentPage = currentPage + 1;
					getDataList(true);	
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int _visibleItemCount, int totalItemCount)
			{
                if ((firstVisibleItem + _visibleItemCount) == totalItemCount && totalItemCount > 0)
                {
                	isRefresh = true;
                }
                else
                {
                	isRefresh = false;
                }
                
                if (firstVisibleItem == 0)// 滑动顶部
                {
                	loadState = true;
                }
                else 
                {
                	loadState = false;
                }
			}
		});
		initData();
	}
	
	private void initData()
	{
		type = getIntent().getIntExtra("type", 1);
		title = type == 1 ? "赞美一下" : "吐槽一下";
		txtHead.setText(title);
		getDataList(false);
		registerBroadcast();
	}
	
	private void registerBroadcast()
	{
		PraiseList.this.registerReceiver(receiver, new IntentFilter(HttpAction.GOOD_LISTS));
		PraiseList.this.registerReceiver(receiver, new IntentFilter(HttpAction.BAD_LISTS));
		PraiseList.this.registerReceiver(receiver, new IntentFilter(HttpAction.ADD_BAD));
		PraiseList.this.registerReceiver(receiver, new IntentFilter(HttpAction.ADD_GOOD));
	}
	
	private void getDataList(boolean isAdd)
	{
		loading.show();
		if (type == 1)
			getGoodList(isAdd);
		else 
			getBadList(isAdd);
	}
	
	private void getGoodList(boolean isAdd)
	{
		Map<String, String> params = new HashMap<String, String>();
		if (isAdd == true)
			params.put("isAdd", "1");	
		
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("page", String.valueOf(currentPage));
		params.put("noCache", "false");
		HttpManage.getInstance().getGoodList(params, PraiseList.this);
	}
	
	private void handleGoodList(JSONObject result, boolean isAdd)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(PraiseList.this, PraiseList.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			
			
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(PraiseList.this, result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(PraiseList.this, true);
    			}
    			return ;
    		}
    		
    		data = result.getJSONArray("data");
    		praiseAdapter.setData(data, isAdd);
    		praiseAdapter.notifyDataSetChanged();
    		
    		if (isAdd == true)
    			lvMessage.setSelection(0);
    		else 
    			lvMessage.setSelection(lvMessage.getBottom());
    		
    		if (data == null || data.length() < HuiConstants.PAGE_SIZE)
				isFinish = true;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getBadList(boolean isAdd)
	{
		Map<String, String> params = new HashMap<String, String>();
		if (isAdd == true)
			params.put("isAdd", "1");	
		
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("page", String.valueOf(currentPage));
		params.put("noCache", "false");
		HttpManage.getInstance().getBadList(params, PraiseList.this);
	}
	
	private void handleBadList(JSONObject result, boolean isAdd)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(PraiseList.this, PraiseList.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(PraiseList.this, result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(PraiseList.this, true);
    			}
    			return ;
    		}
    		
    		data = result.getJSONArray("data");
    		praiseAdapter.setData(data, isAdd);
    		praiseAdapter.notifyDataSetChanged();
    		if (isAdd == true)
    			lvMessage.setSelection(0);
    		else 
    			lvMessage.setSelection(lvMessage.getBottom());
    		
    		if (data == null || data.length() < HuiConstants.PAGE_SIZE)
				isFinish = true;

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View sender)
	{
		switch (sender.getId())
		{
			case R.id.ivBack:
				this.finish();
				break;
			case R.id.btnSend:
				if (type == 1)
					addGood();
				else 
					addBad();
				break;
		}
	}
	
	private void addBad()
	{
		String content = edtMessage.getText().toString().trim();
		
		if (StringUtil.isEmpty(content))
		{
			Toast.makeText(PraiseList.this, PraiseList.this.getResources().getString(R.string.content_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		
		loading.show();
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("token",  MyApplication.getInstance().getMember().getMemberMap().get("token"));
		params.put("content", content);
		HttpManage.getInstance().addBad(params, PraiseList.this);
	}
	
	private void handleBad(JSONObject result)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(PraiseList.this, PraiseList.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(PraiseList.this, result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(PraiseList.this, true);
    			}
    		}
    		else
    		{
    			JSONObject newData = result.getJSONObject("data");
    			List<JSONObject> data = praiseAdapter.getData();
    			data.add(newData);
    			praiseAdapter.setData(data);
    			
    			praiseAdapter.notifyDataSetChanged();
    			lvMessage.setSelection(lvMessage.getBottom());
        		edtMessage.setText("");
    		}

//    		else
//    		{
//    			onRefresh();
//    		}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addGood()
	{
		String content = edtMessage.getText().toString().trim();
		if (StringUtil.isEmpty(content))
		{
			Toast.makeText(PraiseList.this, PraiseList.this.getResources().getString(R.string.content_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		
		loading.show();
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("token",  MyApplication.getInstance().getMember().getMemberMap().get("token"));
		params.put("content", content);
		HttpManage.getInstance().addGood(params, PraiseList.this);
	}
	
	private void handleGood(JSONObject result)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(PraiseList.this, PraiseList.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(PraiseList.this, result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(PraiseList.this, true);
    			}
    			return ;
    		}
    		else
    		{
    			JSONObject resultData = result.getJSONObject("data");
    			praiseAdapter.addNewData(resultData);
    			praiseAdapter.notifyDataSetChanged();
    			lvMessage.setSelection(lvMessage.getBottom());
        		edtMessage.setText("");
    		}

    		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(title);
		MobclickAgent.onPause(PraiseList.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(title);
		MobclickAgent.onResume(PraiseList.this);       //统计时长
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PraiseList.this.unregisterReceiver(receiver);
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
				boolean isAdd = intent.getBooleanExtra("isAdd", false);
				
				if (isAdd == false)
					isRefresh = false;
				
				if (action.equals(HttpAction.ADD_GOOD))
				{
					handleGood(result);
				}
				else if (action.equals(HttpAction.ADD_BAD))
				{
					handleBad(result);
				}
				else if (action.equals(HttpAction.GOOD_LISTS))
				{
					handleGoodList(result, isAdd);
				}
				else if (action.equals(HttpAction.BAD_LISTS))
				{
					handleBadList(result, isAdd);
				}
			}
		}
	};
}
