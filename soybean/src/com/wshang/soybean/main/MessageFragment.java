package com.wshang.soybean.main;

import java.util.List;
import com.wshang.soybean.R;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.ReadType;
import com.wshang.soybean.message.MessageAdapter;
import com.wshang.soybean.news.EssenceInfo;
import com.wshang.soybean.news.InfoArticleDetail;
import com.wshang.soybean.news.TextInfoArticleDetail;
import com.wshang.soybean.tools.DbManage;
import com.wshang.soybean.tools.HttpAction;
import com.wshang.soybean.tools.HuiConstants;
import com.wshang.soybean.tools.LoadingProgress;
import com.wshang.soybean.tools.StringUtil;
import com.wshang.soybean.view.HorizontalSlidingListView;
import com.umeng.analytics.MobclickAgent;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView.OnScrollListener;

/**
 * 消息列表
 * @author liangpeng
 *
 */
public class MessageFragment extends Fragment
{
	private HorizontalSlidingListView  lvMessage;
	private MessageAdapter messageAdapter = null;
	private List<Message> messageList;
    
    private boolean isLoad = false;
    private boolean loadState = false;
    private boolean isFinish = false;
	private int currentPage = 0;
	private Dialog loading = null;
	
	private View messageLayout;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		messageLayout = inflater.inflate(R.layout.message_list,  
	                container, false);  
		
		initView();
		getMessageList(false);
		MessageFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.PUSH_MESSAGE));
		MessageFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(HttpAction.MESSAGE_READ));
		return messageLayout;
	}
	
	private void initView()
	{
		TextView txtTitle = (TextView) messageLayout.findViewById(R.id.txtTitle);
		txtTitle.setText("消息");
        loading = new LoadingProgress(MessageFragment.this.getActivity(), R.style.LoadDialog, "");
        
        lvMessage = (HorizontalSlidingListView) messageLayout.findViewById(R.id.lvMessage);
        lvMessage.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& isLoad == false
					&& loadState == true
					&& isFinish == false)
				{
					currentPage = currentPage + 1;
					loading.show();
					getMessageList(true);	
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int _visibleItemCount, int totalItemCount) {
			 	loadState = false;
                if ((firstVisibleItem + _visibleItemCount) == totalItemCount)
                {
                	loadState = true;
                }
			}
		});
		
        lvMessage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0,
					View arg1, int index, long arg3)
			{
				Message item = (Message) messageAdapter.getItem(index);
				int type = item.getType();
				
				Intent ittInfo = null;
				switch (type)
                {
					case 1://短文
						ittInfo = new Intent(MessageFragment.this.getActivity(), InfoArticleDetail.class);
						break;
					case 2://长文
						ittInfo = new Intent(MessageFragment.this.getActivity(), EssenceInfo.class);
						ittInfo.putExtra("title", item.getTitle());
						ittInfo.putExtra("url", item.getUrl());
						break;
					case 3://文本内容
						if (StringUtil.isEmpty(item.getUrl()))
						{
							ittInfo = new Intent(MessageFragment.this.getActivity(), TextInfoArticleDetail.class);
							ittInfo.putExtra("content", item.getContent());
						}
						else 
						{
							ittInfo = new Intent(MessageFragment.this.getActivity(), WebActivity.class);
							ittInfo.putExtra("url", item.getUrl());
							ittInfo.putExtra("title", item.getTitle());
						}
						break;
					default:
						break;
                }
				ittInfo.putExtra("messageId", item.getMessageId());
				ittInfo.putExtra("pushId", item.getPushId());
				ittInfo.putExtra("id", item.getId());
				ittInfo.putExtra("fromPush", true);
				MessageFragment.this.startActivity(ittInfo);
			}
		});
		messageAdapter = new MessageAdapter(MessageFragment.this.getActivity(), messageList, lvMessage.getRightViewWidth());
        messageAdapter.setOnRightItemClickListener(new MessageAdapter.onRightItemClickListener() {
            @Override
            public void onRightItemClick(View v, int position) {
            	Message mess = (Message) messageAdapter.getItem(position);
        	    boolean result = DbManage.getInstance(MessageFragment.this.getActivity()).deleteMessage(mess);
        	    if (result == true)
        	    {
        			List<Message> messList = messageAdapter.getData();
        			messList.remove(position);
        			lvMessage.hiddenRight(lvMessage.getmCurrentItemView());
        			messageAdapter.notifyDataSetChanged();
        	    }
            }
        });
		lvMessage.setAdapter(messageAdapter);
	}
	
	private void getMessageList(boolean isAdd)
	{
		messageList = DbManage.getInstance(MessageFragment.this.getActivity()).getMessagePageList(currentPage);
		messageAdapter.setData(messageList, isAdd);
		messageAdapter.notifyDataSetChanged();
		
		if (messageList == null || messageList.size() < HuiConstants.PAGE_SIZE)
			isFinish = true;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart(getResources().getString(R.string.main_message));
		MobclickAgent.onPause(MessageFragment.this.getActivity());       //统计时长
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageEnd(getResources().getString(R.string.main_message));
		MobclickAgent.onResume(MessageFragment.this.getActivity());       //统计时长
	}


	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MessageFragment.this.getActivity().unregisterReceiver(receiver);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null)
			{
				String action = intent.getAction();
				if (HttpAction.PUSH_MESSAGE.equals(action))
				{
				     int type = intent.getIntExtra("type", 0);
                     String url = intent.getStringExtra("url");
                     String face = intent.getStringExtra("face");
                     String nickname = intent.getStringExtra("nickname");
                     int id = intent.getIntExtra("id", -1);
                     String messageId = intent.getStringExtra("messageId");
                     String pushId = intent.getStringExtra("pushId");
                     String title = intent.getStringExtra("title");
                     String content = intent.getStringExtra("content");
                     long time = intent.getLongExtra("time", 0);
                     
                     Message mess = new Message();
                     mess.setContent(content);
                     mess.setFace(face);
                     mess.setNickName(nickname);
                     mess.setMessageId(messageId);
                     mess.setPushId(pushId);
                     mess.setTitle(title);
                     mess.setTime(time);
                     mess.setUrl(url);
                     mess.setType(type);
                     mess.setId(id);
                     messageAdapter.getData().add(0, mess);
                     messageAdapter.notifyDataSetChanged();
				}
				else if (HttpAction.MESSAGE_READ.equals(action))
				{
					 int id = intent.getIntExtra("id", -1);
					 updateMessageState(id);
				}
			}
		}
	};
	
	private void updateMessageState(int id)
	{
		 List<Message> messList = messageAdapter.getData();
		 if (messageList != null)
		 {
			 for (Message mess : messList)
			 {
				 if (mess.getId() == id)
				 {
					 mess.setRead(ReadType.READ.ordinal());
					 break;
				 }
			 }
			 messageAdapter.setData(messList);
             messageAdapter.notifyDataSetChanged();
		 }
	}
}