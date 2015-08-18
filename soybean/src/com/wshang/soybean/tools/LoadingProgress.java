package com.wshang.soybean.tools;

import com.wshang.soybean.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * 项目名称：Mojie
 * 
 * 类名称：LoadingProgress
 * 
 * 类描述： 自定义加载提示框
 * 
 * 创建人：梁鹏
 * 
 * 联系方式：
 * 
 * 创建时间：2014-4-18 上午9:58:16
 * 
 * 修改人：梁鹏
 * 
 * 修改时间：2014-4-18 上午9:58:16
 * 
 * 修改备注：
 * 
 * @version
 * 
 */
public class LoadingProgress extends Dialog {

	private TextView TVloadTip;

	public String loadTip = "加载中...";

	public LoadingProgress(Context context, int theme, String tips) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		if (tips != "")
		{
			loadTip = tips;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_dialog);
		TVloadTip = (TextView) findViewById(R.id.TVloadTip);
		TVloadTip.setText(loadTip);
		// setCanceledOnTouchOutside(false);// 点击不取消,具体引用的时候添加
	}

}
