package com.wshang.soybean.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.ReadType;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.DateUtil;


public class MessageAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater;
	private List<Message> data;
	private Activity curActivity;
	private int rightWidth = 0;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	
	public MessageAdapter(Activity parentActivity, List<Message> messageList, int rightWidth) 
	{
		curActivity = parentActivity;
		this.rightWidth = rightWidth;
		this.mInflater = LayoutInflater.from(curActivity);
		this.data =  messageList == null ? new ArrayList<Message>() : messageList;
		
		BitmapTools tools = new BitmapTools(parentActivity);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
		
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingDrawable(parentActivity.getResources().getDrawable(R.drawable.pic_loading));
	}
	
	public List<Message> getData() {
		return data;
	}
	
	public void setData(List<Message> data) {
		this.data = data;
	}

	public void setData(List<Message> messageList, boolean isAdd)
	{
		if (isAdd == false)
			data.clear();
		
		if (messageList != null)
		{
			for (Message mess: messageList)
			{
				data.add(mess);
			}
		}
	}
	
	@Override
	public int getCount()
	{
		if(data == null){
			return 0;
		}
		return data.size();
	}
	@Override
	public Object getItem(int position) 
	{
		return data.get(position);
	}
	@Override
	public long getItemId(int position) 
	{
		return 0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null)
		{
			convertView	= mInflater.inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
			holder.ivPerson = (ImageView) convertView.findViewById(R.id.ivPerson);
			holder.ivUnRead = (ImageView) convertView.findViewById(R.id.ivUnRead);
			holder.txtNickName = (TextView) convertView.findViewById(R.id.txtNickName);
			holder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
			holder.llyLeft = (LinearLayout) convertView.findViewById(R.id.llyLeft);
			holder.rlyRight = (RelativeLayout) convertView.findViewById(R.id.rlyRight);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		LinearLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.llyLeft.setLayoutParams(lp1);
		LinearLayout.LayoutParams lp2 = new LayoutParams(rightWidth,
				LayoutParams.MATCH_PARENT);
		holder.rlyRight.setLayoutParams(lp2);
		
		holder.rlyRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		
		Message item = data.get(position);
		String content = item.getContent();
		String nickName = item.getNickName();
		long create_time = item.getTime();
		Date create_time_date = new Date(create_time * 1000L);
		
		String str_create_time = DateUtil.formatDateByFormat(create_time_date, "MM-dd hh:mm");

		String face = item.getFace();
		bitmapUtils.display(holder.ivPerson, face, config);
		if (item.getRead() == ReadType.UN_READ.ordinal())
		{
			holder.ivUnRead.setVisibility(View.VISIBLE);
		}
		else 
		{
			holder.ivUnRead.setVisibility(View.GONE);
		}
		
		holder.txtNickName.setText(nickName);
		holder.txtTime.setText(str_create_time);
		holder.txtContent.setText(content);
		return convertView;
	}
	
	class ViewHolder
	{
		public LinearLayout llyLeft;
		public ImageView ivPerson;
		public ImageView ivUnRead;
		public TextView txtNickName;
		public TextView  txtContent;
		public TextView  txtTime;
		
		public RelativeLayout rlyRight;
	}
	
	
	
	public void setRightWidth(int rightWidth) {
		this.rightWidth = rightWidth;
	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
}



