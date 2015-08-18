package com.wshang.soybean.main;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.trade.ItemService;
import com.taobao.tae.sdk.callback.TradeProcessCallback;
import com.taobao.tae.sdk.model.TradeResult;
import com.taobao.tae.sdk.webview.TaeWebViewUiSettings;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.news.IComment;
import com.wshang.soybean.news.InfoArticleDetail;
import com.wshang.soybean.news.NewsAdapter;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.EditChangedListener;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.LoadingProgress;
import com.wshang.soybean.tools.StringUtil;
import com.wshang.soybean.tools.ToastUtil;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;


public class IndexFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, IComment 
{
	Map<String, Object> detail;
	private JSONArray news = null;
	private View headerView;
	private ListView lvNews;
	private NewsAdapter newsAdapter = null;
	private SwipeRefreshLayout swipeLayout;  
	private boolean isRefresh = false;
    private boolean isLoad = false;
    private boolean loadState = false;
    private boolean isFinish = false;
	private int currentPage = 1;
	private Dialog loading = null;
	private View indexLayout;
	private EditText edtMessage;
	private LinearLayout llyBottom;
	private RelativeLayout rlyContent;
	private TextView txtCommentOutArea;
	private Button btnSend;
	
	private boolean isOnPause = false;
	private  boolean hasMeasured = false;
	public static int gvWidth = 0;
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		indexLayout = inflater.inflate(R.layout.home,  
	                container, false);  
        inflater    = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerView  = inflater.inflate(R.layout.index_header, null);
		initView();
		getHomeTop();
		loading.show();
		getNewsList(false);
		registerBroadcast();
		return indexLayout;
	}
	
	/**
	 * 注册首页顶部轮播广告广播
	 * 注册资讯正文列表广播
	 */
	private void registerBroadcast()
	{
		IndexFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.HOME_TOP));
		IndexFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.NEWS_LIST));
		IndexFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.COMMENT_CREATE));
		IndexFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.POST_PRAISE));
		IndexFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.TAE_LOGIN));
	}
	
	/**
	 * 
	 */
	private void unRegisterBroadcast()
	{
		IndexFragment.this.getActivity().unregisterReceiver(receiver);
	}
	
	private void initView()
	{
		TextView txtTitle = (TextView) indexLayout.findViewById(R.id.txtTitle);
		txtTitle.setText("毛豆");
		swipeLayout = (SwipeRefreshLayout) indexLayout.findViewById(R.id.swipe_container);  
        swipeLayout.setOnRefreshListener(this);    
        
		loading = new LoadingProgress(IndexFragment.this.getActivity(), R.style.LoadDialog, "");
		lvNews = (ListView) indexLayout.findViewById(R.id.lvNews);
		btnSend = (Button) indexLayout.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
		edtMessage = (EditText) indexLayout.findViewById(R.id.edtMessage);
		llyBottom = (LinearLayout) indexLayout.findViewById(R.id.llyBottom);
		
		lvNews.addHeaderView(headerView);
		newsAdapter = new NewsAdapter(IndexFragment.this.getActivity(), IndexFragment.this, news);
		lvNews.setAdapter(newsAdapter);
		lvNews.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& isLoad == false
					&& loadState == true
					&& isFinish == false)
				{
					currentPage = currentPage + 1;
					loading.show();
					getNewsList(true);	
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int _visibleItemCount, int totalItemCount)
			{
							
			 	loadState = false;
                if ((firstVisibleItem + _visibleItemCount) == totalItemCount)
                {
                	loadState = true;
                }
			}
		});
		
		lvNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0,
					View arg1, int index, long arg3)
			{
				if (index == 0)
					return ;
				
				JSONObject item = (JSONObject) newsAdapter.getItem(index-1);
				String id = null;
				try {
					id = item.getString("id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent ittDetail = new Intent(IndexFragment.this.getActivity(), InfoArticleDetail.class);
				ittDetail.putExtra("messageId", id);
				IndexFragment.this.getActivity().startActivity(ittDetail);
			}
		});
		
		rlyContent = (RelativeLayout) indexLayout.findViewById(R.id.rlyContent);
		txtCommentOutArea = (TextView) indexLayout.findViewById(R.id.txtCommentOutArea);
		txtCommentOutArea.setOnClickListener(this);
		ViewTreeObserver vto = txtCommentOutArea.getViewTreeObserver();
	    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
	      @Override
	      public boolean onPreDraw() {
	        if (hasMeasured == false)
	        {	
		          //获取高度
	        	gvWidth = txtCommentOutArea.getMeasuredWidth() - 20;
		        hasMeasured = true;
	        }
	        return true;
	      }
	    });
		rlyContent.bringToFront();

	}
	
	/**
	 * 初始化轮播图片
	 * @param data
	 */
	private void initPictureCarousel(JSONArray data)
	{
		//图片轮播
		SlideShowView sv = new SlideShowView(this.getActivity(), this, data, RelativeLayout.CENTER_HORIZONTAL);
		LinearLayout llyTop = (LinearLayout) indexLayout.findViewById(R.id.llyTopCarousel);
		llyTop.removeAllViews();
		llyTop.addView(sv);
	}
	

	/**
	 * 获取顶部轮播图片
	 */
	public void getHomeTop()
	{
		HttpManage.getInstance().getHomeTop(IndexFragment.this.getActivity());
	}
	
	public void getNewsList(boolean isAdd)
	{
		Map<String, String> params = new HashMap<String, String>();
		if (isAdd == true)
			params.put("isAdd", "1");	
		
		params.put("page", String.valueOf(currentPage));
		HttpManage.getInstance().getNewsList(params, IndexFragment.this.getActivity());
	}
	
	
	public void handleTop(JSONObject result)
	{
		if (result == null)
		{
			ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), IndexFragment.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			if (success == false)
			{
				ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), result.getString("msg"));
				return ;
			}
			

			JSONArray list = result.getJSONObject("data").getJSONArray("list");
    		initPictureCarousel(list);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 首页商品列表
	 * 
	 */
	public void handleNewsList(JSONObject result, boolean isAdd)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), IndexFragment.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			if (success == false)
			{
				ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), result.getString("msg"));
				return ;
			}
			
			JSONObject data = result.getJSONObject("data");
			
			long current_time = data.getLong("current_time");
			
			Date current_time_date = new Date(current_time * 1000L);
			HuiConstants.NOW_TIME = current_time_date;
			
			news = data.getJSONArray("list");
			newsAdapter.setData(news, isAdd);
			newsAdapter.notifyDataSetChanged();
			
			if (news == null || news.length() < HuiConstants.PAGE_SIZE)
				isFinish = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isLoad = false;
	}
	
	public void onRefresh() 
	{ 
		if(!isRefresh){
			isRefresh = true;
			getHomeTop();
			refreshIndexNews();
	   }  
	}  
	
	private void refreshIndexNews()
	{
		currentPage = 1;
		isFinish = false;
		getNewsList(false);
		new Handler().postDelayed(new Runnable() {  
			public void run() {  
				swipeLayout.setRefreshing(false);
				isRefresh = false;
			}  
		}, 1500); 
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




	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterBroadcast();
	}
	
	public void enableDisableSwipeRefresh(boolean enable) {
        if (swipeLayout != null) {
        	swipeLayout.setEnabled(enable);
        }
    }

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.txtCommentOutArea:
				restoreNewsList();
				break;
			case R.id.btnSend:
				int position =  (Integer) v.getTag();
				JSONObject jsonObj = (JSONObject) newsAdapter.getItem(position);
				String id = null;
				try {
					id = jsonObj.getString("id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				createComment(id);
				break;
		}
	}
	
	private void restoreNewsList()
	{
		if (newsAdapter.getEditPosition() > -1)
		{
			rlyContent.bringToFront();
			llyBottom.setVisibility(View.GONE);
			View view = IndexFragment.this.getActivity().getWindow().peekDecorView();
	        if (view != null) {
	            InputMethodManager inputmanger = (InputMethodManager) IndexFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
	        }
			autoNavi();
			
			newsAdapter.setEditPosition(-1);
			newsAdapter.notifyDataSetChanged(); 
		}
	}
	
	private void handlePraise(String curId, JSONObject result)
	{
 		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), IndexFragment.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			Toast.makeText(IndexFragment.this.getActivity(), result.getString("msg"), Toast.LENGTH_SHORT).show();	
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(IndexFragment.this.getActivity(), true);
    			}
    		}
    		else
    		{
    			List<JSONObject> data = newsAdapter.getData();
    			
    			if (data != null)
    			{
    				JSONObject item = null;
    				for (int i = 0; i < data.size(); i++)
    				{
    					item = data.get(i);
    					String id = item.getString("id");
    					if (id.equals(curId))
    					{
        					int praise = item.getInt("praise");
        					item.put("praise", praise + 1);
        					newsAdapter.notifyDataSetChanged();
        					break;
    					}
    				}
    			}
    		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void handleCreateComment(String id, JSONObject result)
	{
 		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), IndexFragment.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(IndexFragment.this.getActivity(), result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(IndexFragment.this.getActivity(), true);
    			}
    		}
    		else
    		{
    			restoreNewsList();
    			edtMessage.setText("");
    			Intent ittDetail = new Intent(IndexFragment.this.getActivity(), InfoArticleDetail.class);
				ittDetail.putExtra("messageId", id);
				ittDetail.putExtra("queryComment", true);
				IndexFragment.this.getActivity().startActivity(ittDetail);
    			
    		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createComment(String id)
	{
		String content = edtMessage.getText().toString().trim();
		
		if (StringUtil.isEmpty(content))
		{
			Toast.makeText(IndexFragment.this.getActivity(), IndexFragment.this.getActivity().getResources().getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
			return;
		}

		if (CommonUtil.openLogin(IndexFragment.this.getActivity(), false) == true)
			return;
		
		loading.show();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("token",  MyApplication.getInstance().getMember().getMemberMap().get("token"));
		params.put("content", content);
		HttpManage.getInstance().createComment(params, IndexFragment.this.getActivity());
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnPause = true;
		MobclickAgent.onPageStart(getResources().getString(R.string.main_home));
		MobclickAgent.onPause(IndexFragment.this.getActivity());       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isOnPause = false;
		MobclickAgent.onPageEnd(getResources().getString(R.string.main_home));
		MobclickAgent.onResume(IndexFragment.this.getActivity());       //统计时长
	}

	private void autoNavi()
	{
		if (IndexFragment.this.getActivity() instanceof MainActivity)
		{
			MainActivity main = (MainActivity)IndexFragment.this.getActivity();
			main.autoNavi();
		}
	}

	@Override
	public void open(int index) {
		llyBottom.setVisibility(View.VISIBLE);
		btnSend.setTag(index);
		
		autoNavi();
		txtCommentOutArea.bringToFront();
		edtMessage.requestFocus();

		if (android.os.Build.VERSION.SDK_INT >= 8)
		{  
			lvNews.smoothScrollToPosition(index + 1);
	    } else {  
	    	lvNews.setSelection(index + 1);  
	    }
		InputMethodManager imm = (InputMethodManager) IndexFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
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
				if (action.equals(HttpAction.NEWS_LIST))
				{
					handleNewsList(result, isAdd);
				}
				else if (action.equals(HttpAction.HOME_TOP))
				{
					handleTop(result);
				}
				else if (action.equals(HttpAction.COMMENT_CREATE) && isOnPause == false)
				{
					String id = intent.getStringExtra("id");
					handleCreateComment(id, result);
				}
				else if (action.equals(HttpAction.POST_PRAISE))
				{
					String id = intent.getStringExtra("id");
					handlePraise(id, result);
				}
				else if (HttpAction.TAE_LOGIN.equals(action))
				{
					HttpManage.getInstance().saveMember(result, IndexFragment.this.getActivity());
				}
			}
		}
	};
}