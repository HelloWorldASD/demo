package com.wshang.soybean.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.trade.ItemService;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.taobao.tae.sdk.webview.TaeWebViewUiSettings;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.main.MyApplication;
import com.wshang.soybean.my.MemberInfo;

public class HttpManage
{
	private static HttpManage manage;
	//无网络连接请求结果
	private String noNetworkResult = "{\"success\":false,\"code\":1,\"msg\":\"网络连接失败\"}";
	private HttpManage()
	{
		
	}
	
	/**
	 * 单例
	 * @return
	 */
	public static HttpManage getInstance()
	{
		if (manage == null)
			manage = new HttpManage();
		
		return manage;
	}
	
	/**
	 * 拼装http 的get请求的url
	 * @param input
	 * @param url
	 * @return
	 */
	private String getUrl(Map<String, String> input, String url)
	{
		for (Entry<String, String> entry :  input.entrySet())
		{
			String key = entry.getKey();
			if (key.equals("isAdd"))
				continue;
			
			String value = entry.getValue();
			url += key + "/" + value + "/";
		}
		return url;
	}
	
	private RequestParams getRequestParams(Map<String, String> input)
	{
		RequestParams params = new RequestParams();
		for (Entry<String, String> entry :  input.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			
			params.addBodyParameter(key, value);
		}
		return params;
	}
	
	/**
	 * 获取首页的资讯列表
	 * @param input
	 * @param curCtx
	 */
	public void getNewsList(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(0);	
		
		String url = HuiConstants.PREFIX + "post/lists/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.GET,
				url,
				new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	Intent itt = new Intent(HttpAction.NEWS_LIST);
	        		itt.putExtra("result", responseInfo.result);
	        		itt.putExtra("isAdd", input.containsKey("isAdd")? true : false);
	        		curCtx.sendBroadcast(itt);
		        }

