package com.wshang.soybean.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.NewsShareType;
import com.wshang.soybean.bean.NewsType;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.my.AccessTokenKeeper;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.HuiConstants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;

public class NewsShare extends Activity implements IWeiboHandler.Response, IUiListener, OnClickListener  {
	private final UMSocialService controller = UMServiceFactory
	            .getUMSocialService(HuiConstants.UMENG_SHARE, RequestType.SOCIAL);
	
	private String wapUrl;
	private String id;
	private String title;
	private int type;
	private String shortTitle = "真的很有趣！马上去查看......";
	private String longContent = "颗颗饱满,粒粒逗比";
	
	
	   /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken accessToken;
    
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    private Tencent curTencent; 
    
    /**
     * @see {@link Activity#onCreate}
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_share);
		
		initData();
		initView();
		configPlatforms();
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        statisticsSingleContent();
	}
	
	
    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() 
    {
        String appId = "wxfc9c4344d99f379d";
        String appSecret = "08bb5a0d92f2adcfa63657e9347091a7";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(NewsShare.this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(NewsShare.this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        
 	   // 创建微博实例
        mWeiboAuth = new WeiboAuth(this, HuiConstants.WEI_BO_APP_KEY, HuiConstants.WEI_BO_REDIRECT_URL, HuiConstants.WEI_BO_SCOPE);
   	 // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, HuiConstants.WEI_BO_APP_KEY);
        
		curTencent = Tencent.createInstance("1104766178", NewsShare.this);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ShareSDK.stopSDK(this);
	}

	private void initData() {
		Intent intent = getIntent();
		wapUrl =  intent.getStringExtra("wapUrl") + "/from/app";
		id = intent.getStringExtra("id");
		title = intent.getStringExtra("title");
		type = intent.getIntExtra("type", NewsType.SHORT.ordinal());
	}

	private void initView() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		getWindow().setAttributes(lp);
	}
	
    /**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
	
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.ivSinaWeiBo:
				   // 如果未安装微博客户端，设置下载微博对应的回调
				statisticsNumberChannelsTransmit(NewsShareType.WEIBO);
		        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
		            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
		                public void onCancel() {
		                    Toast.makeText(NewsShare.this, 
		                            R.string.weibosdk_cancel_download_weibo,
		                            Toast.LENGTH_SHORT).show();
		                }
		            });
		            Toast.makeText(NewsShare.this, R.string.weibosdk_has_not_installed_weibo,
		                    Toast.LENGTH_SHORT).show();
		        	return;
		        }
		        else
		        {
		        	  // 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
		            if (mWeiboShareAPI.checkEnvironment(true) == false)
		            {
		            	return;
		            }
		        }
				
				 accessToken = AccessTokenKeeper.readAccessToken(NewsShare.this);
				 if (accessToken.isSessionValid() == false)
				 {
					 mSsoHandler = new SsoHandler(NewsShare.this, mWeiboAuth);
		                mSsoHandler.authorize(new AuthListener());
				 }
				 else
				 {
					 sendWeiBo();
				 }
				break;
			case R.id.ivWechat:
				statisticsNumberChannelsTransmit(NewsShareType.WEIXIN);
				performShare(SHARE_MEDIA.WEIXIN);
				break;
			case R.id.ivWechatcomment:
				statisticsNumberChannelsTransmit(NewsShareType.WEIXIN_FRIEND);
				performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
				break;
			case R.id.ivQzone:
				statisticsNumberChannelsTransmit(NewsShareType.QZONE);
				
				Bundle params = new Bundle();
				params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
				if(type==NewsType.SHORT.ordinal()){
					params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shortTitle);//必填
					params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, wapUrl);
				}else{
					params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
					params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, longContent);
				}
				params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, wapUrl);//必填
				
				ArrayList<String> images = new ArrayList<String>();
				images.add("http://maodou.image.alimmdn.com/others/share_logo.png");
				params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);

				curTencent.shareToQzone(NewsShare.this, params, NewsShare.this);
				break;
		}
	}
	
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
        if (requestCode == Constants.REQUEST_QZONE_SHARE) 
        {
        	if (resultCode == Constants.ACTIVITY_OK) {
        		Tencent.handleResultData(data, this);
        	}
        }
    }
	
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
        	accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(NewsShare.this, accessToken);
                sendWeiBo();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(NewsShare.this, message, Toast.LENGTH_LONG).show();
            }
        }

        public void onCancel() {
            Toast.makeText(NewsShare.this, 
                    R.string.weibosdk_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        public void onWeiboException(WeiboException e) {
            Toast.makeText(NewsShare.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    
    private void performShare(SHARE_MEDIA platform) 
    {
		if (platform == SHARE_MEDIA.WEIXIN)
		{
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			// 设置分享内容
	    	if (type == NewsType.SHORT.ordinal())
	    	{
	    		weixinContent.setShareContent(shortTitle + wapUrl);
//	    		//设置title
	    	}
	    	else
	    	{
	    		//设置分享图片
	    		UMImage img = new UMImage(NewsShare.this, R.drawable.share_logo);
	    		weixinContent.setTitle(title);
	    		weixinContent.setShareContent(longContent);
	    		weixinContent.setShareImage(img);
	    	}
			//设置分享内容跳转URL
			weixinContent.setTargetUrl(wapUrl);
	    	controller.setShareMedia(weixinContent);
		}
		else
		{
			CircleShareContent circleMedia = new CircleShareContent();
			if (type == NewsType.SHORT.ordinal())
			{
				circleMedia.setShareContent(shortTitle + wapUrl);
				circleMedia.setTitle("");
			}
			else
			{
				//设置分享图片
	    		UMImage img = new UMImage(NewsShare.this, R.drawable.share_logo);
	    		circleMedia.setTitle(title);
	    		circleMedia.setShareContent(longContent);
	    		circleMedia.setShareImage(img);
			}
			circleMedia.setTargetUrl(wapUrl);
			controller.setShareMedia(circleMedia);
		}
		
    	
        controller.postShare(NewsShare.this, platform, new SnsPostListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = null;
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showText = "分享成功";
                    
                    if (platform == SHARE_MEDIA.WEIXIN)
                    {
                    	statisticsSingleContentSuccess(NewsShareType.WEIXIN);
                    }
                    else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE)
                    {
                    	statisticsSingleContentSuccess(NewsShareType.WEIXIN_FRIEND);
                    }
                } else {
                    showText = "分享失败";
                }
                Toast.makeText(NewsShare.this, showText, Toast.LENGTH_SHORT).show();
                NewsShare.this.finish();
            }
        });
    }
    
    private void sendWeiBo()
    {
        // 注册第三方应用 到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        mWeiboShareAPI.registerApp();
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351) {
                sendMultiMessage();
            } else {
                sendSingleMessage();
            }
        } else {
            Toast.makeText(this, R.string.weibosdk_not_support_api_hint, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage() {
        
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (type == NewsType.SHORT.ordinal())
        {
        	weiboMessage.textObject = getTextObj();
        }
        else
        {
        	weiboMessage.imageObject = getImageObj();
        	weiboMessage.mediaObject = getWebpageObj();
        }
        	

        
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }
    
    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        String content = type == NewsType.SHORT.ordinal() ? shortTitle : title;
        
        textObject.text = content + wapUrl;
        textObject.title = content;
        textObject.actionUrl = wapUrl;
        return textObject;
    }
    
    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(BitmapFactory.decodeResource(NewsShare.this.getResources(), R.drawable.share_logo));
        return imageObject;
    }
    
    /**
     * 创建多媒体（网页）消息对象。
     * 
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = longContent;
        
        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(BitmapFactory.decodeResource(NewsShare.this.getResources(), R.drawable.share_logo));
        mediaObject.actionUrl = wapUrl;
        mediaObject.defaultText = longContent;
        return mediaObject;
    }
    

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private void sendSingleMessage() {
        
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        weiboMessage.mediaObject = getTextObj();

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(NewsShare.this.getLocalClassName());
		MobclickAgent.onPause(NewsShare.this);       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(NewsShare.this.getLocalClassName());
		MobclickAgent.onResume(NewsShare.this);       //统计时长
	}

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
        	statisticsSingleContentSuccess(NewsShareType.WEIBO);
            Toast.makeText(this, R.string.weibosdk_toast_share_success, Toast.LENGTH_LONG).show();
            NewsShare.this.finish();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, R.string.weibosdk_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
                    getString(R.string.weibosdk_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
    }
    
    @Override
    public void onComplete(Object response)
    {
    	statisticsSingleContentSuccess(NewsShareType.QZONE);
    	
    }
    
    protected void doComplete(JSONObject values)
    {

    }
    
    @Override
    public void onError(UiError e) 
    {
    	
    }
    
    @Override
    public void onCancel()
    {
    	
    }
    
    /**
     * 单个内容转发成功数量
     * @param type
     */
    private void statisticsSingleContentSuccess(NewsShareType type)
    {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("postId", id);
    	params.put("id", String.valueOf(StatisticsType.NumberSuccessfulSingleContentForwarding.value()));
    	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	params.put("type", String.valueOf(type.value()));
    	HttpManage.getInstance().statistics(params, NewsShare.this);
    }
    
    /**
     * 单个内容点击转发数量
     * @param type
     */
    private void statisticsSingleContent()
    {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("postId", id);
    	params.put("id", String.valueOf(StatisticsType.SingleContentForwardingNumber.value()));
    	HttpManage.getInstance().statistics(params, NewsShare.this);
    }
    
    private void statisticsNumberChannelsTransmit(NewsShareType type)
    {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("postId", id);
    	params.put("id", String.valueOf(StatisticsType.SingleContentNumberChannelsTransmit.value()));
    	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	params.put("type", String.valueOf(type.value()));
    	HttpManage.getInstance().statistics(params, NewsShare.this);
    }
}
