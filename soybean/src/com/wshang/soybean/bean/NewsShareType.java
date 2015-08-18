package com.wshang.soybean.bean;

public enum NewsShareType
{
	WEIXIN(1),
	WEIXIN_FRIEND(2),
	WEIBO(3),
	QZONE(4);
	
	// 定义私有变量
    private int code ;
	private NewsShareType (int code)
	{
        this.code = code;
    }
	
	public int value() 
	{
       return this.code;
    }
} 
