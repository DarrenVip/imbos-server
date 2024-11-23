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
package com.imbos.server.dao.hibernate;

import java.util.List;

import com.imbos.server.dao.MessageDao;
import com.imbos.server.dao.UserDao;
import com.imbos.server.model.Message;
import com.imbos.server.model.User;
import com.imbos.server.service.UserNotFoundException;
import com.imbos.server.util.DateUtils;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** 
 * This class is the implementation of UserDAO using Spring's HibernateTemplate.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class MessageDaoHibernate extends HibernateDaoSupport implements MessageDao {

   

   
	public Message getMessage(Long id) {
		return (Message) getHibernateTemplate().get(Message.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages() {
		 return getHibernateTemplate().find(
         	"from Message m order by m.date desc");
	}

	public void removeMessage(Long id) {
		getHibernateTemplate().delete(getMessage(id));
	}

	public Message saveMessage(Message msg) {
		getHibernateTemplate().saveOrUpdate(msg);
        getHibernateTemplate().flush();
        return msg;
	}
	public void updateReceiveDateById(String id, String receiveDate) {
		Message m =(Message) this.getHibernateTemplate().get(id,Message.class);
		m.setCreatedDate(DateUtils.formatStr2Date(receiveDate));
		this.getHibernateTemplate().update(m) ;
	}
	public boolean exists(Long id) {
		
		return false;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Message> getMessagesByUser(String tos) {
		return getHibernateTemplate().find(
				"from Message m where m.tos=? order by m.createdDate asc",tos);
	}

  

}
