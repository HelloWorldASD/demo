package com.wshang.soybean.news;

import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.ReadType;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TextInfoArticleDetail extends Activity implements View.OnClickListener
{
	@ViewInject(R.id.txtHead)
	private TextView txtHead;
	@ViewInject(R.id.txtContent)
	private TextView txtContent;
	
	private int id;//存储在sqlite的id
	private boolean fromPush;//是否来自推送消息
	private String pushId;//推送消息id
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_info_article_detail);
		ViewUtils.inject(this);
		initData();
	}
	
	private void initData()
	{
		content = getIntent().getStringExtra("content");
		id = getIntent().getIntExtra("id", -1);
		fromPush = getIntent().getBooleanExtra("fromPush", false);
		pushId = getIntent().getStringExtra("pushId");
		initContent();
		setMessageRead();
		statisticsMessagesOpened();
	}
	
	private void setMessageRead()
	{
		Message mess = DbManage.getInstance(TextInfoArticleDetail.this).getMessageById(id);
		if (mess != null && mess.getRead() == ReadType.UN_READ.ordinal())
		{
			mess.setRead(ReadType.READ.ordinal());
			DbManage.getInstance(TextInfoArticleDetail.this).save(mess);
			
			Intent ittMessageRead = new Intent(HttpAction.MESSAGE_READ);
			ittMessageRead.putExtra("id", id);
    		sendBroadcast(ittMessageRead);
		}
	}
	
	private void initContent()
	{
		txtContent.setText(content);
		txtHead.setText("消息详情");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(TextInfoArticleDetail.this.getLocalClassName());
		MobclickAgent.onPause(TextInfoArticleDetail.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(TextInfoArticleDetail.this.getLocalClassName());
		MobclickAgent.onResume(TextInfoArticleDetail.this);       //统计时长
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.ivBack:
				finish();
				break;
		}
	}
	
	private void statisticsMessagesOpened()
	{
		if (fromPush == false)
			return ;
		
		Map<String, String> params = new HashMap<String, String>();
    	params.put("id", String.valueOf(StatisticsType.SingleNumberMessagesOpened.value()));
     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
     	params.put("pushId", pushId);
    	params.put("deviceid", CommonUtil.getMac());
    	HttpManage.getInstance().statistics(params, TextInfoArticleDetail.this);
	}
}
