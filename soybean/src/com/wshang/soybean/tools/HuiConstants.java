package com.wshang.soybean.tools;

import java.util.Date;


public  class HuiConstants
{
//	public static String IP = "http://10.64.87.230/Maodou/";
	public static String IP = "http://maodou.wx.jaeapp.com/";
	public static String URL = IP + "/api.php?s=v1/";
	
	//每页大小
	public final static int PAGE_SIZE = 10;
	
	//裁剪大小
	public final static int INTERCEPT_SIZE = 30;
	
	public final static int CONTENT_SIZE = 250;
	
	public static String PERSON_ICON = "person_logo.jpg";
	
	//登录码
	public final static int LOGIN_REQUEST_CODE = 104;
	
	public final static String PREFIX = IP + "api.php?s=v1/";
	public final static String HTML_URL = IP + "index.php";
	
	
	public static Date NOW_TIME;
//	
//	public static List<String> SIMLE_LIST = new ArrayList<String>(Arrays.asList("smiley_0", "smiley_1", "smiley_2", "smiley_3", "smiley_4", "smiley_5", "smiley_6", "smiley_7", "smiley_8", "smiley_9",
//			"smiley_10", "smiley_11", "smiley_12", "smiley_13", "smiley_14", "smiley_15", "smiley_16", "smiley_17", "smiley_18", "smiley_19", "smiley_20", "smiley_21", "smiley_22", "smiley_23",
//			"smiley_24", "smiley_25", "smiley_26", "smiley_27", "smiley_28"));
	
	/**
	 * ShareSDK APPKEY
	 */
	public static final String ShareSDK = "";
	

	public static final String UMENG_SHARE = "com.umeng.share";
	 
	 
	 
	/** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
	public static final String WEI_BO_APP_KEY      = "23545204";

    /** 
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String WEI_BO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String WEI_BO_SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
}