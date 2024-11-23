package com.imbos.server.xmpp.session;

import java.util.List;

import com.imbos.server.model.Message;
import com.imbos.server.service.MessageService;
import com.imbos.server.service.ServiceLocator;
import com.imbos.server.service.UserNotFoundException;
import com.imbos.server.util.Config;
import com.imbos.server.util.DateUtils;
import com.imbos.server.xmpp.push.MessageManager;

public class SimpleSessionListener implements OnSessionListener{
	
	private MessageService messageService;
	MessageManager messageManager;
	public SimpleSessionListener() {
		messageService = ServiceLocator.getMessageService();
		messageManager = new MessageManager();
	}
	
	@Override
	public void onCreate(ClientSession clientSession) {
		
		try {
			String uid = clientSession.getUsername();
			
			System.out.println("建立会话"+uid);
			String apiKey = Config.getString("apiKey", "");
			
			
			List<Message> list = messageService.getMessagesByUser(uid);
			for (Message message : list) {
				messageManager.sendMessageToUser(apiKey, message.getId(),
						message.getFroms(), message.getTos(), message
								.getContent(), DateUtils.formatDate2Str(message
								.getCreatedDate()));
			}
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onClose(ClientSession clientSession) {
		try {
			System.out.println("关闭会话"+clientSession.getUsername());
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
