package com.wshang.soybean.bean;

public enum NewsType
{
	SHORT(1),
	LONG(2);

	
	// 定义私有变量
    private int code;
	private NewsType(int code)
	{
        this.code = code;
    }
	
	public int value() 
	{
       return this.code;
    }
}