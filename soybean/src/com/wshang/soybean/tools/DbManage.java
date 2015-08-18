package com.wshang.soybean.tools;

import java.util.List;
import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import com.wshang.soybean.bean.Message;
import com.wshang.soybean.bean.ReadType;

public class DbManage
{
	private static DbManage manage;
	private DbUtils db;
	private int pageSize = 10;
	//本地存储的消息最多为200条
	private int maxCount = 200;
	
	private DbManage (Context ctx)
	{
		DaoConfig config = new DaoConfig(ctx);
		config.setDbName("soybean"); //db名
		config.setDbVersion(1);  //db版本
		
		db = DbUtils.create(config);//db还有其他的一些构造方法，比如含有更新表版本的监听器的
		db.configAllowTransaction(true);
		try {
			db.createTableIfNotExist(Message.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public int save(Object obj)
	{
		try {
			if (getMessageCount() >= maxCount)
			{
				deleteOldMessage(getOlderMessageId());
			}
			
			db.saveOrUpdate(obj);

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getRecentlyId();
	}
	
	public List<Message> getMessagePageList(int pageIndex)
	{
		List<Message> messageList = null;
		try {
			messageList = db.findAll(Selector.from(Message.class).orderBy("id", true).limit(pageSize).offset(pageSize * pageIndex));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return messageList;
	}
	
	public int getMessageCount()
	{
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = db.execQuery("select count(0) as messageCount from message");
			while (cursor.moveToNext()) {
				count = cursor.getInt(0);
				break;
			}
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
				cursor.close();
		}
		return count;
	}
	
	public int getRecentlyId()
	{
		Message mess = null;
		try {
			mess = db.findFirst(Selector.from(Message.class).limit(1).orderBy("id", true));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return mess.getId();
	}
	
	public int getOlderMessageId()
	{
		Message mess = null;
		try {
			mess = db.findFirst(Selector.from(Message.class).limit(1).orderBy("id"));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return mess.getId();
	}
	
	public Message getMessageById(int id)
	{
		Message mess = null;
		try {
			mess = db.findById(Message.class, id);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return mess;
	}
	
	public boolean deleteMessage(Message mess)
	{
		try 
		{
			db.delete(mess);
			return true;
		} 
		catch (DbException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public void deleteOldMessage(int olderMessageId)
	{
		try 
		{
			db.deleteById(Message.class, olderMessageId);
		} 
		catch (DbException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getUnReadMessageCount()
	{
		int unReadCount = 0;
		try {
			DbModel model = db.findDbModelFirst(Selector.from(Message.class).where("read" ,"=", String.valueOf(ReadType.UN_READ.ordinal())).select("count(0) as unReadCount"));
			unReadCount = model.getInt("unReadCount");
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		return unReadCount;
	}
	
	public static DbManage getInstance(Context ctx)
	{
		if (manage == null)
			manage = new DbManage(ctx);
		
		return manage;
	}
}
