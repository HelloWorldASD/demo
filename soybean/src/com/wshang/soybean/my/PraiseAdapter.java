package com.wshang.soybean.my;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.BaseViewHolder;
import com.wshang.soybean.bean.Text_Receive_ViewHolder;
import com.wshang.soybean.bean.Text_Send_ViewHolder;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PraiseAdapter extends BaseAdapter
{
	private List<JSONObject> data;
	private Context curAct;
	private LayoutInflater mInflater;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	public static final int TEXT_SEND_TYPE = 0;
	public static final int TEXT_RECEIVE_TYPE = 1;

	public PraiseAdapter(Context ctx, JSONArray praiseData)
	{
		curAct = ctx;
		mInflater = LayoutInflater.from(curAct);
		BitmapTools tools = new BitmapTools(curAct);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
		
		data = new ArrayList<JSONObject>();
		if (praiseData != null)
		{
			for (int i = 0; i < praiseData.length(); i++)
			{
				try {
					JSONObject jsonObj = (JSONObject) praiseData.get(i);
					data.add(jsonObj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}



	public void setData(JSONArray result, boolean isAdd)
	{
		if (isAdd == false)
			data.clear();
		
		if (result != null)
		{
			for (int i = 0; i < result.length(); i++)
			{
				try {
					JSONObject jsonObj = (JSONObject) result.get(i);
					data.add(0, jsonObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void setData(List<JSONObject> data) {
		this.data = data;
	}



	public List<JSONObject> getData() {
		return data;
	}
	
	public void addNewData(JSONObject newData)
	{
		data.add(newData);
	}

	public int getCount()
	{
		return data.size();
	}


	public Object getItem(int position)
	{
		return data.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	private View sendInitTextMessage(Text_Send_ViewHolder text_Send_ViewHolder)
	{
		View convertView = mInflater.inflate(R.layout.chatting_item_msg_text_send, null);
		text_Send_ViewHolder.text_send_txtSendTime = (TextView) convertView.findViewById(R.id.txtSendTime);
		text_Send_ViewHolder.text_send_txtContent = (TextView) convertView.findViewById(R.id.txtContent);
		text_Send_ViewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.ivHeader);

		return convertView;
	}
	
	private View receiveInitTextMessage(Text_Receive_ViewHolder text_Receive_ViewHolder)
	{
		View convertView = mInflater.inflate(R.layout.chatting_item_msg_text_receive, null);
		text_Receive_ViewHolder.text_receive_txtSendTime = (TextView) convertView.findViewById(R.id.txtSendTime);
		text_Receive_ViewHolder.text_receive_txtContent = (TextView) convertView.findViewById(R.id.txtContent);
		text_Receive_ViewHolder.ivHeader = (ImageView) convertView.findViewById(R.id.ivHeader);

		return convertView;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		Text_Receive_ViewHolder text_Receive_ViewHolder = null;
		Text_Send_ViewHolder text_Send_ViewHolder = null;
		
		JSONObject item = data.get(position);
		int is_reply = -1;
		try {
			is_reply = item.getInt("is_reply");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (convertView == null)
		{
			switch (is_reply)
			{
				case TEXT_SEND_TYPE: //0为发送
					text_Send_ViewHolder = new Text_Send_ViewHolder();
					convertView = sendInitTextMessage(text_Send_ViewHolder);
					convertView.setTag(text_Receive_ViewHolder);
					break;
				case TEXT_RECEIVE_TYPE: //1为回复
					text_Receive_ViewHolder = new Text_Receive_ViewHolder();
					convertView = receiveInitTextMessage(text_Receive_ViewHolder);
					convertView.setTag(text_Receive_ViewHolder);
					break;
			}
		}
		else
		{
			BaseViewHolder baseHolder = (BaseViewHolder) convertView.getTag();
			switch (is_reply)
			{
				case TEXT_SEND_TYPE:
					if (baseHolder != null && baseHolder.type == TEXT_SEND_TYPE)
					{
						text_Send_ViewHolder = (Text_Send_ViewHolder) baseHolder;
					}
					else
					{
						text_Send_ViewHolder = new Text_Send_ViewHolder();
						convertView = sendInitTextMessage(text_Send_ViewHolder);
						convertView.setTag(text_Receive_ViewHolder);
					}
					break;
				case TEXT_RECEIVE_TYPE:
					if (baseHolder != null && baseHolder.type == TEXT_RECEIVE_TYPE)
					{
						text_Receive_ViewHolder = (Text_Receive_ViewHolder) baseHolder;
					}
					else
					{
						text_Receive_ViewHolder = new Text_Receive_ViewHolder();
						convertView = receiveInitTextMessage(text_Receive_ViewHolder);
						convertView.setTag(text_Receive_ViewHolder);
					}
					break;
			}
		}
		
		switch (is_reply)
		{
			case TEXT_SEND_TYPE:
				dispalySendMessage( position, text_Send_ViewHolder);
				break;
			case TEXT_RECEIVE_TYPE:
				dispalyReceiveMessage(position, text_Receive_ViewHolder);
				break;
		}
		
		
		return convertView;
	}
	
	private void dispalySendMessage(int index, Text_Send_ViewHolder text_Send_ViewHolder)
	{
		
		try {
			JSONObject item = data.get(index);
			long create_time = item.getLong("create_time");
			Date create_time_date = new Date(create_time * 1000L);
			
			String str_add_time = DateUtil.formatDateByFormat(create_time_date, "MM-dd HH:mm");
			String face = item.getString("face");
			text_Send_ViewHolder.text_send_txtSendTime.setText(str_add_time);
			text_Send_ViewHolder.text_send_txtContent.setText(item.getString("content"));
			bitmapUtils.display(text_Send_ViewHolder.ivHeader, face, config);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void dispalyReceiveMessage(int index, Text_Receive_ViewHolder text_Receive_ViewHolder)
	{
		try {
			JSONObject item = data.get(index);
			
			long create_time = item.getLong("create_time");
			Date create_time_date = new Date(create_time * 1000L);
			
			String str_add_time = DateUtil.formatDateByFormat(create_time_date, "MM-dd HH:mm");
			text_Receive_ViewHolder.text_receive_txtSendTime.setText(str_add_time);
			text_Receive_ViewHolder.text_receive_txtContent.setText(item.getString("content"));
			
			String face = item.getString("face");
			bitmapUtils.display(text_Receive_ViewHolder.ivHeader, face);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
