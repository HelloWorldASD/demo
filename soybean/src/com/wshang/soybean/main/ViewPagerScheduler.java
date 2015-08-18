package com.wshang.soybean.main;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
public class ViewPagerScheduler 
{
	private ViewPager viewPager;
	private int       count;
	static final int MESSAGE = 30;
	private Handler scheduleTurnHandler = new Handler(){
		@Override
		public void handleMessage (Message msg) {
			if(MESSAGE != msg.what || count < 2) return;
			int index = (viewPager.getCurrentItem()+1) % count;
			viewPager.setCurrentItem(index, true);
		}
	};

	private Timer scheduleTimer;

	public ViewPagerScheduler(ViewPager viewPager){
		this.viewPager = viewPager;
	}

	/**
	 * 更新ViewPager包含的数据数量
	 * @param count 数据量
	 */
	public void updateCount(int count){
		this.count = count;
	}

	/**
	 * 重新开启（首次开启）定时轮播。
	 * @param period 轮播周期，单位毫秒。
	 */
	public void restart (int period){
		scheduleTimer = new Timer();
		scheduleTimer.schedule(new TimerTask() {
			@Override
			public void run () {
				scheduleTurnHandler.sendEmptyMessage(MESSAGE);
			}
		},period,period);
	}

	/**
	 * 停止。
	 */
	public void stop(){
		if(scheduleTimer != null) scheduleTimer.cancel();
	}
}
