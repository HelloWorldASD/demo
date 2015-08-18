package com.wshang.soybean.news;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.wshang.soybean.R;

public class CreateInfoPhotoAdapter extends BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Activity curAct = null;
	private LayoutInflater   mInflater;
	private List<JSONObject> data;
	
	/**
	 * 
	 * @param act
	 * @param product
	 * @param rightWidth
	 */
	public CreateInfoPhotoAdapter(Activity act, List<JSONObject> data) 
	{
		curAct = act;
		this.data = data;
		this.mInflater = LayoutInflater.from(curAct);
	}
	
	public void setData(List<JSONObject> incrementData)
	{
		if (data != null)
		{
			for (int i = 0; i < data.size(); i++)
			{
				data.addAll(incrementData);
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
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.create_info_photo_item, parent, false);
			holder = new ViewHolder();
			holder.ivNews = (ImageView) convertView.findViewById(R.id.ivNews);
			
			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		JSONObject item = data.get(position);
		
		Bitmap photo = null;
		try {
			photo = (Bitmap) item.get("photo");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		holder.ivNews.setImageBitmap(photo);
		return convertView;
	}
	
	
	class ViewHolder
	{
		public ImageView ivNews;
	}
}
