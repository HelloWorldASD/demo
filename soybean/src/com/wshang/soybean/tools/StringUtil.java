package com.wshang.soybean.tools;

import java.util.Random;
import java.util.regex.Pattern;

public class StringUtil 
{
	public static String getRandomString(int length)
	{
		String str          = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
		Random random       = new Random();
		StringBuffer sf     = new StringBuffer();
		for(int i=0;i<length;i++)
		{
			int number=random.nextInt(62);
			sf.append(str.charAt(number));
		}
		return sf.toString();
	}
	
	/**
	 * 判断字符串是否为空
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
    	if (string==null || string.length() == 0) {
			return true;
		}
    	return false;
    }
	
	//字符转全角
	public static String toDBC(String input) 
	{
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}if (c[i]> 65280&& c[i]< 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	
	public static boolean isNumberic(String content)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(content).matches();
	}
	
	public static boolean isMobile(String content)
	{
		Pattern pattern = Pattern.compile("^1\\d{10}$");
		return pattern.matcher(content).matches();
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public static String interceptContent(String content, int size)
	{
		String strIntercept = null;
		if (isEmpty(content)|| content.length() <= size)
			strIntercept = content;
		else 
			strIntercept = content.substring(0, size) + "...";
		
		return strIntercept;
	}
}
