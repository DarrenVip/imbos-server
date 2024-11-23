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
package com.imbos.server.console.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imbos.server.util.Config;
import com.imbos.server.util.DateUtils;
import com.imbos.server.xmpp.push.MessageManager;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * A controller class to process the notification related requests.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class MessageController extends MultiActionController {

    private MessageManager messageManager;
   
    public MessageController() {
    	messageManager = new MessageManager();
    }

    public void send(HttpServletRequest request,HttpServletResponse response)
    	throws Exception {
    	
    	logger.debug("MessageController.send()");
        
    	String form = ServletRequestUtils.getStringParameter(request,
                "form", "admin");
        String to = ServletRequestUtils.getStringParameter(request,
                "to","123456");
        String content = ServletRequestUtils.getStringParameter(request,
                "content","hello");
        String date = DateUtils.formatDate2Str();

        String apiKey = Config.getString("apiKey", "");
        String id = System.currentTimeMillis()+"";
        logger.debug("apiKey=" + apiKey);

        messageManager.sendMessageToUser(apiKey, id, form, to, content, date);
        
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("SUCCESS");
        
    }
    
}
