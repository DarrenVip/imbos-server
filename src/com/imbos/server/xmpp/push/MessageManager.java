/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.imbos.server.xmpp.push;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import com.imbos.server.model.Message;
import com.imbos.server.service.MessageService;
import com.imbos.server.service.ServiceLocator;
import com.imbos.server.util.DateUtils;
import com.imbos.server.xmpp.session.ClientSession;
import com.imbos.server.xmpp.session.SessionManager;

/** 
 * This class is to manage sending the message to the users.  
 *
 * @author xianze
 */
public class MessageManager {

    private static final String MESSAGE_NAMESPACE = "imbos:iq:msg";
    private static final String MESSAGE_NAME = "msg";
    
    public static final int SEND_FINISH = 1;
    public static final int SEND_FAILED = 0;

    private final Log log = LogFactory.getLog(getClass());

    private SessionManager sessionManager;
    private MessageService messageService;
    /**
     * Constructor.
     */
    public MessageManager() {
        sessionManager = SessionManager.getInstance();
        messageService = ServiceLocator.getMessageService();
    }

    /**
     * 
     * @param apiKey
     * @param id
     * @param from
     * @param to
     * @param content
     * @param date
     */
    public void sendBroadcast(String apiKey,String id,String from, String to,
    		String content,String date
            ) {
        log.debug("sendBroadcast()...");
        IQ messageIQ = createMessageIQ(apiKey, id, from, to, content, date);
        
        for (ClientSession session : sessionManager.getSessions()) {
            if (session.getPresence().isAvailable()) {
            	messageIQ.setTo(session.getAddress());
                session.deliver(messageIQ);
            }
        }
    }

   /**
    * 函数功能说明 
    * miracre 2013-3-10
    * 修改者名字 
    * 修改日期 
    * 修改内容
    * @param apiKey
    * @param id
    * @param form
    * @param to
    * @param content
    * @param date
    * @return
    */
    public int sendMessageToUser(String apiKey,String id,String from, String to,
    		String content,String date) {
    	int result = SEND_FAILED;
        log.debug("sendMessageToUser()...");
        
        Message msg =new Message();
    	msg.setId(id);
    	msg.setFroms(from);
    	msg.setTos(to);
    	msg.setContent(content);
    	msg.setCreatedDate(DateUtils.formatStr2Date(date));
    	
        ClientSession session = sessionManager.getSession(to);
        if (session != null) {
            if (session.getPresence().isAvailable()) {
            	IQ messageIQ = createMessageIQ(apiKey, id, from, to,content,DateUtils.formatDate2Str());
                messageIQ.setTo(session.getAddress());
                session.deliver(messageIQ);
                result = SEND_FINISH;
                msg.setReceiveDate(new Date());
            }
            
        }else{//用户不在线
        	result = SEND_FAILED;
        }
        messageService.saveMessage(msg);
    	return result;
    }

    /**
     * Creates a new message IQ and returns it.
     */
    public IQ createMessageIQ(String apiKey,String id,String from, String to,
    		String content,String date) {
        
        Element message = DocumentHelper.createElement(QName.get(
        		MESSAGE_NAME, MESSAGE_NAMESPACE));
        message.addElement("id").setText(id);
        message.addElement("apiKey").setText(apiKey);
        message.addElement("froms").setText(from);
        message.addElement("tos").setText(to);
        message.addElement("content").setText(content);
        message.addElement("date").setText(date);
        
        IQ iq = new IQ();
        iq.setType(IQ.Type.set);
        iq.setChildElement(message);

        return iq;
    }
}
