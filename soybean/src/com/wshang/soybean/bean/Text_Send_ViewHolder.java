package com.wshang.soybean.bean;

import com.wshang.soybean.my.PraiseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Text_Send_ViewHolder extends BaseViewHolder
{
	public Text_Send_ViewHolder()
	{
		type = PraiseAdapter.TEXT_SEND_TYPE;
	}
	
	// 文本
	public TextView text_send_txtSendTime;
	public TextView text_send_txtContent;
	public ImageView ivHeader; 
}
