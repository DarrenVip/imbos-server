package com.imbos.server.push;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.api.ErrorCodeEnum;
import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;

import com.imbos.server.util.DateUtils;
import com.imbos.server.util.JSONUtil;

public class ChatPusher extends Thread{
	
	private JPushClient jpush ;
	
	private static ChatPusher pusher;
	private ChatPusher() {
		String masterSecret= "dcee7c0bc53cd2ebd778e957";
		String appKey = "eb0493b3ae538b3c92528d25";
		long timeToLive = 1000*60;
		jpush = new JPushClient(masterSecret, appKey, timeToLive);
	}
	
	public static ChatPusher instance(){
		synchronized (ChatPusher.class) {
			if(pusher==null)
				pusher = new ChatPusher();
		}
		return pusher;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Map<String,String> data = new HashMap<String, String>();
		data.put("f","181");
		data.put("t","183");
		data.put("c",
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"一二三四五六七八九十" +
				"");
		
		data.put("d",DateUtils.formatDate2Str());
		
		ChatPusher chatPusher = new ChatPusher();
		
		chatPusher.pushMessage(createSendNo(),"183","",JSONUtil.encode(data));
	
	}
	public static int createSendNo(){
		String no =DateUtils.formatDate2Str(new Date(),"ddHHmmss");
		return Integer.parseInt(no);
	}
	
	public boolean pushNotification(int sendNo,String title,String message,int builderId,Map<String, Object> extra) {
		boolean result = false;
		MessageResult msgResult = jpush.sendNotificationWithAppKey(
				sendNo, 
				title, 
				message,
				builderId,
				extra
		);
		if (null != msgResult) {
		    if (msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
		    	result = true;
		        System.out.println("推送服务：发送成功， sendNo=" + msgResult.getSendno());
		    } else {
		        System.out.println("推送服务：发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
		    }
		} else {
		    System.out.println("推送服务：无法获取返回数据");
		}
		return result;
	}
	
	public boolean pushNotification(int sendNo,String alias,String title,String message){
		boolean result = false;
		MessageResult msgResult = jpush.sendNotificationWithAlias(sendNo, alias, title,message);
		if (null != msgResult) {
		    if (msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
		    	result = true;
		        System.out.println("推送服务：发送成功， sendNo=" + msgResult.getSendno());
		    } else {
		        System.out.println("推送服务：发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
		    }
		} else {
		    System.out.println("推送服务：无法获取返回数据");
		}
		return result;
	}
	public  boolean pushMessage(int sendNo,String alias,String title,String message) {
		boolean result = false;
		MessageResult msgResult = jpush.sendCustomMessageWithAlias(sendNo, alias, title,message);
		if (null != msgResult) {
		    if (msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
		    	result = true;
		        System.out.println("推送服务：发送成功， sendNo=" + msgResult.getSendno());
		    } else {
		        System.out.println("推送服务：发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
		    }
		} else {
		    System.out.println("推送服务：无法获取返回数据");
		}
		return result;
	}
	
}
