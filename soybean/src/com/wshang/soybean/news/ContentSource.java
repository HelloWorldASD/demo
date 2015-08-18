package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentSource extends Activity implements View.OnClickListener
{
	@ViewInject(R.id.txtRightTitle)
	private TextView txtRightTitle;
	
	@ViewInject(R.id.llyContent)
	private LinearLayout llyContent;
	@ViewInject(R.id.rlySource0)
	private RelativeLayout rlySource0;
	
	@ViewInject(R.id.lvSource)
	private ListView lvSource;
	
	private ContentSourceAdapter adapter;
	private List<JSONObject> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_source);
		ViewUtils.inject(this);
		initData();
	}
	
	private void initData()
	{
		txtRightTitle.setVisibility(View.VISIBLE);
		txtRightTitle.setText("确定");
		txtRightTitle.setOnClickListener(this);
		data = new ArrayList<JSONObject>();
		data.add(new JSONObject());
		adapter = new ContentSourceAdapter(ContentSource.this, data);
		lvSource.setAdapter(adapter);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(ContentSource.this.getLocalClassName());
		MobclickAgent.onPause(ContentSource.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(ContentSource.this.getLocalClassName());
		MobclickAgent.onResume(ContentSource.this);       //统计时长
	}
	

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.ivAdd:
				data.add(new JSONObject());
				adapter.notifyDataSetChanged();
				break;
			case R.id.txtRightTitle:
				if (data != null)
				{
					ArrayList<String> titles = new ArrayList<String>();
					ArrayList<String> urls = new ArrayList<String>();
					for (JSONObject item : data)
					{
						try {
							titles.add(item.getString("title"));
							urls.add(item.getString("url"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Intent ittData = getIntent();
					ittData.putExtra("titles", titles);
					ittData.putExtra("urls", urls);
					setResult(RESULT_OK, ittData);
					finish();
				}
				break;
			
		}
	}
}