		        @Override
		        public void onStart() {
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
	        		Intent itt = new Intent(HttpAction.NEWS_LIST);
	        		itt.putExtra("result", noNetworkResult);
	        		curCtx.sendBroadcast(itt);
		        }
		});
	}
	
	/**
	 * 
	 */
	public void getHomeTop(final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		
		http.send(HttpRequest.HttpMethod.GET,
				HuiConstants.PREFIX + "post/top/",
				new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) 
		        {
		        	Intent itt = new Intent(HttpAction.HOME_TOP);
	        		itt.putExtra("result", responseInfo.result);
	        		curCtx.sendBroadcast(itt);
		        }

		        @Override
		        public void onStart() {
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
	        		Intent itt = new Intent(HttpAction.HOME_TOP);
	        		itt.putExtra("result", noNetworkResult);
	        		curCtx.sendBroadcast(itt);
		        }
		});
	}
	
	/**
	 * 获取详情
	 * @param input
	 * @param curCtx
	 */
	public void getNewsDetail(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);
		else 
			http.configCurrentHttpCacheExpiry(3000);
		
		String url = HuiConstants.PREFIX + "post/detail/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.GET,
				  url,
				  new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		        	  
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.POST_DETAIL);
		        	  itt.putExtra("result", responseInfo.result);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			    	  
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.POST_DETAIL);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	/**
	 * 获取评论列表
	 * @param input
	 * @param curCtx
	 */
	public void getCommentList(final Map<String, String> input, final boolean isAdd, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(3000);//缓存3秒
		String url = HuiConstants.PREFIX + "comment/lists/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.GET,
				  url,
				  new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		        	  
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.COMMENT_LISTS);
		        	  itt.putExtra("result", responseInfo.result);
		        	  itt.putExtra("isAdd", isAdd);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			    	  
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.COMMENT_LISTS);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void sendPraise(final Map<String, String> input, final Context curCtx)
	{
		
    	Map<String, String> params = new HashMap<String, String>();
		params.put("postId", input.get("id"));
		params.put("id", String.valueOf(StatisticsType.SingleContentPointPraise.value()));
		params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
		params.put("deviceid", CommonUtil.getMac());
		statistics(params, curCtx);
		
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(0);
			
		String url = HuiConstants.PREFIX + "post/praise/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.GET,
				  url,
				  new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		        	  
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.POST_PRAISE);
			    	  itt.putExtra("id", input.get("id"));
		        	  itt.putExtra("result", responseInfo.result);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			    	  
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.POST_PRAISE);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void createComment(final Map<String, String> input, final Context curCtx)
	{
    	Map<String, String> paramsStatistics = new HashMap<String, String>();
    	paramsStatistics.put("postId", input.get("id"));
    	paramsStatistics.put("id", String.valueOf(StatisticsType.SingleContentComment.value()));
    	paramsStatistics.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	paramsStatistics.put("deviceid", CommonUtil.getMac());
		statistics(paramsStatistics, curCtx);
		
		
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);

		RequestParams params = new RequestParams();
		for (Entry<String, String> entry :  input.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			
			params.addBodyParameter(key, value);
		}
		
		http.send(HttpRequest.HttpMethod.POST,
			 HuiConstants.PREFIX + "comment/create/",
			 params,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.COMMENT_CREATE);
		        	  itt.putExtra("result", responseInfo.result);
		        	  itt.putExtra("id", input.get("id"));
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.COMMENT_CREATE);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void addGood(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);

		RequestParams params = getRequestParams(input);
		
		http.send(HttpRequest.HttpMethod.POST,
			 HuiConstants.PREFIX + "AppComment/good/",
			 params,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.ADD_GOOD);
		        	  itt.putExtra("result", responseInfo.result);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.ADD_GOOD);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void addBad(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);
		
		RequestParams params = getRequestParams(input);
		
		http.send(HttpRequest.HttpMethod.POST,
			 HuiConstants.PREFIX + "AppComment/bad/",
			 params,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.ADD_GOOD);
		        	  itt.putExtra("result", responseInfo.result);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.ADD_GOOD);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void getGoodList(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);
		
		String url = HuiConstants.PREFIX + "AppComment/good_lists/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.POST,
			 url,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.GOOD_LISTS);
		        	  itt.putExtra("result", responseInfo.result);
		        	  itt.putExtra("isAdd", input.containsKey("isAdd")? true : false);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.GOOD_LISTS);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	
	public void getBadList(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);
		
		String url = HuiConstants.PREFIX + "AppComment/bad_lists/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.GET,
			 url,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.BAD_LISTS);
		        	  itt.putExtra("result", responseInfo.result);
		        	  itt.putExtra("isAdd", input.containsKey("isAdd")? true : false);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.BAD_LISTS);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void taeLogin(final Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		if (input.containsKey("noCache"))
			http.configCurrentHttpCacheExpiry(0);
		
		RequestParams params = getRequestParams(input);
		
		http.send(HttpRequest.HttpMethod.POST,
			 HuiConstants.PREFIX + "user/tae_login/",
			 params,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.TAE_LOGIN);
		        	  itt.putExtra("result", responseInfo.result);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.TAE_LOGIN);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void umengUpdate(final Context curCtx)
	{
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		    @Override
		    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
		    	Intent itt = new Intent(HttpAction.UMENG_UPDATE);
		        switch (updateStatus) 
		        {
			        case UpdateStatus.Yes: // has update
			            UmengUpdateAgent.showUpdateDialog(curCtx, updateInfo);
			            itt.putExtra("update", true);
			            break;
			        case UpdateStatus.No: // 没有更新
			        case UpdateStatus.NoneWifi: // 没有wifi连接， 只在wifi下更新
			        case UpdateStatus.Timeout: // 超时	
			        	itt.putExtra("update", false);
			            break;
		        }
		    	curCtx.sendBroadcast(itt);
		    }
		});

		UmengUpdateAgent.update(curCtx);
	}
	
	public void saveMember(JSONObject result, Context ctx)
	{
		if (result == null)
		{
			ToastUtil.makeFailToastThr(ctx, ctx.getResources().getString(R.string.server_bad));
			return;
		}
		
		try {
			boolean success = result.getBoolean("success");
		
    		if (success == false)
    		{
    			ToastUtil.makeFailToastThr(ctx, result.getString("msg"));
    			return ;
    		}
    		
    		String data = XXTEA.decode(result.getString("data"), MyApplication.getInstance().getMember().getMemberMap().get("key"));
    		JSONObject memberInfo = new JSONObject(data);
    		MemberInfo member = MyApplication.getInstance().getMember();
    		member.saveMemberInfo(memberInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 统计
	 * @param input
	 * @param curCtx
	 */
	public void statistics(Map<String, String> input, final Context curCtx)
	{
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(0);
		String url = HuiConstants.PREFIX + "Statistics/index/";
		url = getUrl(input, url);
		
		http.send(HttpRequest.HttpMethod.GET,
			 url,
			 new RequestCallBack<String>(){
		          @Override
		          public void onLoading(long total, long current, boolean isUploading)
		          {
		          }

			      @Override
			      public void onSuccess(ResponseInfo<String> responseInfo) 
			      {
			    	  Intent itt = new Intent(HttpAction.STATISTICS);
		        	  itt.putExtra("result", responseInfo.result);
		        	  curCtx.sendBroadcast(itt);
			      }
	
			      @Override
			      public void onStart()
			      {
			      }
	
			      @Override
			      public void onFailure(HttpException error, String msg) 
			      { 
			    	  Intent itt = new Intent(HttpAction.STATISTICS);
			    	  itt.putExtra("result", noNetworkResult);
		        	  curCtx.sendBroadcast(itt);
			      }
		});
	}
	
	public void openOrClosePush(Context curCtx)
	{

		if ("true".equals(MyApplication.getInstance().getMember().getMemberMap().get("push")))
		{
			PushManager.getInstance().turnOnPush(curCtx);
		}
		else
		{
			PushManager.getInstance().turnOffPush(curCtx);
		}
	}
	
	public void openTaoBaoH5(Activity curAct, String url)
	{
		if (url == null)
			return;
		
		int position = url.indexOf("=");
		if (position == -1)
			return;
		
		if (url.length() > position)
			position +=1;
		
		String itemId = url.substring(position);
		//商品类型。填写：1 ，表示淘宝商品; 2 ，表示天猫商品。必填
		int type = url.contains("taobao") ? 1 : 2;
		//商品详情服务
		ItemService service = AlibabaSDK.getService(ItemService.class);
		TaeWebViewUiSettings  webViewSettings = new TaeWebViewUiSettings();
		webViewSettings.title = "淘宝";
		com.alibaba.sdk.android.trade.callback.TradeProcessCallback callBack = new com.alibaba.sdk.android.trade.callback.TradeProcessCallback() 
		{

			@Override
			public void onFailure(int arg0, String arg1) {
			}

			@Override
			public void onPaySuccess(com.alibaba.sdk.android.trade.model.TradeResult arg0) {
			}
		};
		service.showItemDetailByItemId(curAct, callBack, webViewSettings, Long.valueOf(itemId), type, null);
	}
	
}
