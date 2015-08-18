package com.wshang.soybean.main;

import java.util.HashMap;
import java.util.Map;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.HttpManage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class SplashScreen  extends Activity implements OnPageChangeListener
{
	private ViewPager viewPager;  
	
	 /** 
     * 装ImageView数组 
     */  
    private ImageView[] imageViews;  
    
	 /** 
     * 装ImageView数组 
     */  
    private int[] imgIdArray = {R.drawable.flash_one};
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		/** 全屏设置，隐藏窗口所有装饰 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/** 标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.splashscreen);
		if (MyApplication.getInstance().getMember().getMemberMap().containsKey("isFirstStart"))
		{
			start();
		}
		else
		{
			MyApplication.getInstance().getMember().saveKeyOfValue("push", "true");
			MyApplication.getInstance().getMember().saveKeyOfValue("isFirstStart", "No");
			viewPager = (ViewPager) findViewById(R.id.vpStart); 
			
			 //将图片装载到数组中  
			imageViews = new ImageView[imgIdArray.length];  
	        for(int i=0; i<imageViews.length; i++){  
	            ImageView imageView = new ImageView(this);  
	            imageViews[i] = imageView;  
	            imageView.setBackgroundResource(imgIdArray[i]);  
	        }
			
			//设置Adapter  
	        StartAdapter adapter = new StartAdapter();
	        viewPager.setAdapter(adapter);
	        
	        //设置监听，主要是设置点点的背景  
	        viewPager.setOnPageChangeListener(this);  
	        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动  
	        viewPager.setCurrentItem(0);  
	        
	        statisticsOpenAnEvenActive();
		}
	}
	
	private void statisticsOpenAnEvenActive()
	{
	 	Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(StatisticsType.OpenAnEvenActive.value()));
		params.put("deviceid", CommonUtil.getMac());
		HttpManage.getInstance().statistics(params, SplashScreen.this);
	}

	@Override  
	protected void onRestart() 
	{  
		super.onRestart();  
	}  

	public void onResume() 
	{
		super.onResume();
	}

	public void onPause() 
	{
		super.onPause();
	}
	
	
	/**
	 * 
	 * @author liangpeng
	 *
	 */
    private class StartAdapter extends PagerAdapter{  
  
        @Override  
        public int getCount() {  
            return imgIdArray.length;  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public void destroyItem(View container, int position, Object object) {  
            ((ViewPager)container).removeView(imageViews[position % imgIdArray.length]);  
        }  
  
        /** 
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键 
         */  
        @Override  
        public Object instantiateItem(View container,final int position) {  
            ((ViewPager)container).addView(imageViews[position % imgIdArray.length], 0);  
            ImageView curImageView = imageViews[position % imgIdArray.length];
            curImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					start();
				}
			});
            return curImageView;  
        }    
    }  

    @Override  
    public void onPageScrollStateChanged(int arg0) {  
          
    }  
  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
          
    }  
  
    @Override  
    public void onPageSelected(int index) {  
    	start();
    }  
    
    private void start()
    {
    	Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(StatisticsType.StartQuantity.value()));
		params.put("deviceid", CommonUtil.getMac());
		HttpManage.getInstance().statistics(params, SplashScreen.this);
    	
    	
    	Intent intent   = new Intent();
		intent.setClass(SplashScreen.this, MainActivity.class);
		startActivity(intent);
		SplashScreen.this.finish();
    }
}
