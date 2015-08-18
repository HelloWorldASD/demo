package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.NewsType;
import com.wshang.soybean.bean.ReadType;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.IndexFragment;
import com.wshang.soybean.main.LoginTaoBao;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.DateUtil;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.LoadingProgress;
import com.wshang.soybean.tools.StringUtil;
import com.wshang.soybean.tools.ToastUtil;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InfoArticleDetail extends LoginTaoBao implements  SwipeRefreshLayout.OnRefreshListener, View.OnClickListener
{
	private TextView txtHead;
	private ImageView ivPerson;
	private TextView txtNickName;
	private TextView txtAddTime;
	private TextView txtContent;
	private GridView gvNews;
	private SwipeRefreshLayout swipe_container;  
	private TextView txtCommentCount;
	private ListView lvComment;  
	private TextView txtPraiseCount;
	private LinearLayout llyNoComment;
	
	private NewsImageAdapter newsImageAdapter;
	private CommentAdapter commentAdapter;
	private JSONObject data;
	private JSONArray small_imgs = null;
	private ArrayList<String> big_imgs = null;
	private JSONArray comments = null;
	
	private boolean isRefresh = false;
    private boolean isLoad = false;
    private boolean loadState = false;
    private boolean isFinish = false;
	private int currentPage = 1;
	private Dialog loading = null;
	
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private String url = null; //h5分享链接
	private String messageId;//趣文id
	private int id;//存储在sqlite的id
	private String pushId;//推送消息id
	private String title;
	private boolean fromPush;//是否来自推送消息
	private boolean queryComment;//查看评论列表
	private JSONArray product_list;
	
	private RelativeLayout rlyContent;
	private LinearLayout llyFunction;
	private LinearLayout llyComment;
	private TextView txtCommentOutArea;
	private EditText edtMessage;
	private ImageView ivShare;
	private ImageView ivEdit;
	private ImageView ivSource;
	private RelativeLayout rlyPraise;
	private TextView txtSplit;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_article_detail);
		initView();
		initData();
	}
	
	private void initView()
	{
		txtHead = (TextView) findViewById(R.id.txtHead);
		swipe_container = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		View commentHeader = this.getLayoutInflater().inflate(R.layout.info_article_detail_top, null);
		ivPerson = (ImageView) commentHeader.findViewById(R.id.ivPerson);
		txtNickName = (TextView) commentHeader.findViewById(R.id.txtNickName);
		txtAddTime = (TextView) commentHeader.findViewById(R.id.txtAddTime);
		txtContent = (TextView) commentHeader.findViewById(R.id.txtContent);
		gvNews = (GridView) commentHeader.findViewById(R.id.gvNews);
		txtCommentCount = (TextView) commentHeader.findViewById(R.id.txtCommentCount);
		
		lvComment = (ListView) findViewById(R.id.lvComment);
		lvComment.addHeaderView(commentHeader);

		txtPraiseCount = (TextView) findViewById(R.id.txtPraiseCount);
		rlyContent = (RelativeLayout) findViewById(R.id.rlyContent);
		
		txtCommentOutArea = (TextView) findViewById(R.id.txtCommentOutArea);
		txtCommentOutArea.setOnClickListener(this);
		llyFunction = (LinearLayout) findViewById(R.id.llyFunction);
		llyComment = (LinearLayout) findViewById(R.id.llyComment);

		
		swipe_container.setOnRefreshListener(this);  
		loading = new LoadingProgress(this, R.style.LoadDialog, "");
		gvNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) 
			{
				
			}
		});
		
		gvNews.setHorizontalSpacing(10);
		gvNews.setVerticalSpacing(10);
		
		commentAdapter = new CommentAdapter(this, comments);
		lvComment.setAdapter(commentAdapter);
		edtMessage = (EditText) findViewById(R.id.edtMessage);
		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
		lvComment.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& isLoad == false
					&& loadState == true
					&& isFinish == false)
				{
					currentPage = currentPage + 1;
		        	loading.show();
					getCommentList(true);	
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int _visibleItemCount, int totalItemCount) {
			 	loadState = false;
                if ((firstVisibleItem + _visibleItemCount) == totalItemCount)
                {
                	loadState = true;
                }
			}
		});
		ivShare = (ImageView) findViewById(R.id.ivShare);
		ivEdit = (ImageView) findViewById(R.id.ivEdit);
		ivSource = (ImageView) findViewById(R.id.ivSource);
		rlyPraise = (RelativeLayout) findViewById(R.id.rlyPraise);
		txtSplit = (TextView) findViewById(R.id.txtSplit);
		llyNoComment = (LinearLayout) findViewById(R.id.llyNoComment);
		
		ivShare.setOnClickListener(this);
		ivEdit.setOnClickListener(this);
		ivSource.setOnClickListener(this);
		rlyPraise.setOnClickListener(this);
		cancelComment();
	}
	
	/**
	 * 
	 */
	private void registerBroadcast()
	{
		InfoArticleDetail.this.registerReceiver(receiver, new IntentFilter(HttpAction.POST_DETAIL));
		InfoArticleDetail.this.registerReceiver(receiver, new IntentFilter(HttpAction.COMMENT_LISTS));
		InfoArticleDetail.this.registerReceiver(receiver, new IntentFilter(HttpAction.COMMENT_CREATE));
		InfoArticleDetail.this.registerReceiver(receiver, new IntentFilter(HttpAction.POST_PRAISE));
	}
	
	/**
	 * 
	 */
	private void unRegisterBroadcast()
	{
		InfoArticleDetail.this.unregisterReceiver(receiver);
	}
	
	
	private void initData()
	{
		txtHead.setText(getResources().getString(R.string.short_essay));

		BitmapTools tools = new BitmapTools(InfoArticleDetail.this);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
		messageId = getIntent().getStringExtra("messageId");
		id = getIntent().getIntExtra("id", -1);
		pushId = getIntent().getStringExtra("pushId");
		fromPush = getIntent().getBooleanExtra("fromPush", false);
		queryComment = getIntent().getBooleanExtra("queryComment", false);
		getInfoDetail();
		getCommentList(false);
		setMessageRead();
		registerBroadcast();
		statisticsMessagesOpened();
	}
	
	private void setMessageRead()
	{
		Message mess = DbManage.getInstance(InfoArticleDetail.this).getMessageById(id);
		if (mess != null && mess.getRead() == ReadType.UN_READ.ordinal())
		{
			mess.setRead(ReadType.READ.ordinal());
			DbManage.getInstance(InfoArticleDetail.this).save(mess);
			Intent ittMessageRead = new Intent(HttpAction.MESSAGE_READ);
			ittMessageRead.putExtra("id", id);
    		sendBroadcast(ittMessageRead);
		}
	}
	
	
	
	private void getInfoDetail()
	{
		loading.show();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		HttpManage.getInstance().getNewsDetail(params, InfoArticleDetail.this);
	}
	
	private void handleInfoDetail(JSONObject result)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			if (success == false)
			{
				ToastUtil.makeFailToastThr(InfoArticleDetail.this, result.getString("msg"));
				return ;
			}
			
			data = result.getJSONObject("data");
    		initContent();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getCommentList(final boolean isAdd)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		params.put("page", String.valueOf(currentPage));
		HttpManage.getInstance().getCommentList(params, isAdd, InfoArticleDetail.this);
	}
	
	private void handleCommentList(JSONObject result, boolean isAdd)
	{
		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
			if (success == false)
			{
				ToastUtil.makeFailToastThr(InfoArticleDetail.this, result.getString("msg"));
				return ;
			}
			
			JSONObject data = result.getJSONObject("data");
			JSONArray list = data == null? null : data.getJSONArray("list");
			if (isAdd == false)
			{
				int count = data.getInt("count");
				txtCommentCount.setText(count == 0 ? "" : "(" + String.valueOf(count) + ")");
			}
			
    		commentAdapter.setData(list, isAdd);
    		commentAdapter.notifyDataSetChanged();
    		if (isAdd == true)
    			lvComment.setSelection(commentAdapter.getCount());
    		
    		if (list == null || list.length() < HuiConstants.PAGE_SIZE)
				isFinish = true;
    		
    		if (queryComment == true)
    		{
    			lvComment.setSelection(commentAdapter.getCount() == 0 ? 0 : 1);
    			queryComment = false;
    		}
    		
    		if (isAdd == false)
    		{
    			if ((list == null || list.length() == 0))
    			{
    				llyNoComment.setVisibility(View.VISIBLE);
    				lvComment.setDividerHeight(0);
    			}
    			else
    			{
    				llyNoComment.setVisibility(View.GONE);
    				lvComment.setDividerHeight(1);
    			}
    		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initContent()
	{
		try {
			String nickname = data.getString("nickname");
			String face = data.getString("face");
			product_list = data.getJSONArray("product_list");
			if (product_list != null && product_list.length() > 0)
			{
				ivSource.setImageResource(R.drawable.eye);
			}
			else
			{
				ivSource.setImageResource(R.drawable.no_eye);
			}
			
			long current_time = data.getLong("current_time");
			Date current_time_date = new Date(current_time * 1000L);
			HuiConstants.NOW_TIME = current_time_date;
			
			
			long pub_time = data.getLong("pub_time");
			
			String content = data.getString("content");
			title = StringUtil.interceptContent(content,HuiConstants.INTERCEPT_SIZE);
			int praise = data.getInt("praise");
			
			small_imgs = data.getJSONArray("small_imgs");
			big_imgs = new ArrayList<String>();
			JSONArray big_imgs_array = data.getJSONArray("big_imgs");
			if (big_imgs_array != null)
			{
				int count = big_imgs_array.length();
				for (int i = 0; i < count; i++)
				{
					big_imgs.add(big_imgs_array.getString(i));
				}
			}
			
			newsImageAdapter = new NewsImageAdapter(this, small_imgs, big_imgs);
			gvNews.setAdapter(newsImageAdapter);
			
			int height = gvNews.getLayoutParams().height;
			int small_count = small_imgs.length();
			int multiple = 0;
			
			if (small_count  % 3 == 0)
			{
				multiple = small_count / 3;
			}
			else 
			{
				multiple = (small_count / 3) + 1;
			}
			
			gvNews.getLayoutParams().height = ((IndexFragment.gvWidth / 3) + 10) * multiple;
			
			newsImageAdapter.notifyDataSetChanged();
			
			url = data.getString("url");
			
			Date  pub_time_date = new Date(pub_time * 1000L);
			String show_time = DateUtil.getFormatDate(HuiConstants.NOW_TIME, pub_time_date);
			
			txtAddTime.setText(show_time);
			bitmapUtils.display(ivPerson, face, config);
			txtNickName.setText(nickname);
			txtContent.setText(content);
			txtPraiseCount.setText(praise == 0 ? "" : String.valueOf(praise));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void onRefresh() {
		if(!isRefresh){
			currentPage = 1;
			isRefresh = true;
			isFinish = false;
			getCommentList(false);
			new Handler().postDelayed(new Runnable() {  
				public void run() {  
					swipe_container.setRefreshing(false);
					isRefresh = false;
				}  
			}, 1500); 
	   } 
	}


	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.ivShare:
				Intent ittShare = new Intent(this, NewsShare.class);
				ittShare.putExtra("wapUrl", url);
				ittShare.putExtra("id", messageId);
				ittShare.putExtra("title", StringUtil.interceptContent(txtContent.getText().toString(), HuiConstants.INTERCEPT_SIZE));
				ittShare.putExtra("type", NewsType.SHORT.ordinal());
				startActivity(ittShare);
				break;
			case R.id.ivEdit:
				llyFunction.setVisibility(View.GONE);
				txtCommentOutArea.bringToFront();
				llyComment.setVisibility(View.VISIBLE);
				txtSplit.setVisibility(View.GONE);
				edtMessage.requestFocus();
				InputMethodManager imm = (InputMethodManager) InfoArticleDetail.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
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
	
	@SuppressLint("ShowToast") private void openSource()
	{
		if (product_list != null)
		{
			int count = product_list.length();
			if (count == 0)
			{
				Toast.makeText(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.no_source), Toast.LENGTH_SHORT).show();
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
			Toast.makeText(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.no_source), Toast.LENGTH_SHORT).show();
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
	
	private void cancelComment()
	{
		rlyContent.bringToFront();
		llyFunction.setVisibility(View.VISIBLE);
		llyComment.setVisibility(View.GONE);
		txtSplit.setVisibility(View.VISIBLE);
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
        imm.hideSoftInputFromWindow(edtMessage.getWindowToken(), 0);   
	}
	
	public void sendPraise()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("deviceid", CommonUtil.getMac());
		HttpManage.getInstance().sendPraise(params, InfoArticleDetail.this);
	}
	
	/**
	 * 
	 * @param result
	 */
	public void handleSendPraise(JSONObject result)
	{
		if (result == null)
		{
			ToastUtil.makeFailToastThr(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			Toast.makeText(InfoArticleDetail.this, result.getString("msg"), Toast.LENGTH_SHORT).show();	
    			return ;
    		}
    		else 
    		{
    			int praise = data.getInt("praise");
    			data.put("praise", praise + 1);
    			txtPraiseCount.setText(String.valueOf(praise + 1));
    		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void handleCreateComment(JSONObject result)
	{
 		loading.dismiss();
		if (result == null)
		{
			ToastUtil.makeFailToastThr(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(InfoArticleDetail.this, result.getString("msg"));
    			int code = result.getInt("code");
    			if (code == -1)
    			{
    				CommonUtil.openLogin(InfoArticleDetail.this, true);
    			}
    		}
    		else
    		{
    			cancelComment();
    			edtMessage.setText("");
    			loading.show();
    			currentPage = 1;
    			queryComment = true;
    			getCommentList(false);
    		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createComment()
	{
		String content = edtMessage.getText().toString().trim();
		
		if (StringUtil.isEmpty(content))
		{
			Toast.makeText(InfoArticleDetail.this, InfoArticleDetail.this.getResources().getString(R.string.comment_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (CommonUtil.openLogin(InfoArticleDetail.this, false) == true)
			return;
		
		loading.show();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("token",  MyApplication.getInstance().getMember().getMemberMap().get("token"));
		params.put("content", content);
		HttpManage.getInstance().createComment(params, InfoArticleDetail.this);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(InfoArticleDetail.this.getLocalClassName());
		MobclickAgent.onPause(InfoArticleDetail.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(InfoArticleDetail.this.getLocalClassName());
		MobclickAgent.onResume(InfoArticleDetail.this);       //统计时长
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterBroadcast();
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
				if (action.equals(HttpAction.POST_DETAIL))
				{
					handleInfoDetail(result);
				}
				else if (action.equals(HttpAction.COMMENT_LISTS))
				{
					handleCommentList(result, isAdd);
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
    	HttpManage.getInstance().statistics(params, InfoArticleDetail.this);
	}
}
