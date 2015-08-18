package com.wshang.soybean.bean;

import com.wshang.soybean.my.PraiseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Text_Receive_ViewHolder extends BaseViewHolder
{
	public Text_Receive_ViewHolder()
	{
		type = PraiseAdapter.TEXT_RECEIVE_TYPE;
	}
	
	// 文本
	public TextView text_receive_txtSendTime;
	public TextView text_receive_txtContent;
	public ImageView ivHeader; 
}
