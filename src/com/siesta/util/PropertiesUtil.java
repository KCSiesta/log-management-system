package com.siesta.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtil {

	public static String getValue(String key){
		//读取配置文件
		Properties prop=new Properties();
		//读取指定位置的配置文件
		InputStream in=new PropertiesUtil().getClass().getResourceAsStream("/diary.properties");
		try {
			prop.load(in);//加载配置文件
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (String)prop.get(key);
	}
}
