package com.imbos.server.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.imbos.server.util.json.JSONArray;
import com.imbos.server.util.json.JSONException;
import com.imbos.server.util.json.JSONObject;
import com.imbos.server.util.json.JSONStringer;



public final class JSONUtil  {
	
	@SuppressWarnings("unchecked")
	public  void encode(Object obj,JSONStringer stringer)throws JSONException
	{
		obj=obj==null?"":obj;
		if(obj instanceof Map){
			encodeMap((Map)obj, stringer);
		}else if(obj instanceof List){
			encodeArray((List)obj, stringer);
		}else if(obj!=null && obj.getClass().isArray()){
			encodeArray(obj, stringer);
		}else{
			stringer.value(obj);
		}
	}
	@SuppressWarnings("unchecked")
	private  void encodeMap(Map map,JSONStringer stringer) throws JSONException
	{
		Collection collection= map.keySet();
		Iterator iterator= collection.iterator();
		stringer.object();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			stringer.key(key);
			Object value = map.get(key);
			encode(value, stringer);
		}
		stringer.endObject();
	}
	private  void encodeArray(Object array,JSONStringer stringer)throws JSONException
	{
		if(array==null)
			return;
		stringer.array();
			for (int i = 0; i < Array.getLength(array); i++) {
				encode(Array.get(array, i), stringer);	
			}
		stringer.endArray();
	}
	@SuppressWarnings("unchecked")
	private  void encodeArray(List array,JSONStringer stringer)throws JSONException
	{
		if(array==null)
			return;
		stringer.array();
		for (int i = 0; i < array.size(); i++) {
			encode(array.get(i), stringer);
		}
		stringer.endArray();
	}
	public static String encode(Object object) throws JSONException
	{
		JSONStringer stringer = new JSONStringer();
		new JSONUtil().encode(object, stringer);
		return stringer.toString();
	}
	public static Object decode(String source) throws JSONException
	{
		JSONUtil util = new JSONUtil();
		if('{'==source.trim().charAt(0))
		 return util.decodeJSONObject(new JSONObject(source));
		else
		 return util.decodeJSONArray(new JSONArray(source));
		
	}
	private  Object decode(Object obj) throws JSONException
	{
		if(obj instanceof JSONObject)
			return decodeJSONObject((JSONObject)obj);
		else if(obj instanceof JSONArray) 
			return decodeJSONArray((JSONArray)obj);
		else 
			return obj;
	}
	@SuppressWarnings({ "unchecked"})
	private Hashtable decodeJSONObject(JSONObject obj) throws JSONException{
		String[] keys = JSONObject.getNames(obj);
		Hashtable hashtable = new Hashtable();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i].toString();
			Object temp = obj.get(key);
			Object value = this.decode(temp);
			hashtable.put(keys[i],value);
		}
		return hashtable;
	}
	@SuppressWarnings("unchecked")
	private List decodeJSONArray(JSONArray array) throws JSONException{
		List list = new ArrayList();
		for (int i = 0; i < array.length(); i++) {
			Object value = this.decode(array.get(i));
			list.add(value);
		}
		return list;
	}
	
	
	public static void main(String[] args) throws Exception {
		Map<String, Object> map = new Hashtable<String, Object>();
		map.put("1", 1);
		map.put("2", "2");
		map.put("3", true);
		map.put("4", 1.21f);
		Map<String, Object> submap = new Hashtable<String, Object>();
		submap.put("submap1", 1);
		submap.put("submap2", "2");
		submap.put("submap3", true);
		submap.put("submap4", 1.21f);
		//map.put("subMap", submap);
		List<Object> list = new ArrayList<Object>();
		list.add("list1");
		list.add("list2");
		list.add("list3");
		list.add("list4");
		map.put("list", list);
		List<String> list2 = new ArrayList<String>();
		list2.add("list21");
		list2.add("list22");
		list2.add("list23");
		list.add(list2);
		String[] str = new String[]{"str1","str2","str3"};
		List<Map> list3 = new ArrayList<Map>();
		list3.add(map);
		map.put("array", str);
		//System.out.println(array.getClass());
		String json = JSONUtil.encode(map);
		System.out.println("json="+json);
	    long st = System.currentTimeMillis();
		Hashtable hashtable = (Hashtable) JSONUtil.decode(json);
		System.out.println(System.currentTimeMillis()-st);
		Enumeration en = hashtable.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			Object value = hashtable.get(key);
			System.out.println(key+"="+value+"  ----type="+value.getClass());
		}
	}
}
