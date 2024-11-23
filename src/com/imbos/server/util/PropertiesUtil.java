package com.imbos.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * 读取类路径下的属性文件，返回一个Properties
 * 
 * @author white
 * 
 */
public class PropertiesUtil {

	// 类路径对应的file
	private static final File CLASS_FILE = FileUtils
			.toFile(PropertiesUtil.class.getResource("/"));

	/**
	 * 读取类路径下的某个属性配置文件
	 * @param filename 属性文件名
	 * @return
	 */
	public static Properties readProperties(String filename) {
		System.out.println("CLASS_FILE:"+CLASS_FILE);
		try {
			InputStream is = new FileInputStream(new File(CLASS_FILE, filename));
			Properties properties = new Properties();
			properties.load(is);
			is.close();
			return properties;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取类路径的字符串表示
	 * @return
	 */
	public static String getWebRoot() {
		return CLASS_FILE.getAbsolutePath();
	}
	public static String getFileDirPath(){
		File dir = new File(getWebRoot(),"files");
		if(!dir.exists()){dir.mkdirs();}
		return dir.getPath();
	}
	public static void main(String[] args) {
		System.out.println(getWebRoot());
		System.out.println(File.separatorChar);
	}
}
