package com.wshang.soybean.tools;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.xmlpull.v1.XmlPullParserException;

import com.wshang.soybean.R;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 
 * 项目名称：工具包
 * 
 * 类名称：ToastUtil
 * 
 * 类描述： 弹出提示信息
 * 
 * 创建人：梁鹏
 * 
 * 联系方式：
 * 
 * 创建时间：2014-3-31 上午10:23:59
 * 
 * 修改人：梁鹏
 * 
 * 修改时间：2014-3-31 上午10:23:59
 * 
 * 修改备注：
 * 
 * @version
 * 
 */
public class ToastUtil {
	private static Toast toast = null;
	public static int LENGTH_LONG = Toast.LENGTH_LONG;
	public static int LENGTH_SHORT = Toast.LENGTH_SHORT;
	//public final static int TIP_OK =1;    			//操作成功的提示
	//public final static int TIP_FAIL =2;			//操作失败的提示
	
	public static Toast toastOk =null;
	public static Toast toastFail =null;

	/**
	 * 普通文本消息提示
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void TextToast(Context context, CharSequence text,
			int duration) {
		// 创建一个Toast提示消息
		if (toast == null)
		{
			toast = Toast.makeText(context, text, duration);
			// 设置Toast提示消息在屏幕上的位置
			// toast.setGravity(Gravity.BOTTOM, 0, 0);
		}
		else
		{
			toast.setText(text);
			toast.setDuration(duration);
			// 设置Toast提示消息在屏幕上的位置
			// toast.setGravity(Gravity.BOTTOM, 0, 0);
		}
		// 显示消息
		toast.show();
	}

	public static void makeLongToast(Context context, String text) {
		if (toast == null)
		{
			toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		}
		else
		{
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_LONG);
		}
		toast.show();
	}

	public static void makeShortToast(Context context, String text) {

		if (toast == null)
		{
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		else
		{
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	/**
	 * 提示消息如果当前线程是UI线程,那么行动是立即执行。如果当前线程不是UI线程,操作是发布到事件队列的UI线程
	 * @param context
	 * @param text
	 */
	public static void makeShortToastUI(final Context context, final String text) {

		// 如果当前线程是UI线程,那么行动是立即执行。如果当前线程不是UI线程,操作是发布到事件队列的UI线程
		// 因为runOnUiThread是Activity中的方法，Context是它的父类，所以要转换成Activity对象才能使用
		((Activity) context).runOnUiThread(new Runnable() {
		 
		    @Override
		    public void run() {
		        // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
		    	if (toast == null)
				{
					toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				}
				else
				{
					toast.setText(text);
					toast.setDuration(Toast.LENGTH_SHORT);
				}
				toast.show();
		    }
		});
	}
	
	
	public static void makeExceptionToast(Context context, Exception exception) {

		if (exception instanceof SocketTimeoutException)
		{
			makeShortToast(context, "请求网络超时");
		}
		else if (exception instanceof SocketException)
		{
			makeShortToast(context, "服务器没有响应");
		}
		else if (exception instanceof NetworkErrorException)
		{
			makeShortToast(context, "网络错误");
		}
		else if (exception instanceof IOException)
		{
			makeShortToast(context, "输入输出流错误");
		}
		else if (exception instanceof XmlPullParserException)
		{
			makeShortToast(context, "解析xml出错");
		}
		else
		{
			makeShortToast(context, "程序发生未知错误");
		}
	}
	
	/*public static void makeCustomToast(Context context, String text, int tipType, int duration)
	{
		if(toastOk ==null)
		{
			if(duration ==Toast.LENGTH_LONG)
				toastOk = Toast.makeText(context,text, Toast.LENGTH_LONG);
			else
				toastOk = Toast.makeText(context,text, Toast.LENGTH_SHORT);
		}
		else
		{
			toastOk.setText(text);
			if(duration ==Toast.LENGTH_LONG)
				toastOk.setDuration(Toast.LENGTH_LONG);
			else
				toastOk.setDuration(Toast.LENGTH_SHORT);
		}
		toastOk.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(context);
		
		if(tipType ==TIP_FAIL)
			imageCodeProject.setImageResource(R.drawable.fail_tip);			//失败提示
		else
			imageCodeProject.setImageResource(R.drawable.success_tip);		//成功提示

		toastView.addView(imageCodeProject, 0);
		toast.show();
	}*/
	
	public static void makeOkToastThr(final Context context, final String text)
	{
		((Activity) context).runOnUiThread(new Runnable() {
			 
		    @Override
		    public void run() {
		        // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
		    	if(toastOk ==null)
				{
					toastOk = Toast.makeText(context,text, Toast.LENGTH_SHORT);
					toastOk.setGravity(Gravity.CENTER, 0, 0);
					LinearLayout toastView = (LinearLayout) toastOk.getView();
					ImageView imageCodeProject = new ImageView(context);
					imageCodeProject.setImageResource(R.drawable.success_tip);		//成功提示
					toastView.addView(imageCodeProject, 0);
				}
				else
				{
					toastOk.setText(text+"\r\n");	
					toastOk.setDuration(Toast.LENGTH_SHORT);
				}	
		    	toastOk.show();
		    }
		});
	}
	
	public static void makeFailToastThr(final Context context, final String text)
	{
		((Activity) context).runOnUiThread(new Runnable() {
			 
		    @Override
		    public void run() {
		        // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
		    	if(toastFail ==null)
				{
					toastFail = Toast.makeText(context,text, Toast.LENGTH_SHORT);
					toastFail.setGravity(Gravity.CENTER, 0, 0);
					LinearLayout toastView = (LinearLayout) toastFail.getView();
					ImageView imageCodeProject = new ImageView(context);
					imageCodeProject.setImageResource(R.drawable.fail_tip);		//成功提示
					toastView.addView(imageCodeProject, 0);
				}
				else
				{
					toastFail.setText(text+"\r\n");	
					toastFail.setDuration(Toast.LENGTH_SHORT);
				}	
		    	toastFail.show();
		    }
		});
	}
}
