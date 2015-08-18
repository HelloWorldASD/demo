package com.wshang.soybean.news;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.NewsType;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.IndexFragment;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.DateUtil;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.StringUtil;

public class NewsAdapter extends BaseAdapter implements View.OnClickListener {
	/**
	 * 上下文对象
	 */
	private Activity curAct = null;
	private LayoutInflater mInflater;
	private List<JSONObject> data;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private int editPosition = -1;
	private IComment iComment;

	/**
	 * 
	 * @param act
	 * @param news
	 */
	public NewsAdapter(Activity act, IComment iComment, JSONArray news) 
	{
		curAct = act;
		this.iComment = iComment;
		this.mInflater = LayoutInflater.from(curAct);
		BitmapTools tools = new BitmapTools(act);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();

		data = new ArrayList<JSONObject>();
		if (news != null)
		{
			for (int i = 0; i < news.length(); i++)
			{
				try {
					JSONObject jsonObj = (JSONObject) news.get(i);
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


	public List<JSONObject> getData() {
		return data;
	}

	public void setData(List<JSONObject> data) {
		this.data = data;
	}

	public int getEditPosition() {
		return editPosition;
	}

	public void setEditPosition(int editPosition) {
		this.editPosition = editPosition;
	}

	@Override
	public int getCount()
	{
		if(data == null){
			return 0;
		}
		else
			return data.size();

		//		if (editPosition > -1)
		//			return editPosition + 1;
		//		else
		//			return data.size();
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
			convertView = mInflater.inflate(R.layout.news_item, parent, false);

			holder = new ViewHolder();
			holder.ivPerson = (ImageView) convertView.findViewById(R.id.ivPerson);
			holder.txtNickName = (TextView) convertView.findViewById(R.id.txtNickName);
			holder.txtAddTime  = (TextView) convertView.findViewById(R.id.txtAddTime);
			holder.txtContent = (TextView)  convertView.findViewById(R.id.txtContent);
			holder.gvNews  = (GridView)  convertView.findViewById(R.id.gvNews);

			holder.ivShare = (ImageView) convertView.findViewById(R.id.ivShare);
			holder.ivShare.setOnClickListener(this);
			holder.ivEdit = (ImageView) convertView.findViewById(R.id.ivEdit);
			holder.ivEdit.setOnClickListener(this);
			holder.ivSource = (ImageView) convertView.findViewById(R.id.ivSource);
			holder.ivSource.setOnClickListener(this);
			holder.rlyPraise = (RelativeLayout) convertView.findViewById(R.id.rlyPraise);
			holder.ivPraise = (ImageView) convertView.findViewById(R.id.ivPraise);
			holder.rlyPraise.setOnClickListener(this);
			holder.txtPraiseCount = (TextView)  convertView.findViewById(R.id.txtPraiseCount);

			holder.llyFunction = (LinearLayout)  convertView.findViewById(R.id.llyFunction);
			holder.txtItemSplit = (TextView)  convertView.findViewById(R.id.txtItemSplit);


			holder.txtItemSplit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}

		JSONObject jsonObj = data.get(position);

		try {
			holder.ivShare.setTag(position);
			holder.rlyPraise.setTag(position);
			holder.ivSource.setTag(position);
			holder.ivEdit.setTag(position);

			if (position != getCount() -1)
			{
				holder.txtItemSplit.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.txtItemSplit.setVisibility(View.GONE);
			}

			String nickname = jsonObj.getString("nickname");
			String face = jsonObj.getString("face");
			JSONArray product_list = jsonObj.getJSONArray("product_list");
			if (product_list != null && product_list.length() > 0)
			{
				holder.ivSource.setImageResource(R.drawable.eye);
			}
			else
			{
				holder.ivSource.setImageResource(R.drawable.no_eye);
			}


			String content = jsonObj.getString("content");
			//			String url = jsonObj.getString("url");
			long pub_time = jsonObj.getLong("pub_time");

			Date  pub_time_date = new Date(pub_time * 1000L);
			String show_time = DateUtil.getFormatDate(HuiConstants.NOW_TIME, pub_time_date);
			int praise = jsonObj.getInt("praise");

			bitmapUtils.display(holder.ivPerson, face, config);
			holder.txtNickName.setText(nickname);
			holder.txtAddTime.setText(show_time);
			holder.txtContent.setText(content);
			if(praise > 0)
				holder.txtPraiseCount.setText(String.valueOf(praise));
			else 
				holder.txtPraiseCount.setText("");

			JSONArray small_imgs = jsonObj.getJSONArray("small_imgs");
			JSONArray big_img_array = jsonObj.getJSONArray("big_imgs");
			ArrayList<String> big_imgs = new ArrayList<String>();
			if (big_img_array != null)
			{
				int count = big_img_array.length();
				for (int i = 0; i < count; i++)
				{
					big_imgs.add(big_img_array.getString(i));
				}
			}

			NewsImageAdapter newsImageAdapter = new NewsImageAdapter(curAct, small_imgs, big_imgs);

			Object tag =  holder.gvNews.getTag();
			int height = 0;
			if (tag == null)
			{
				height = holder.gvNews.getLayoutParams().height;
				holder.gvNews.setTag(height);
			}
			else 
			{
				height = (Integer)tag;
			}
			int small_count = small_imgs.length();
			int multiple = 0;

			if (small_count  % 3 == 0)
			{
				multiple = small_count / 3;
			}
			else 
			{
				multiple = (small_count / 3) + 1;
			}

			holder.gvNews.getLayoutParams().height = ((IndexFragment.gvWidth / 3) + 10) * multiple;

			holder.gvNews.setHorizontalSpacing(10);
			holder.gvNews.setVerticalSpacing(10);
			holder.gvNews.setColumnWidth(IndexFragment.gvWidth / 3);
			holder.gvNews.setAdapter(newsImageAdapter);

			final String id=jsonObj.optString("id");

			holder.gvNews.setOnTouchListener(new OnTouchListener() {
				boolean isClick=false;
				@Override
				public boolean onTouch(View view, MotionEvent event) {

					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						isClick=true;
						break;
					case MotionEvent.ACTION_UP:
						if(id.length()>0 && isClick){
							Intent ittDetail = new Intent(curAct, InfoArticleDetail.class);
							ittDetail.putExtra("messageId", id);
							curAct.startActivity(ittDetail);	
						}
						break;
					default:
						isClick=false;
						break;
					}
					return false;
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
		public ImageView     ivPerson;
		public TextView      txtNickName;
		public TextView      txtAddTime;
		public TextView      txtContent;
		public TextView      txtSplit;
		public GridView      gvNews;

		public ImageView     ivShare;
		public ImageView     ivEdit;
		public ImageView     ivSource;
		public ImageView     ivPraise;
		public TextView      txtPraiseCount;
		public RelativeLayout rlyPraise;

		public LinearLayout  llyFunction;
		public TextView      txtItemSplit; 
	}

	@SuppressLint("ShowToast") @Override
	public void onClick(View v) 
	{
		int position = (Integer) v.getTag();
		JSONObject jsonObj = data.get(position);
		String id = null;

		try {
			id = jsonObj.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		switch (v.getId()) 
		{
		case R.id.ivShare:
			String url = null;
			try {
				url = jsonObj.getString("url");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String content = null;
			try {
				content = jsonObj.getString("content");
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			String shareContent = StringUtil.interceptContent(content, HuiConstants.INTERCEPT_SIZE);

			Intent ittShare = new Intent(curAct, NewsShare.class);
			ittShare.putExtra("wapUrl", url);
			ittShare.putExtra("id", id);
			ittShare.putExtra("title", shareContent);
			ittShare.putExtra("type", NewsType.SHORT.ordinal());
			curAct.startActivity(ittShare);
			break;
		case R.id.ivEdit:
			editPosition = position;
			NewsAdapter.this.notifyDataSetChanged();
			if (iComment != null)
			{
				iComment.open(position);
			}
			break;
		case R.id.ivSource:
			JSONObject item = data.get(position);
			JSONArray product_list = null;

			try {
				product_list = item.getJSONArray("product_list");
			} catch (JSONException ex) {
				ex.printStackTrace();
			}

			int count = product_list == null ? 0 : product_list.length();
			if (count == 0)
			{
				Toast.makeText(curAct, curAct.getResources().getString(R.string.no_source), Toast.LENGTH_SHORT).show();
				statisticsSingleContentOnlookers(id);
				return ;
			}

			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<String> urls = new ArrayList<String>();
			ArrayList<String> product_ids = new ArrayList<String>();
			String title = null;
			String source_url = null;
			String product_id = null;
			for (int i = 0; i < count; i++)
			{
				try {
					jsonObj = product_list.getJSONObject(i);
					product_id = jsonObj.getString("product_id");
					title = jsonObj.getString("title");
					source_url = jsonObj.getString("url");
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				product_ids.add(product_id);
				titles.add(title);
				urls.add(source_url);
			}
			if (product_ids.size() == 1)
			{			
				HttpManage.getInstance().openTaoBaoH5(curAct, urls.get(0));
			}
			else
			{
				Intent ittSource = new Intent(curAct, DataSourcePop.class);
				ittSource.putExtra("titles", titles);
				ittSource.putExtra("urls", urls);
				ittSource.putExtra("product_ids", product_ids);
				ittSource.putExtra("id", id);
				curAct.startActivity(ittSource);
			}
			break;
		case R.id.rlyPraise:
			praise(id);
			break;
		}
	}

	private void statisticsSingleContentOnlookers(String messageId)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("postId", messageId);
		params.put("id", String.valueOf(StatisticsType.SingleContentClickNumberOnlookers.value()));
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("deviceid", CommonUtil.getMac());
		HttpManage.getInstance().statistics(params, curAct);
	}

	public void praise(String id)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("deviceid", CommonUtil.getMac());
		HttpManage.getInstance().sendPraise(params, curAct);
	}
}
