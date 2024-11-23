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
package com.imbos.server.service.impl;

import java.util.List;

import javax.persistence.EntityExistsException;

import com.imbos.server.dao.MessageDao;
import com.imbos.server.dao.UserDao;
import com.imbos.server.model.Message;
import com.imbos.server.model.User;
import com.imbos.server.service.MessageService;
import com.imbos.server.service.UserExistsException;
import com.imbos.server.service.UserNotFoundException;
import com.imbos.server.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

/** 
 * This class is the implementation of MessageService.
 *
 * @author xianze
 */
public class MessageServiceImpl implements MessageService {

    protected final Log log = LogFactory.getLog(getClass());

    private MessageDao messageDao;

	public boolean exists(Long id) {
		return messageDao.exists(id);
	}

	public Message getMessage(Long id) {
		return messageDao.getMessage(id);
	}

	public List<Message> getMessages() {
		return messageDao.getMessages();
	}

	public void removeMessage(Long id) {
		messageDao.removeMessage(id);
	}

	public Message saveMessage(Message msg) {
		return messageDao.saveMessage(msg);
	}

	public void updateReceiveDateById(String id, String receiveDate) {
		this.messageDao.updateReceiveDateById(id, receiveDate);
	}
	public MessageDao getMessageDao() {
		return messageDao;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	@Override
	public List<Message> getMessagesByUser(String tos) {
		return this.messageDao.getMessagesByUser(tos);
	}

}
