package com.wshang.soybean.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.wshang.soybean.R;
import com.wshang.soybean.news.EssenceInfo;
import com.wshang.soybean.tools.BitmapTools;
import com.wshang.soybean.tools.StringUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SlideShowView extends FrameLayout 
{
    private final static int  TIME_INTERVAL  =  5;
    private final static boolean isAutoPlay = true; 
    private int  started = 0;
    
    private List<ImageView> imageViewsList;
    private List<ImageView> dotViewsList;
    private ViewPager viewPager;
    private int currentItem  = 0;
    private ScheduledExecutorService scheduledExecutorService = null;
    private int align = RelativeLayout.ALIGN_PARENT_RIGHT;
    private Fragment frag;
    private Context curCtx;
    private LinearLayout llyDot;
    private TextView txtTitle;
    private JSONArray data;
    
    private Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
    };
    
    public SlideShowView(Context context, Fragment frag, JSONArray data, int align) 
    {
        this(context, frag, null, 0, data, align);
    }
    public SlideShowView(Context context) 
    {
        this(context, null, null, 0, null, RelativeLayout.ALIGN_PARENT_RIGHT);
    }
    public SlideShowView(Context context, AttributeSet attrs) 
    {
        this(context, null, attrs, 0, null, RelativeLayout.ALIGN_PARENT_RIGHT);
    }
    public SlideShowView(Context context, Fragment frag, AttributeSet attrs, int defStyle,JSONArray data, int align) 
    {
        super(context, attrs, defStyle);
        this.curCtx = context;
        this.align = align;
    	this.frag = frag;
        initData();
        initUI(context, data);
        
        startPlay();
    }
    
    private void startPlay()
    {
    	started = 1;
    	scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), TIME_INTERVAL, TIME_INTERVAL,TimeUnit.SECONDS);
    }
    
    private void stopPlay()
    {
    	started = 0;
        scheduledExecutorService.shutdown();
    }
    
    private void initData()
    {
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList   = new ArrayList<ImageView>();
    }
    
    private void initUI(final Context context, JSONArray data)
    {
    	LayoutInflater.from(context).inflate(R.layout.slideshow, this, true);
    	this.data = data;
        int length     = data.length();
        if(length > 0){
        	llyDot = (LinearLayout) findViewById(R.id.llyDot);
        	txtTitle = (TextView) findViewById(R.id.txtTitle);
  
        	if (align == RelativeLayout.ALIGN_PARENT_RIGHT)
        	{
            	RelativeLayout.LayoutParams rlyLp = new RelativeLayout.LayoutParams
            			(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            	rlyLp.addRule(align); 
            	rlyLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); 
            	llyDot.setLayoutParams(rlyLp);
        	}


   
        	BitmapTools tools = new BitmapTools(curCtx);
        	BitmapUtils bitmapUtils  = tools.getBitmapUtils();
        	BitmapDisplayConfig config = tools.getBitmapDisplayConfig();
        	LinearLayout.LayoutParams lp = null;
        	
            for(int i=0; i < length; i++)
    		{ 
    		    try {
	                final JSONObject one = (JSONObject) data.get(i);
					String img = one.getString("img");
					ImageView view = new ImageView(context);
	                view.setScaleType(ScaleType.FIT_XY);

	                bitmapUtils.display(view, img, config);
	                
	                view.setOnClickListener(new OnClickListener() {
	    				@Override
	    				public void onClick(View v) 
	    				{
	    					String url = null;
	    					String post_id = null;
	    					String title = null;
	    					try 
	    					{
								url = one.getString("url");
								post_id = one.getString("post_id");
								title = one.getString("title");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    					if (StringUtil.isEmpty(url) == false)
	    					{
	    						Intent ittEssence = new Intent(curCtx, EssenceInfo.class);
	    						ittEssence.putExtra("url", url);
	    						ittEssence.putExtra("title", title);
	    						ittEssence.putExtra("messageId", post_id);
	    						curCtx.startActivity(ittEssence);
	    					}
	    				}
	    			});
	                imageViewsList.add(view);

	                ImageView dot = new ImageView(context);
	                if (i != length -1)
	                {
	                	lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	                	lp.setMargins(0, 0, 15, 0);
	                	dot.setLayoutParams(lp);
	                 	
	                }
	                else
	                {
	                	lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	                	lp.setMargins(0, 0, 0, 0);
	                	dot.setLayoutParams(lp);
	                }
	              
	                dot.setImageResource(i == 0 ? R.drawable.red_point : R.drawable.white_point);
	                if (i == 0)
	                {
	                	txtTitle.setText(one.getString("title"));
	                }
	                dot.setScaleType(ScaleType.FIT_CENTER);
	                llyDot.addView(dot);
	                dotViewsList.add(dot);
				} catch (JSONException e) {}
    		}
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            viewPager.setFocusable(true);
            viewPager.setAdapter(new MyPagerAdapter());
            viewPager.setOnPageChangeListener(new MyPageChangeListener());
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            if (frag != null && frag instanceof IndexFragment)
                        	{
                        		IndexFragment indexFrag = (IndexFragment) frag;
                				indexFrag.enableDisableSwipeRefresh(false);
                        	}
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (frag != null && frag instanceof IndexFragment)
                        	{
                        		IndexFragment indexFrag = (IndexFragment) frag;
                				indexFrag.enableDisableSwipeRefresh(true);
                        	}
                            break;
                    }
                    return false;
                }
            });
        }
        
    }
    private class MyPagerAdapter  extends PagerAdapter
    {
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(imageViewsList.get(position));
        }
        
        @Override
        public Object instantiateItem(View container, int position) 
        {
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }
        @Override
        public int getCount() 
        {
            return imageViewsList.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) 
        {
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        
        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {}

        @Override
        public void finishUpdate(View arg0) {}
    }
    
    private class MyPageChangeListener implements OnPageChangeListener
    {
    	
        boolean isAutoPlay = false;
        @Override
        public void onPageScrollStateChanged(int arg0) 
        {
            switch (arg0) 
            {
	            case 1:// 手势滑动，空闲中
	                isAutoPlay = false;
	                //stopPlay();
	                break;
	            case 2:// 界面切换中
	                isAutoPlay = true;
	                break;
	            case 0:// 滑动结束，即切换完毕或者加载完毕
	                // 当前为最后一张，此时从右向左滑，则切换到第一张
	            	if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
	            		viewPager.setCurrentItem(0);
	            	}
	            	// 当前为第一张，此时从左向右滑，则切换到最后一张
	            	else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
	            		viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
	            	}
	                break;
           }
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) 
        {
        	
        }
        
        @Override
        public void onPageSelected(int pos) 
        {
            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    dotViewsList.get(pos).setImageResource(R.drawable.red_point);
                }else {
                    dotViewsList.get(i).setImageResource(R.drawable.white_point);
                }
            } 
            
            String title = null;
			try {
				JSONObject item = data.getJSONObject(pos);
				title = item.getString("title");
			} catch (JSONException e) {
				e.printStackTrace();
			}
            txtTitle.setText(title);
        }
    }
    
    private class SlideShowTask implements Runnable
    {
        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }
}