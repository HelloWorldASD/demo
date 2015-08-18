package com.wshang.soybean.bean;

public enum StatisticsType
{
	H5NumberPromotionalDownloads(1),//宣传下载数量   H5页面进入下载的数量
	StartQuantity(2),//启动数量   启动次数总量（人次）
	ActivatedUser(3),//（已激活用户） 中启动的人数，基于用户
	OpenAnEvenActive(4),//打开一次就算活跃，基于app  输入参数
	SingleContentPointPraise(5),//单个内容点赞数量
	SingleContentComment(6),//单个内容评论数量
	NumberSuccessfulSingleContentForwarding(7),//单个内容转发成功数量
	SingleContentForwardingNumber(8),//单个内容点击转发数量
	SingleContentNumberChannelsTransmit(9),//单个内容点击转发渠道数量
	SingleContentClickNumberOnlookers(10),	//单个内容点击围观数量
	SingleContentEnterNumberTaobao(11),//单个内容进入淘宝页数量   淘宝页唤起次数
	SingleNumberMessagesReceived(12),//单条消息有多少人接受了
	SingleNumberMessagesOpened(13);//单条消息有多少人打开了

	// 定义私有变量
    private int code;
	private StatisticsType (int code)
	{
        this.code = code;
    }
	
	public int value() 
	{
       return this.code;
    }
}
