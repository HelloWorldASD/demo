package com.wshang.soybean.news;

import java.util.ArrayList;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.umeng.analytics.MobclickAgent;
import com.wshang.soybean.R;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.HuiConstants;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class ShowBigPhoto extends Activity
{
	private ViewPager vpBigPhoto;
	private MyPagerAdapter pagerAdapter;
	private ArrayList<String> listImages = null;
	private ArrayList<View> listViews = null;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private int position = -1;
	private TextView txtCatalog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_big_photo);
		position = getIntent().getIntExtra("position", -1);
		BitmapTools tools = new BitmapTools(ShowBigPhoto.this);
		bitmapUtils  = tools.getBitmapUtils();
		config = tools.getBitmapDisplayConfig();
		vpBigPhoto = (ViewPager) findViewById(R.id.vpBigPhoto);
		listImages = getIntent().getStringArrayListExtra("listImages");
		if(listImages == null)
			finish();
		else{
			for(int i=0;i<listImages.size();i++)
				initListViews(listImages.get(i));
		}
		txtCatalog=(TextView) findViewById(R.id.txtCatalog);
		txtCatalog.setText(1+"/"+listImages.size());
		pagerAdapter = new MyPagerAdapter(listViews);
		vpBigPhoto.setAdapter(pagerAdapter);
		vpBigPhoto.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				txtCatalog.setText((arg0+1)+"/"+listImages.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
		
		if (position > 0)
		{
			vpBigPhoto.setCurrentItem(position);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(ShowBigPhoto.this.getLocalClassName());
		MobclickAgent.onPause(ShowBigPhoto.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(ShowBigPhoto.this.getLocalClassName());
		MobclickAgent.onResume(ShowBigPhoto.this);       //统计时长
	}
	
	private void initListViews(String image) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		
		ImageView ivPhoto = new ImageView(this);// 构造textView对象
		ivPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowBigPhoto.this.finish();
			}
		});
		bitmapUtils.display(ivPhoto, image, config);
		ivPhoto.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(ivPhoto);// 添加view
	}
	
	class MyPagerAdapter extends PagerAdapter{
		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPagerAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return size;
		}
		
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		
		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}
		
		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
		
	}
}
