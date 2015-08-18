package com.wshang.soybean.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.ui.support.WebViewActivitySupport;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.RSAUtil;
import com.wshang.soybean.tools.StringUtil;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public class TaoBaoLoginCallback implements LoginCallback {
	private Context curCtx;
	public TaoBaoLoginCallback(Context ctx)
	{
		this.curCtx = ctx;
	}
	
    @Override
    public void onSuccess(Session session) {
    	
    	// 头像
    	String avatarUrl = session.getUser().avatarUrl;
    	String userId = session.getUser().id;
    	String nick = session.getUser().nick;

        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
        Map<String, String[]> m = WebViewActivitySupport.getInstance().getCookies();
        for (Entry<String, String[]> e : m.entrySet()) {
            for (String s : e.getValue()) {
                CookieManager.getInstance().setCookie(e.getKey(), s);
            }
        }
        CookieSyncManager.getInstance().sync();
        taeLogin(userId, avatarUrl, nick);
    }

    @Override
    public void onFailure(int code, String message)
    {

    }
    
	private void taeLogin(String userId, String avatarUrl, String nick)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", RSAUtil.encryptByPublic(userId));
		params.put("face", avatarUrl);
		params.put("nick", RSAUtil.encryptByPublic(nick));

	    String key = StringUtil.getRandomString(10);//随机key
		MyApplication.getInstance().getMember().saveKeyOfValue("key", key);
		
		String encryptKey = RSAUtil.encryptByPublic(key);
		params.put("key", encryptKey);
		
		HttpManage.getInstance().taeLogin(params, curCtx);
	}
}