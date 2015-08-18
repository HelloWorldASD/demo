package com.wshang.soybean.my;

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MemberInfo
{
	private Context ctx;
	private Map<String, String> memberMap;
	
	public MemberInfo(Context ctx)
	{
		this.ctx = ctx;
		getMemberInfo();
	}
	
	public void saveMemberInfo(JSONObject memberInfo)
	{
		SharedPreferences preferences = ctx.getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		Editor edit = preferences.edit();
		try {
			edit.putString("user_id", memberInfo.getString("user_id"));
//			edit.putString("phone", memberInfo.getString("phone"));
			edit.putString("nickname", memberInfo.getString("nickname"));
//			edit.putString("ww", memberInfo.getString("ww"));
//			edit.putString("email", memberInfo.getString("email"));
//			edit.putString("birthday", memberInfo.getString("birthday"));
//			edit.putString("gender", memberInfo.getString("gender"));
//			edit.putString("status", memberInfo.getString("status"));
//			edit.putString("add_time", memberInfo.getString("add_time"));
//			edit.putString("last_login_time", memberInfo.getString("last_login_time"));
//			edit.putString("login_num", memberInfo.getString("login_num"));
//			edit.putString("invite_code", memberInfo.getString("invite_code"));
//			edit.putString("channel", memberInfo.getString("channel"));
			edit.putString("face", memberInfo.getString("face"));
//			edit.putString("score", memberInfo.getString("score"));
//			edit.putString("unread", memberInfo.getString("unread"));
			if (memberInfo.has("token"))
				edit.putString("token", memberInfo.getString("token"));
			edit.commit();
			getMemberInfo();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearAll()
	{
		SharedPreferences preferences = ctx.getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		Editor edit = preferences.edit();
		edit.putString("user_id", null);
//		edit.putString("phone", null);
		edit.putString("nickname", null);
//		edit.putString("ww", null);
//		edit.putString("email", null);
//		edit.putString("birthday", null);
//		edit.putString("gender", null);
//		edit.putString("status", null);
//		edit.putString("add_time", null);
//		edit.putString("last_login_time", null);
//		edit.putString("login_num", null);
//		edit.putString("invite_code", null);
//		edit.putString("channel", null);
		edit.putString("face", null);
//		edit.putString("score", null);
//		edit.putString("unread", null);
		edit.putString("token", null);
		edit.commit();
		getMemberInfo();
	}
	
	public void saveKeyOfValue(String key, String value)
	{
		SharedPreferences preferences = ctx.getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		Editor edit = preferences.edit();
		edit.putString(key, value);
		edit.commit();
		getMemberInfo();
	}
	
	@SuppressWarnings("unchecked")
	public void getMemberInfo() 
	{
		SharedPreferences preferences = ctx.getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		memberMap = (Map<String, String>) preferences.getAll();
	}

	public Map<String, String> getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map<String, String> memberMap) {
		this.memberMap = memberMap;
	}
}
