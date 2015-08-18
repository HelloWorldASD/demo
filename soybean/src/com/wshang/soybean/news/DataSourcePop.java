package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.HttpManage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class DataSourcePop extends Activity   
{
	@ViewInject(R.id.lvSource)
	private ListView lvSource;
	private String postId;
	private ArrayList<String> product_ids;
	private ArrayList<String> titles;
	private ArrayList<String> urls;
	private List<JSONObject> data;
	private DataSourceAdapter adapter;
	
    /**
     * 
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_source_pop);
		ViewUtils.inject(this);

		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ShareSDK.stopSDK(this);
	}

	private void initView() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		getWindow().setAttributes(lp);
		
	}
	
	private void initData()
	{
		postId = getIntent().getStringExtra("id");
		product_ids = getIntent().getStringArrayListExtra("product_ids");
		titles = getIntent().getStringArrayListExtra("titles");
		urls = getIntent().getStringArrayListExtra("urls");
		if (titles != null)
		{
			int count = titles.size();
			data = new ArrayList<JSONObject>();
			JSONObject newDataSourceItem = null;
			for (int i = 0; i < count; i++)
			{
				newDataSourceItem = new JSONObject();
				try {
					newDataSourceItem.put("title", titles.get(i));
					newDataSourceItem.put("url", urls.get(i));
					data.add(newDataSourceItem);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		adapter = new DataSourceAdapter(this, data);
		lvSource.setAdapter(adapter);
		lvSource.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				String product_id = product_ids.get(position);
				Map<String, String> params = new HashMap<String, String>();
		    	params.put("postId", postId);
		    	params.put("product_id", product_id);
		    	params.put("id", String.valueOf(StatisticsType.SingleContentEnterNumberTaobao.value()));
		     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		    	params.put("deviceid", CommonUtil.getMac());
		    	HttpManage.getInstance().statistics(params, DataSourcePop.this);
				
				String url = urls.get(position);
				HttpManage.getInstance().openTaoBaoH5(DataSourcePop.this, url);
				DataSourcePop.this.finish();
			}
		});
		statisticsSingleContentOnlookers();
	}
	
	private void statisticsSingleContentOnlookers()
	{
		Map<String, String> params = new HashMap<String, String>();
    	params.put("postId", postId);
    	params.put("id", String.valueOf(StatisticsType.SingleContentClickNumberOnlookers.value()));
     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	HttpManage.getInstance().statistics(params, DataSourcePop.this);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(DataSourcePop.this.getLocalClassName());
		MobclickAgent.onPause(DataSourcePop.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(DataSourcePop.this.getLocalClassName());
		MobclickAgent.onResume(DataSourcePop.this);       //统计时长
	}
}
