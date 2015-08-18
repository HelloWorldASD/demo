package com.wshang.soybean.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import android.app.Activity;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.main.TaoBaoLoginCallback;

public class CommonUtil 
{
	/**
	 * 打开登录
	 */
	public static boolean openLogin(Activity curAct, Boolean isLogin)
	{
		if (StringUtil.isEmpty(MyApplication.getInstance().getMember().getMemberMap().get("user_id")) == true || isLogin)
		{
			AlibabaSDK.getService(LoginService.class).showLogin(curAct, new TaoBaoLoginCallback(curAct));
			return true;
		}
		else 
			return false;
	
	}
	
	/**
	 * 获取Mac地址
	 * @return
	 */
	public static String getMac() 
	{
	      String macSerial = null;
	      String str = "";
	  
	      try
	      {
	    	  String mac = MyApplication.getInstance().getMember().getMemberMap().get("mac");
	  		  if (StringUtil.isEmpty(mac) == false)
	  			  return mac;
	    	  
	          Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
	          InputStreamReader ir = new InputStreamReader(pp.getInputStream());
	          LineNumberReader input = new LineNumberReader(ir);
	 
	         for (; null != str;) 
	         {
	             str = input.readLine();
	             if (str != null)
	             {
	                 macSerial = str.trim();// 去空格
	                 break;
	             }
	         }
	     } catch (IOException ex) {
	         // 赋予默认值
	         ex.printStackTrace();
	     }
	      
	     MyApplication.getInstance().getMember().saveKeyOfValue("mac", macSerial);
	     return macSerial;
	 }
}
