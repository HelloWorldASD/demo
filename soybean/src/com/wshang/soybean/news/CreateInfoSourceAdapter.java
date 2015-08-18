package com.wshang.soybean.news;


import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.wshang.soybean.R;

public class CreateInfoSourceAdapter extends BaseAdapter
{
	/**
	 * 上下文对象
	 */
	private Activity curAct = null;
	private LayoutInflater mInflater;
	private List<JSONObject> data;
	
	/**
	 * 
	 * @param act
	 * @param news
	 */
	public CreateInfoSourceAdapter(Activity act, List<JSONObject> data) 
	{
		curAct = act;
		this.data = data;
		this.mInflater = LayoutInflater.from(curAct);
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
			convertView = mInflater.inflate(R.layout.create_info_source_item, parent, false);
			holder = new ViewHolder();
			holder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
			holder.txtUrl  = (TextView) convertView.findViewById(R.id.txtUrl);

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		
		JSONObject jsonObj = data.get(position);
		try {
			String title = jsonObj.getString("title");
			String url = jsonObj.getString("url");
			holder.txtTitle.setText(title);
			holder.txtUrl.setText(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	
	class ViewHolder
	{
		public ImageView  ivDelete;
		public TextView  txtTitle;
		public TextView  txtUrl;
	}
}
