package com.wshang.soybean.main;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.StatisticsType;
import com.wshang.soybean.news.EssenceInfo;
import com.wshang.soybean.news.InfoArticleDetail;
import com.wshang.soybean.news.TextInfoArticleDetail;
import com.wshang.soybean.tools.CommonUtil;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HttpManage;
import com.wshang.soybean.tools.StringUtil;

public class PushReceiver extends BroadcastReceiver {
	private int NOTIFICATION_ID = 4241;
    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @SuppressWarnings("deprecation")
	@Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 10001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);
                    
                    String push_id = null;
                    String face = null;
                    String nickname = null;
                    int type = 0;
                    String url = null;
                    String id = null;
                    String title = null;
                    String content = null;
                    long time = 0;
                    try 
                    {
                        JSONObject jsonData = new JSONObject(data);
                        push_id = jsonData.getString("push_id");
                        type = jsonData.getInt("type");
                        url = jsonData.getString("url");
                        id = String.valueOf(jsonData.getInt("id"));
                        face = jsonData.getString("face");
                        nickname = jsonData.getString("nickname");
                        title = jsonData.getString("title");
                        content = jsonData.getString("content");
                        time = jsonData.getLong("time");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                    Message mess = new Message();
                    mess.setContent(content);
                    mess.setPushId(push_id);
                    mess.setMessageId(id);
                    mess.setTitle(title);
                    mess.setType(type);
                    mess.setTime(time);
                    mess.setFace(face);
                    mess.setNickName(nickname);
                    mess.setUrl(url);
                    int primaryId = DbManage.getInstance(context).save(mess);
                    mess.setId(primaryId);
                    
                    Intent ittInfo = null;
                    switch (type)
                    {
						case 1://短文
							ittInfo = new Intent(context, InfoArticleDetail.class);
							break;
						case 2://长文
							ittInfo = new Intent(context, EssenceInfo.class);
							ittInfo.putExtra("title", title);
							ittInfo.putExtra("url", url);
							break;
						case 3://文本内容
							if (StringUtil.isEmpty(url))
							{
								ittInfo = new Intent(context, TextInfoArticleDetail.class);
								ittInfo.putExtra("content", content);
							}
							else 
							{
								ittInfo = new Intent(context, WebActivity.class);
								ittInfo.putExtra("url", url);
							}
							ittInfo.putExtra("title", title);
							break;
						default:
							break;
					}
					ittInfo.putExtra("messageId", id);
					ittInfo.putExtra("pushId", push_id);
					ittInfo.putExtra("fromPush", true);
					ittInfo.putExtra("id", primaryId);
					
                    statisticsMessagesReceived(push_id, MyApplication.getInstance().getAppCtx());
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, ittInfo, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new Notification();
                    notification.tickerText = content;
                    notification.icon = R.drawable.ic_launcher;
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    notification.setLatestEventInfo(MyApplication.getInstance().getAppCtx(), title, content, contentIntent); 
                    notificationManager.notify(NOTIFICATION_ID, notification);
 
	              	Intent ittPushMessage = new Intent(HttpAction.PUSH_MESSAGE);
	            	ittPushMessage.putExtra("pushId", push_id);
	              	ittPushMessage.putExtra("messageId", id);
	              	ittPushMessage.putExtra("type", type);
	            	ittPushMessage.putExtra("time", time);
	            	ittPushMessage.putExtra("id", primaryId);
	            	ittPushMessage.putExtra("title", title);
	            	ittPushMessage.putExtra("face", face);
	            	ittPushMessage.putExtra("nickname", nickname);
	            	ittPushMessage.putExtra("content", content);
	            	ittPushMessage.putExtra("url", url);
	            	
	            	MyApplication.getInstance().getAppCtx().sendBroadcast(ittPushMessage);
                    
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
//                if (GetuiSdkDemoActivity.tView != null) {
//                    GetuiSdkDemoActivity.tView.setText(cid);
//                }
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                break;
            default:
                break;
        }
    }
    
    private void statisticsMessagesReceived(String id, Context context)
    {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("pushId", id);
    	params.put("id", String.valueOf(StatisticsType.SingleNumberMessagesReceived.value()));
     	params.put("uid", MyApplication.getInstance().getMember().getMemberMap().get("user_id"));
    	params.put("deviceid", CommonUtil.getMac());
    	HttpManage.getInstance().statistics(params, context);
    }
}
