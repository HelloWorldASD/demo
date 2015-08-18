package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;
import com.wshang.soybean.main.IndexFragment;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.HuiConstants;

public class NewsImageAdapter extends BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Activity curAct = null;
	private LayoutInflater    mInflater;
	private JSONArray small_imgs;
	private ArrayList<String> big_imgs = null;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	
	/**
	 * 
	 * @param act
	 * @param product
	 * @param rightWidth
	 */
	public NewsImageAdapter(Activity act,JSONArray small_imgs, ArrayList<String> big_imgs) 
	{
		curAct = act;
		
		this.mInflater = LayoutInflater.from(curAct);
		BitmapTools tools = new BitmapTools(act);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
		
		this.small_imgs = small_imgs;
		this.big_imgs = big_imgs;
	}

	@Override
	public int getCount()
	{
		if(small_imgs == null){
			return 0;
		}
		return small_imgs.length();
	}
	@Override
	public Object getItem(int position) 
	{
		Object result = null;
		try {
			result = small_imgs.getString(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.news_img_item, parent, false);
			holder = new ViewHolder();
			holder.ivNews = (ImageView) convertView.findViewById(R.id.ivNews);
			holder.ivNews.getLayoutParams().height = IndexFragment.gvWidth / 3;
			holder.ivNews.getLayoutParams().width = IndexFragment.gvWidth / 3;  
			
			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		
		String img = null;
		try {
			img = small_imgs.getString(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bitmapUtils.display(holder.ivNews, img, config);
		holder.ivNews.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ittPictureAlbum = new Intent(curAct, ShowBigPhoto.class);
				ittPictureAlbum.putExtra("listImages", big_imgs);
				ittPictureAlbum.putExtra("position", position);
				curAct.startActivity(ittPictureAlbum);
			}
		});

		return convertView;
	}
	
	
	class ViewHolder
	{
		public ImageView ivNews;
	}
}
