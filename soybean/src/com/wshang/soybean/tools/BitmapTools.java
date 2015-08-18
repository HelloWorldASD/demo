package com.wshang.soybean.tools;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;

public class BitmapTools
{
	private Context ctx;
	
	public BitmapTools(Context ctx)
	{
		this.ctx = ctx;
	}
	
	public BitmapUtils getBitmapUtils()
	{
		BitmapUtils utils  = new BitmapUtils(ctx);
		return utils;
	}
	
	public BitmapDisplayConfig getBitmapDisplayConfig()
	{
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingDrawable(ctx.getResources().getDrawable(R.drawable.pic_loading));
		config.setLoadFailedDrawable(ctx.getResources().getDrawable(R.drawable.pic_error));
		return config;
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
