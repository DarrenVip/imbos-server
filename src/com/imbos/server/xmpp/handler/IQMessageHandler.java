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
package com.imbos.server.xmpp.handler;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import com.imbos.server.model.Message;
import com.imbos.server.service.ServiceLocator;
import com.imbos.server.xmpp.UnauthenticatedException;
import com.imbos.server.xmpp.UnauthorizedException;
import com.imbos.server.xmpp.push.MessageManager;
import com.imbos.server.xmpp.session.ClientSession;
import com.imbos.server.xmpp.session.Session;

/** 
 * This class is to handle the TYPE_IQ jabber:iq:auth protocol.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class IQMessageHandler extends IQHandler {

    private static final String NAMESPACE = "imbos:iq:msg";

    private Element probeResponse;
    private MessageManager messageManager;

    /**
     * Constructor.
     */
    public IQMessageHandler() {
    	
    	messageManager = new MessageManager();
     
    	probeResponse = DocumentHelper.createElement(QName.get("msg",
                NAMESPACE));
        probeResponse.addElement("forms");
        probeResponse.addElement("tos");
        probeResponse.addElement("content");
        probeResponse.addElement("date");
        probeResponse.addElement("id");
    }

    /**
     * Handles the received IQ packet.
     * 
     * @param packet the packet
     * @return the response to send back
     * @throws UnauthorizedException if the user is not authorized
     */
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        IQ reply = null;

        ClientSession session = sessionManager.getSession(packet.getFrom());
        if (session == null) {
            log.error("Session not found for key " + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }

        try {
            Element iq = packet.getElement();
            Element msg = iq.element("msg");
            Element queryResponse = probeResponse.createCopy();

            if (IQ.Type.get == packet.getType()) { // get query
                
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(queryResponse);
                
                if (session.getStatus() != Session.STATUS_AUTHENTICATED) {
                    reply.setTo((JID) null);
                }
                
                
            } else if(IQ.Type.set == packet.getType()){ // set query
            	String id = msg.elementText("id");
                String froms = msg.elementText("froms");
                String tos = msg.elementText("tos");
                String content = msg.elementText("content");
                String date = msg.elementText("date");
                String apiKey = msg.elementText("apiKey");
                
                messageManager.sendMessageToUser(apiKey, id,froms,tos, content, date);
                
                //create replay IQ

                packet.setFrom(session.getAddress());
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setChildElement("content","");
                
             }else if(IQ.Type.result == packet.getType()){
            	log.debug("msg result:"+packet.getChildElement());
             }
        } catch (Exception ex) {
            log.error(ex);
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            if (ex instanceof IllegalArgumentException) {
                reply.setError(PacketError.Condition.not_acceptable);
            } else if (ex instanceof UnauthorizedException) {
                reply.setError(PacketError.Condition.not_authorized);
            } else if (ex instanceof UnauthenticatedException) {
                reply.setError(PacketError.Condition.not_authorized);
            } else {
                reply.setError(PacketError.Condition.internal_server_error);
            }
        }

        // Send the response directly to the session
        if (reply != null) {
            session.process(reply);
        }
        return null;
    }
    
    /**
     * Returns the namespace of the handler.
     * 
     * @return the namespace
     */
    public String getNamespace() {
        return NAMESPACE;
    }

}
