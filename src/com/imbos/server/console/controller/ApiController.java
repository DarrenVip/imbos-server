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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.imbos.server.model.User;
import com.imbos.server.push.ChatPusher;
import com.imbos.server.service.ServiceLocator;
import com.imbos.server.service.UserService;
import com.imbos.server.util.Config;
import com.imbos.server.util.DateUtils;
import com.imbos.server.util.JSONUtil;
import com.imbos.server.util.PropertiesUtil;
import com.imbos.server.util.json.JSONException;
import com.imbos.server.xmpp.presence.PresenceManager;
import com.imbos.server.xmpp.push.MessageManager;

/** 
 * A controller class to process the notification related requests.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ApiController extends MultiActionController {

    private MessageManager messageManager;
    private UserService userService;
    private PresenceManager presenceManager;
    private String fileDir;
    private ChatPusher chatPusher;
    
    public ApiController() {
    	messageManager = new MessageManager();
    	presenceManager = new PresenceManager();
    	userService = ServiceLocator.getUserService();
    	fileDir = PropertiesUtil.getFileDirPath();
    	chatPusher = ChatPusher.instance();
    }
    public void login (HttpServletRequest request,HttpServletResponse response) throws Exception {
    	String jsonStr = readText(request);
    	logger.debug("ApiController:"+jsonStr);
    	try{
	    	Map<String,String> json = (Map<String,String>) JSONUtil.decode(jsonStr);
	    	
	    	String username = json.get("username");
	    	String password = json.get("password");
	    	
	    	User user= userService.getUserByUsername(username);
	    	if(user!=null && password.equals(user.getPassword()))
	    		writeText(response, "{resultCode:0}");
	    	else
	    		writeText(response, "{resultCode:500}");
    	}catch (Exception e) {
    		e.printStackTrace();
    		writeText(response, "{resultCode:400}");
		}
    }
    public void register (HttpServletRequest request,HttpServletResponse response) throws Exception {
    	String jsonStr = readText(request);
    	logger.debug("ApiController:"+jsonStr);
    	try{
	    	Map<String,String> json = (Map<String,String>) JSONUtil.decode(jsonStr);
	    	
	    	String username = json.get("username");
	    	String password = json.get("password");
	        String email = json.get("email");
	        String name = json.get("username");
	        String imei = json.get("imei");
	        
	        User user = new User();
	        user.setUsername(username);
	        user.setPassword(password);
	        user.setEmail(email);
	        user.setName(name);
	        userService.saveUser(user);
	        writeText(response, "{resultCode:0}");
    	}catch (Exception e) {
			throw e;
		}
    }
    
    
    public void send(HttpServletRequest request,HttpServletResponse response)
    	throws Exception {
    	
    	logger.debug("ApiController.send()");
    	
    	String jsonStr = readText(request);
    	logger.debug("ApiController.send:"+jsonStr);
    	try{
	    	Map<String,String> json = (Map<String,String>) JSONUtil.decode(jsonStr);
	    	
	    	String from = json.get("from");
	    	String to = json.get("to");;
	        String content = json.get("content");;
	        String date = DateUtils.formatDate2Str();
	        String apiKey = Config.getString("apiKey", "");
	        String id = System.currentTimeMillis()+"";
	        pushMessage(apiKey, id, from, to, content, date);
	        writeText(response, "{resultCode:0}");
    	}catch (Exception e) {
			//throw e;
    		writeText(response, "{resultCode:-1}");
        }
     }
     
     public void sendFile(HttpServletRequest request,HttpServletResponse response)
     	throws Exception {
    	 //request.gets.getRealPath(String.valueOf(File.separatorChar));
    	 String fileName = request.getHeader("fileName");
    	 long validSize = request.getContentLength();
    	 
    	 File file = new File(fileDir,fileName);
    	 if(!file.exists())
    		 file.createNewFile();
    	 
    	 InputStream is =  request.getInputStream();
    	 FileOutputStream fos = new FileOutputStream(file);
    	 byte[] buffer = new byte[1024];
    	 int readLen=0;
    	 while ((readLen=is.read(buffer))!=-1){
			fos.write(buffer,0,readLen);
		 }
    	 
    	 fos.close();
    	 writeText(response, "{resultCode:0;name:\""+fileName+"\";link:\"/api.do?action=download&fileName="+fileName+"\"}");
     }
     
     public void download(HttpServletRequest request,HttpServletResponse response)
  		throws Exception {
    	
    	String fileName =request.getParameter("fileName");
    	
    	response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
    	
    	FileInputStream fis = new FileInputStream(new File(fileDir,fileName));
    	ServletOutputStream  sos = response.getOutputStream();
		 byte[] buffer = new byte[1024];
	   	 int readLen=0;
	   	 while ((readLen=fis.read(buffer))!=-1){
	   		sos.write(buffer,0,readLen);
		 }
	   	sos.flush();
    }
     
    /**
     * send to users
     * @param apiKey
     * @param id
     * @param form
     * @param to
     * @param content
     * @param date
     */
	private void sendMessage(String apiKey, String id, String from, String to,
			String content, String date) {
		
		String[] users = to.indexOf(";")!=-1?to.split(";"):new String[]{to};
		
		for (String user : users) {
			int resultCode = messageManager.sendMessageToUser(apiKey, id, from,user,
					content,date);
		    if(resultCode==MessageManager.SEND_FAILED){
		    	logger.info("user:"+user+" offline");
		    }
		}
	}
	/**
     * send to users
     * @param apiKey
     * @param id
     * @param form
     * @param to
     * @param content
     * @param date
	 * @throws Exception 
     */
	private boolean pushMessage(String apiKey, String id, String from, String to,
			String content, String date) throws Exception {
		
		boolean result=false;
		String[] users = to.indexOf(";")!=-1?to.split(";"):new String[]{to};
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("f", from);
		map.put("t", to);
		map.put("c", content);
		map.put("d", DateUtils.formatDate2Str());
		boolean temp;
		for (String user : users) {
			map.put("t",user);
			temp = chatPusher.pushMessage(ChatPusher.createSendNo(),user,"",JSONUtil.encode(map));
			if(!result)
				result = temp;
		}
		return result;
	}
	
    
    
    public void list(HttpServletRequest request,HttpServletResponse response)
		throws Exception {
    	List<User> users= userService.getUsers();
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	List<List<String>> table =  new ArrayList<List<String>>();
    	table.add(Arrays.asList(new String[]{"username","online"}));
    	List<String> row;
    	for (User user : users) {
    		if (presenceManager.isAvailable(user)) {
                user.setOnline(true);
            } else {
                user.setOnline(false);
            }
    		row = new ArrayList<String>();
    		row.add(user.getUsername());    
    		row.add(user.isOnline()+"");
    		table.add(row);
		}
    	result.put("resultCode","0");
    	result.put("data",table);
    	writeText(response, JSONUtil.encode(result));
    }
    
    
    protected void writeText(HttpServletResponse response,String text) throws IOException{
    	 response.setContentType("text/javascript;charset=UTF-8");
         response.setHeader("Cache-Control","no-store, max-age=0, no-cache, must-revalidate");     
         response.addHeader("Cache-Control", "post-check=0, pre-check=0"); 
         response.setHeader("Pragma", "no-cache");  
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(text);
    }
    
    protected String readText(HttpServletRequest request) throws IOException{
    	String result;
    	InputStream is =  request.getInputStream();
    	ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
    	byte[] buffer = new byte[1024];
    	int readLen = 0;
    	while ((readLen=is.read(buffer))!=-1) {
			os.write(buffer,0,readLen);
		}
    	os.flush();
    	result = new String(os.toByteArray(),request.getCharacterEncoding());
    	os.close();
    	return result;
    }
}
