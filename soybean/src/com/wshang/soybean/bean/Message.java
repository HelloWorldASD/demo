package com.wshang.soybean.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;


// 建议加上注解， 混淆后表名不受影响
@Table(name = "message")
public class Message extends EntityBase {
//
//    @Column(column = "id")
//    public int id;
    
    @Column(column = "messageId") // 建议加上注解， 混淆后列名不受影响
    public String messageId;
    
    @Column(column = "pushId") 
    public String pushId;
    
    @Column(column = "face")
    private String face;
    
    @Column(column = "nickName")
    private String nickName;

    @Column(column = "type")
    private int type;

    @Column(column = "url")
    private String url;
    
    @Column(column = "title")//标题
    private String title;

    @Column(column = "content")
    private String content;
    
    @Column(column = "read")//查看与否0未查看，1查看
    private int read;
    
    @Column(column = "time")//时间
    private long time;
    
	public String getPushId() {
		return pushId;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
