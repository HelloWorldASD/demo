package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.DateUtil;

public class CommentAdapter extends BaseAdapter
{
	/**
	 * 上下文对象
	 */
	private Activity curAct = null;
	private LayoutInflater mInflater;
	private List<JSONObject> data;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	
	/**
	 * 
	 * @param act
	 * @param news
	 */
	public CommentAdapter(Activity act, JSONArray comments) 
	{
		curAct = act;
		this.mInflater = LayoutInflater.from(curAct);
		BitmapTools tools = new BitmapTools(act);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
		
		data = new ArrayList<JSONObject>();
		if (comments != null)
		{
			for (int i = 0; i < comments.length(); i++)
			{
				try {
					JSONObject jsonObj = (JSONObject) comments.get(i);
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
					data.add(jsonObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
			convertView = mInflater.inflate(R.layout.comment_item, parent, false);
			holder = new ViewHolder();
			holder.ivPerson = (ImageView) convertView.findViewById(R.id.ivPerson);
			holder.txtNickName = (TextView) convertView.findViewById(R.id.txtNickName);
			holder.txtAddTime  = (TextView) convertView.findViewById(R.id.txtAddTime);
			holder.txtContent = (TextView)  convertView.findViewById(R.id.txtContent);

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		
		JSONObject jsonObj = data.get(position);

		try {

			String nickname = jsonObj.getString("nickname");
			String face = jsonObj.getString("face");
			String content = jsonObj.getString("content");
			long create_time = jsonObj.getLong("create_time");
			
			Date  create_time_date = new Date(create_time * 1000L);
			String show_time = DateUtil.formatDateByFormat(create_time_date, "MM-dd HH:mm");
			
			bitmapUtils.display(holder.ivPerson, face, config);
			holder.txtNickName.setText(nickname);
			holder.txtAddTime.setText(show_time);
			holder.txtContent.setText(content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	
	class ViewHolder
	{
		public ImageView     ivPerson;
		public TextView      txtNickName;
		public TextView      txtAddTime;
		public TextView      txtContent;
	}
}
