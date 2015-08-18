package com.wshang.soybean.news;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.wshang.soybean.R;

public class ContentSourceAdapter extends BaseAdapter
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
	public ContentSourceAdapter(Activity act, List<JSONObject> data) 
	{
		curAct = act;
		this.mInflater = LayoutInflater.from(curAct);
		this.data = data;
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
			convertView = mInflater.inflate(R.layout.content_source_item, parent, false);
			holder = new ViewHolder();

			holder.txtGoodsNameTitle = (TextView) convertView.findViewById(R.id.txtGoodsNameTitle);
			holder.edtGoodsName  = (EditText) convertView.findViewById(R.id.edtGoodsName);
			holder.txtGoodsUrl = (TextView)  convertView.findViewById(R.id.txtGoodsUrl);
			holder.edtGoodsUrl  = (EditText) convertView.findViewById(R.id.edtGoodsUrl);
			holder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		
		JSONObject jsonObj = data.get(position);

		try {

			String title = jsonObj.has("title") ? jsonObj.getString("title") : "";
			String url = jsonObj.has("url") ? jsonObj.getString("url") : "";
			holder.edtGoodsName.setText(title);
			holder.edtGoodsName.setTag(position);
			holder.edtGoodsUrl.setText(url);
			holder.edtGoodsUrl.setTag(position);
			
			holder.edtGoodsName.addTextChangedListener(new EditChangedListener(holder.edtGoodsName));
			holder.edtGoodsUrl.addTextChangedListener(new EditChangedListener(holder.edtGoodsUrl));
			holder.ivDelete.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					data.remove(position);
					ContentSourceAdapter.this.notifyDataSetChanged();
				}
			});
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	class ViewHolder
	{
		public TextView  txtGoodsNameTitle;
		public EditText  edtGoodsName;
		public TextView  txtGoodsUrl;
		public EditText  edtGoodsUrl;
		public ImageView ivDelete;
	}
	
	class EditChangedListener implements TextWatcher
	{  
		private EditText curEdt;
		
		public  EditChangedListener(EditText curEdt)
		{
			this.curEdt = curEdt;
		}
		
        @Override  
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {  

        }  
  
        @Override  
        public void onTextChanged(CharSequence s, int start, int before, int count) 
        {  
    	    int position = (Integer) curEdt.getTag();
    	    JSONObject item = data.get(position);
    	    switch (curEdt.getId()) 
    	    {
				case R.id.edtGoodsName:
					try {
						item.put("title", curEdt.getText().toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case R.id.edtGoodsUrl:
					try {
						item.put("url", curEdt.getText().toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
		    }
        }  
  
        @Override  
        public void afterTextChanged(Editable s) {  
  
        }  
    }; 
}
