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
package com.imbos.server.service;

import java.util.List;

import com.imbos.server.model.Message;

/** 
 * Business service interface for the message management.
 *
 * @author xianze
 */
public interface MessageService {

	public Message getMessage(Long id);

    public Message saveMessage(Message msg);

    public void removeMessage(Long id);

    public boolean exists(Long id);

    public List<Message> getMessages();
    
    public List<Message> getMessagesByUser(String uid);
    
    public void updateReceiveDateById(String id,String receiveDate);
}
